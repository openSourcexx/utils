package com.example.dbutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * db连接
 *
 * @author aq
 * @date 20190601
 */

public class DatabaseConnUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(DatabaseConnUtil.class);

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL
        = "jdbc:mysql://localhost:3306/jc_project?useUnicode=true&serverTimezone=UTC&characterEncoding=utf8";
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
            LOGGER.error("get connection failure", e);
        }
        return conn;
    }

    public static Connection getConnection(String dbName) {
        Connection conn = null;
        String url = "jdbc:mysql://localhost:3306/" + dbName
            + "?useUnicode=true&serverTimezone=UTC&characterEncoding=utf8";
        try {
            conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error("get connection failure", e);
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     *
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
            }
        }
    }

}
