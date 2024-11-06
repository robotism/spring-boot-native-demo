package com.gankcode.springboot.db.config.mybatis.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.gankcode.springboot.utils.JsonTemplate;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes({JdbcType.VARCHAR, JdbcType.NVARCHAR})
public class JsonNodeTypeHandler extends BaseTypeHandler<JsonNode> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonNode parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter != null ? parameter.toString() : "");
    }

    @Override
    public JsonNode getNullableResult(ResultSet rs, String columnName) throws SQLException {
        final String data = rs.getString(columnName);
        return data == null ? null : JsonTemplate.getInstance().fromJson(data, getRawType());
    }

    @Override
    public JsonNode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        final String data = rs.getString(columnIndex);
        return data == null ? null : JsonTemplate.getInstance().fromJson(data, getRawType());
    }

    @Override
    public JsonNode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        final String data = cs.getString(columnIndex);
        return data == null ? null : JsonTemplate.getInstance().fromJson(data, getRawType());
    }

}