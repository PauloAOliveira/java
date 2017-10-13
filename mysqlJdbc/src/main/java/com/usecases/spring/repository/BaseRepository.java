package com.usecases.spring.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseRepository {

    /**
     * Field must be on pattern yyyy-MM-dd
     * */
    protected LocalDate getAsLocalDate(ResultSet rs, String field) {
        try {
            String value = rs.getString(field);
            if(!rs.wasNull()) {
                return LocalDate.parse(value);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Field must be on pattern yyyy-MM-dd HH:mm:SS
     * */
    protected LocalDateTime getAsLocalDateTime(ResultSet rs, String field) {
        try {
            String value = rs.getString(field);
            if(!rs.wasNull()) {
                return LocalDateTime.parse(value.replaceAll("\\.[^.]*$", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
