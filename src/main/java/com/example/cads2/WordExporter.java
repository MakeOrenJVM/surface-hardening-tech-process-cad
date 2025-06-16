package com.example.cads2;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WordExporter {
    private final Stage primaryStage;

    public WordExporter(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void export(HardeningResult result) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить результаты в Word");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Документ Word (*.docx)", "*.docx")
        );
        fileChooser.setInitialFileName("результаты_закалки.docx");

        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            writeToFile(result, file.getAbsolutePath());
            System.out.println("Файл успешно сохранён: " + file.getAbsolutePath());
        } else {
            System.out.println("Сохранение отменено.");
        }
    }

    private void writeToFile(HardeningResult result, String filePath) {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();

            run.setText("Результаты расчёта закалки:");
            run.addBreak();
            run.setText("Температура нагрева: " + result.getHeatingTemperature() + " °C");
            run.addBreak();
            run.setText("Температура отпуска: " + result.getTemperingTemperature() + " °C");
            run.addBreak();
            run.setText("Полная мощность: " + result.getPower() + " кВт");
            run.addBreak();
            run.setText("Частота: " + result.getFrequency() + " кГц");
            run.addBreak();
            run.setText("Скорость перемещения: " + result.getDetailSpeed() + " мм/с");
            run.addBreak();
            run.setText("Среда охлаждения: " + result.getCoolingMedium());

            try (FileOutputStream out = new FileOutputStream(filePath)) {
                document.write(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

