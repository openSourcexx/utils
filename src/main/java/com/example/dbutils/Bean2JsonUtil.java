package com.example.dbutils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.example.DateUtil;
import com.example.User;

/**
 * 类转为json报文
 *
 * @author aq
 * @since 2019/5/30 15:59
 */
public class Bean2JsonUtil {
    public static void main(String[] args) {
        bean2JsonReq(User.class);
        // dbColumn2JsonReq(null, "t_user");
    }

    /**
     * 数据库字段转为jsonReq
     * @param dbName
     * @param tableName
     */
    private static void dbColumn2JsonReq(String dbName, String tableName) {
        Connection conn = DatabaseConnUtil.getConnection();
        DatabaseMetaData metaData = null;
        ResultSet rs = null;
        try {
            metaData = conn.getMetaData();
            rs = metaData.getColumns(null, null, tableName.toUpperCase(), "%");
            StringBuilder builder = new StringBuilder();
            while (rs.next()) {
                builder.append("\"");
                builder.append(GenUtil.getFeild(rs.getString("COLUMN_NAME")));
                builder.append("\" : ");
                getData4Type(builder, null, GenUtil.getType(rs.getString("TYPE_NAME")));
                builder.append(",");
                builder.append("\n");
            }
            System.out.println(builder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnUtil.closeConnection(conn);
        }
    }

    private static void bean2JsonReq(Class clazz) {
        GenUtil.getTableNames("test", "jc_project");
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            builder.append("\"");
            builder.append(field.getName());
            builder.append("\" : ");
            getData4Type(builder, field,null);
            if (i < fields.length - 1) {
                builder.append(",");
            }
            builder.append("\n");
        }
        System.out.println(builder.toString());

    }

    /**
     *
     * @param builder
     * @param field bean.class转换为reqJson
     * @param typeName  db字段转换为reqJson
     */
    private static void getData4Type(StringBuilder builder, Field field, String typeName) {
        if (null != field) {
            if ("class java.lang.String".equals(typeName)) {
                builder.append("\"");
                builder.append("1");
                builder.append("\"");
            } else if ("class java.lang.Integer".equals(field.getType()
                .toString()) || "int".equals(field.getType()
                .toString())) {
                builder.append(1);
            } else if ("class java.lang.Double".equals(field.getType()
                .toString()) || "double".equals(field.getType()
                .toString())) {
                builder.append(1.0d);
            } else if ("class java.lang.Boolean".equals(field.getType()
                .toString()) || "boolean".equals(field.getType()
                .toString())) {
                builder.append(true);
            } else if ("class java.lang.Long".equals(field.getType()
                .toString()) || "long".equals(field.getType()
                .toString())) {
                builder.append(1L);
            } else if ("class java.util.Date".equals(field.getType()
                .toString())) {
                builder.append("\"");
                builder.append(DateUtil.getDate(new Date(), DateUtil.DATE_FORMAT_2));
                builder.append("\"");
            } else {
                builder.append("\"");
                builder.append("1");
                builder.append("\"");
            }
        } else {
            typeName = StringUtils.trim(typeName);
            if ("class java.lang.String".equals(typeName) || "String".equals(typeName)) {
                builder.append("\"");
                builder.append("1");
                builder.append("\"");
            } else if ("class java.lang.Double".equals(typeName) || "double".equals(typeName) || "Double".equals(
                typeName)) {
                builder.append(1.0d);
            } else if ("class java.lang.Double".equals(typeName) || "double".equals(typeName) || "Double".equals(
                typeName)) {
                builder.append(1.0d);
            } else if ("class java.lang.Boolean".equals(typeName) || "boolean".equals(typeName) || "Boolean".equals(
                typeName)) {
                builder.append(true);
            } else if ("class java.lang.Long".equals(typeName) || "long".equals(typeName) || "Long".equals(typeName)) {
                builder.append(1L);
            } else if ("class java.util.Date".equals(typeName) || "Date".equals(typeName)) {
                builder.append("\"");
                builder.append(DateUtil.getDate(new Date(), DateUtil.DATE_FORMAT_2));
                builder.append("\"");
            } else {
                builder.append("\"");
                builder.append("1");
                builder.append("\"");
            }
        }

    }
}
