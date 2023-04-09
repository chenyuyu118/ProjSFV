package pers.cherish.commentservice.config;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.nio.charset.StandardCharsets;
import java.sql.*;

public class BlobTypeHandler implements TypeHandler<String> {
    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter);
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        Blob blob = rs.getBlob(columnName);
        return blobToString(blob);
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        Blob blob = rs.getBlob(columnIndex);
        return blobToString(blob);
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        Blob blob = cs.getBlob(columnIndex);
        return blobToString(blob);
    }

    private String blobToString(Blob blob) throws SQLException {
        if (blob == null) {
            return null;
        }
        byte[] bytes = blob.getBytes(1, (int) blob.length());
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
