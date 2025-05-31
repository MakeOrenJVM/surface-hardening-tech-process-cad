package com.example.cads2;

import java.sql.*;

public class Test {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/sapr_db"; // порт и БД
        String user = "sapr_user"; // замени, если другое имя
        String password = "admin"; // вставь свой пароль

        String query = "SELECT * FROM steel_grades";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("Марка стали | Тип стали | Темп. нагрева | Темп. отпуска | Среда охлаждения после закалки | Среда охлаждения после отпуска");
            System.out.println("------------------------------------------------------------------------------------------");


            while (rs.next()) {
                String grade = rs.getString("grade");
                int hardTemp = rs.getInt("hardening_temp");
                int temperTemp = rs.getInt("tempering_temp");
                String quenchMedium = rs.getString("quenching_medium");
                String temperingMedium = rs.getString("tempering_cooling_medium");

                System.out.printf("%-12s | %-14d | %-14d | %-30s | %s%n",
                        grade, hardTemp, temperTemp, quenchMedium, temperingMedium);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

