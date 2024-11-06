package com.gankcode.springboot.db.config.mybatis.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeTypeHandler extends BaseTypeHandler<OffsetDateTime> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, OffsetDateTime parameter, JdbcType jdbcType) throws SQLException {
        String format = parameter.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ps.setObject(i, format);
    }

    @Override
    public OffsetDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnName);
        return getInstant(timestamp);
    }

    @Override
    public OffsetDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnIndex);
        return getInstant(timestamp);
    }

    @Override
    public OffsetDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Timestamp timestamp = cs.getTimestamp(columnIndex);
        return getInstant(timestamp);
    }

    private static OffsetDateTime getInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        final Instant instant = timestamp.toLocalDateTime().toInstant(ZoneOffset.UTC);
        return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}