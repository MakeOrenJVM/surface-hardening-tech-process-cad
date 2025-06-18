package com.example.cads2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IdFetcher {

    public static int getSteelGradeId(String steelGrade) throws SQLException {
        return getId("steel_grades", "steel_grade", steelGrade);
    }

    public static int getQuenchDepthId(double depthMm) throws SQLException {
        return getId("quench_depths", "depth_mm", depthMm);
    }

    public static int getTvchGeneratorId(String generatorType) throws SQLException {
        return getId("tvch_generators", "type", generatorType);
    }

    public static int getOperationCardId(String partName) throws SQLException {
        return getId("operation_card", "part_name", partName);
    }

    private static int getId(String tableName, String columnName, Object value) throws SQLException {
        String query = String.format("SELECT id FROM %s WHERE %s = ?", tableName, columnName);
        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("ID не найден в таблице " + tableName + " по " + columnName + " = " + value);
                }
            }
        }
    }
}