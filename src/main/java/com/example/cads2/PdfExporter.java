package com.example.cads2;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class PdfExporter {
    private final Stage primaryStage;

    public PdfExporter(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void export(HardeningResult result, HardeningParameters parameters, String author, String projectName, LocalDate date) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить PDF-документ");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF файл (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("операционная_карта.pdf");

        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            writeToPdf(result, parameters, author, projectName, date, file.getAbsolutePath());
        }
    }

    private void writeToPdf(HardeningResult result, HardeningParameters parameters, String author, String projectName, LocalDate date, String filePath) {
        try {
            // Загрузка шрифта из ресурсов
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream("C:/Users/Руслан/Desktop/Шрифт/timesnewromanpsmt.ttf");
            if (fontStream == null) {
                throw new RuntimeException("Шрифт не найден по пути ресурсов");
            }

            // Читаем байты шрифта (Java 11+)
            byte[] fontBytes = fontStream.readAllBytes();

            // Создаем FontProgram из байтов
            FontProgram fontProgram = FontProgramFactory.createFont(fontBytes);

            // Создаем PdfFont с встраиванием и кодировкой
            PdfFont font = PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H);

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Заголовок
            Paragraph title = new Paragraph("Операционная карта")
                    .setFont(font)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Информация о проекте
            document.add(new Paragraph()
                    .setFont(font)
                    .setFontSize(12)
                    .add("Автор: " + author + "\n")
                    .add("Название проекта: " + projectName + "\n")
                    .add("Дата проектирования: " + date + "\n")
                    .add("Тип детали: " + parameters.getPartName() + "\n")
                    .add("Марка стали: " + parameters.getSteelGrade() + "\n\n")
            );

            // Таблица
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 6, 4}))
                    .setWidth(UnitValue.createPercentValue(100));

            addHeaderCell(table, "№ п/п", font);
            addHeaderCell(table, "Наименование операции", font);
            addHeaderCell(table, "Содержание операции", font);
            addHeaderCell(table, "Технологические параметры", font);

            List<String[]> operations = loadOperationsWithParams(result, parameters);
            int index = 1;
            for (String[] op : operations) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(index++)).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(op[0]).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(op[1]).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(op[2]).setFont(font)));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addHeaderCell(Table table, String content, PdfFont font) {
        Cell header = new Cell()
                .add(new Paragraph(content).setFont(font))
                .setFontSize(12)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(header);
    }

    private List<String[]> loadOperationsWithParams(HardeningResult result, HardeningParameters parameters) {
        List<String[]> operations = new ArrayList<>();

        try (Connection conn = Connector.getConnection()) {
            String query = "SELECT * FROM operation_card WHERE part_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, parameters.getPartName());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        operations.add(new String[]{"Подготовка поверхности", rs.getString("prep_surface"), "—"});
                        operations.add(new String[]{"Установка детали", rs.getString("setup"), "—"});
                        operations.add(new String[]{"Индукционный нагрев", rs.getString("induction_heating"),
                                String.format("Температура: %.1f°C\nЧастота: %.1f кГц\nПолная мощность: %.1f кВт\nСкорость перемещения: %.1f мм/с\nРекомендуемый генератор ТВЧ: %s",
                                        Double.valueOf(result.getHeatingTemperature()),
                                        result.getFrequency(),
                                        result.getPower(),
                                        result.getDetailSpeed(),
                                        result.getGenerator().getType())});
                        operations.add(new String[]{"Закалка", rs.getString("quenching"),
                                "Среда охлаждения: " + result.getCoolingMedium()});
                        operations.add(new String[]{"Отпуск", rs.getString("tempering"),
                                "Температура отпуска: " + result.getTemperingTemperature() + " °C"});
                        operations.add(new String[]{"Охлаждение после отпуска", rs.getString("post_tempering_cooling"), "—"});
                        operations.add(new String[]{"Контроль твёрдости", rs.getString("hardness_control"),
                                String.format("Ожидаемая твердость: %d–%d HRC\nОжидаемая глубина закалки: %.1f мм",
                                        result.getHardness().min,
                                        result.getHardness().max,
                                        parameters.getQuenchDepths())});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return operations;
    }
}
