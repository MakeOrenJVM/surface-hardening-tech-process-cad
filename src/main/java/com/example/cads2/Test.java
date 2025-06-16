package com.example.cads2;

import java.sql.*;

public class Test {

    public static void main(String[] args) {
        String query = "SELECT * FROM steel_grades";

        try (Connection connection = Connector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String grade = resultSet.getString("steel_grade");
                String type = resultSet.getString("steel_type");
                int hMin = resultSet.getInt("hardening_temp_min");
                int hMax = resultSet.getInt("hardening_temp_max");
                String medium = resultSet.getString("cooling_medium");
                int tMin = resultSet.getInt("tempering_temp_min");
                int tMax = resultSet.getInt("tempering_temp_max");
                int hardMin = resultSet.getInt("hardness_min");
                int hardMax = resultSet.getInt("hardness_max");

                System.out.printf(
                        "%s (%s): закалка %d-%d°C, охлаждение: %s, отпуск %d-%d°C, твердость: %d-%d HRC%n",
                        grade, type, hMin, hMax, medium, tMin, tMax, hardMin, hardMax
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
