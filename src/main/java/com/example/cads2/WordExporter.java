package com.example.cads2;

import com.example.cads2.Connector;
import com.example.cads2.HardeningResult;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WordExporter {
    private final Stage primaryStage;

    public WordExporter(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void export(HardeningResult result, HardeningParameters parameters, String author, String projectName, LocalDate date) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить операционную карту");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Документ Word (*.docx)", "*.docx")
        );
        fileChooser.setInitialFileName("операционная_карта.docx");

        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            writeToFile(result,parameters, author, projectName, date, file.getAbsolutePath());
            System.out.println("Файл успешно сохранён: " + file.getAbsolutePath());
        } else {
            System.out.println("Сохранение отменено.");
        }
    }

    private void writeToFile(HardeningResult result, HardeningParameters parameters, String author, String projectName, LocalDate date, String filePath) {
        try (XWPFDocument document = new XWPFDocument()) {
            // Заголовок
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("Операционная карта");
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.setFontFamily("Times New Roman");

            // Информация об авторе и проекте
            XWPFParagraph infoParagraph = document.createParagraph();
            XWPFRun infoRun = infoParagraph.createRun();
            infoRun.setFontSize(14);
            infoRun.setFontFamily("Times New Roman");
            infoRun.addBreak();
            infoRun.setText("Автор: " + author);
            infoRun.addBreak();
            infoRun.setText("Название проекта: " + projectName);
            infoRun.addBreak();
            infoRun.setText("Дата проектирования: " + date.toString());
            infoRun.addBreak();
            infoRun.setText("Тип детали: " + parameters.getPartName()) ;
            infoRun.addBreak();
            infoRun.setText("Марка стали: " + parameters.getSteelGrade());
            infoRun.addBreak();
            infoRun.addBreak();

            // Таблица операций
            XWPFTable table = document.createTable();
            table.setWidth("100%");

            // Заголовок таблицы
            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText("№ п/п");
            headerRow.addNewTableCell().setText("Наименование операции");
            headerRow.addNewTableCell().setText("Содержание операции");
            headerRow.addNewTableCell().setText("Технологические параметры");

            List<String[]> operations = loadOperationsWithParams(result, parameters);
            int index = 1;
            for (String[] operation : operations) {
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(String.valueOf(index++));
                row.getCell(1).setText(operation[0]);
                row.getCell(2).setText(operation[1]);
                row.getCell(3).setText(operation[2]);
            }

            try (FileOutputStream out = new FileOutputStream(filePath)) {
                document.write(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String[]> loadOperationsWithParams(HardeningResult result, HardeningParameters parameters) {
        List<String[]> operations = new ArrayList<>();

        try (Connection conn = Connector.getConnection()) {
            String query = "SELECT * FROM operation_card WHERE part_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, parameters.getPartName());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        operations.add(new String[]{
                                "Подготовка поверхности",
                                rs.getString("prep_surface"),
                                "—"
                        });
                        operations.add(new String[]{
                                "Установка детали",
                                rs.getString("setup"),
                                "—"
                        });
                        operations.add(new String[]{
                                "Индукционный нагрев",
                                rs.getString("induction_heating"),
                                String.format("Температура: %.1f°C%nЧастота: %.1f кГц%nПолная мощность: %.1f кВт%nСкорость перемещения: %.1f мм/с%nРекомендуемый генератор ТВЧ: %s",
                                        Double.valueOf(result.getHeatingTemperature()),
                                        result.getFrequency(),
                                        result.getPower(),
                                        result.getDetailSpeed(),
                                        result.getGenerator().getType())
                        });
                        operations.add(new String[]{
                                "Закалка (охлаждение после нагрева)",
                                rs.getString("quenching"),
                                "Среда охлаждения: " + result.getCoolingMedium()
                        });
                        operations.add(new String[]{
                                "Отпуск",
                                rs.getString("tempering"),
                                "Температура отпуска: " + result.getTemperingTemperature() + " °C"
                        });
                        operations.add(new String[]{
                                "Охлаждение после отпуска",
                                rs.getString("post_tempering_cooling"),
                                "—"
                        });
                        operations.add(new String[]{
                                "Контроль твёрдости",
                                rs.getString("hardness_control"),
                                String.format("Ожидаемая твердость: %d - %d%nОжидаемая глубина закалки: %.1f",
                                        result.getHardness().min,
                                        result.getHardness().max,
                                        parameters.getQuenchDepths())
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return operations;
    }
}
