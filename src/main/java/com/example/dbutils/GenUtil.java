package com.example.dbutils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库生成属性字段
 * @author aq
 * @date 20190601
 */

public class GenUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(GenUtil.class);

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String DBNAME = "test";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DBNAME + "?useUnicode=true&serverTimezone=UTC&characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.error("can not load jdbc driver", e);
        }
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error("连接数据库失败", e);
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("关闭数据库失败", e);
            }
        }
    }
    /**
     * 获取表
     * @param dbName 数据库名
     * @param tableName 表名
     * @return
     */
    public static List<String> getTableNames(String dbName,
        String tableName) {
        List<String> tableNames = new ArrayList<>();
        Connection conn = getConnection();
        ResultSet rs = null;
        try {
            //获取数据库的元数据
            DatabaseMetaData db = conn.getMetaData();
            //从元数据中获取到所有的表名
            rs = db.getTables(dbName, null, tableName, new String[] { "TABLE" });
            while(rs.next()) {
                // System.out.println("表名：" + rs.getString("TABLE_NAME"));
                // System.out.println("表类型：" + rs.getString("TABLE_TYPE"));
                // System.out.println("表所属数据库：" + rs.getString("TABLE_CAT"));
                // System.out.println("表备注：" + rs.getString("REMARKS"));
                //tables.add(rs.getString("TABLE_NAME"));
                tableNames.add(rs.getString(3));
            }

            getColumns(db,conn,tableName);


        } catch (SQLException e) {
            LOGGER.error("获取表失败", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                closeConnection(conn);
            } catch (SQLException e) {
                LOGGER.error("close ResultSet failure", e);
            }
        }
        return tableNames;
    }

    /**
     * 生成字段
     * @param db
     * @param conn
     * @param tableName
     * @throws SQLException
     */
    private static void getColumns(DatabaseMetaData db, Connection conn, String tableName) throws SQLException {
        ResultSet rs = db.getColumns(null, null,tableName.toUpperCase(), "%");
        StringBuilder builder = new StringBuilder();
        while(rs.next()){
            // builder.append("    ");
            builder.append("/** ");
            // 注释
            builder.append(rs.getString("REMARKS"));
            builder.append(" */");
            builder.append("\n");
            builder.append("private");
            // 类型
            builder.append(getType(rs.getString("TYPE_NAME")));
            // 表名
            builder.append(getFeild(rs.getString("COLUMN_NAME")));
            builder.append(";\n");
        }
        System.out.println(builder.toString());
    }


    public static void main(String[] args) {
        getTableNames(null,"t_user");
    }

    /**
     * 生成属性
     * @param name
     * @return
     */
    private static String getFeild(String name) {
        if (!name.contains("_")) {
            return name;
        }
        String[] split = name.split("_");
        StringBuilder sb = new StringBuilder();
        sb.append(split[0]);
        for (int i = 1; i < split.length; i++) {
            String s = split[i];
            sb.append(String.valueOf(s.charAt(0))
                .toUpperCase());
            for (int j = 1; j < s.length(); j++) {
                sb.append(String.valueOf(s.charAt(j)));
            }
        }
        return sb.toString();
    }

    private static String getType(String type) {
        if ("INT".startsWith(type)) {
            return " Integer ";
        } else if ("BIGINT".startsWith(type)) {
            return " Long ";
        } else if ("DATETIME".startsWith(type)) {
            return " java.util.Date ";
        } else if ("DECIMAL".startsWith(type)) {
            return " java.math.BigDecimal ";
        } else {
            return " String ";
        }
    }
}
