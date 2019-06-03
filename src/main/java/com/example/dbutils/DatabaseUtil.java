package com.example.dbutils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 操作数据库
 * @author aq
 * @date 20190601
 */

public class DatabaseUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(DatabaseUtil.class);

    private static final String SQL = "SELECT * FROM ";

    /**
     * 获取数据库下的所有表名
     */
    // public static List<String> getTableNames() {
    //     List<String> tableNames = new ArrayList<>();
    //     Connection conn = getConnection();
    //     ResultSet rs = null;
    //     try {
    //         //获取数据库的元数据
    //         DatabaseMetaData db = conn.getMetaData();
    //         //从元数据中获取到所有的表名
    //         *
    //          * 其中"%"就是表示*的意思，也就是任意所有的意思。
    //          * 其中tableNamePattern就是要获取的数据表的名字，
    //          * 如果想获取所有的表的名字，就可以使用"%"来作为参数了。
    //
    //         rs = db.getTables(null, null, null, new String[] { "TABLE" });
    //         while(rs.next()) {
    //             System.out.println("表名：" + rs.getString("TABLE_NAME"));
    //             System.out.println("表类型：" + rs.getString("TABLE_TYPE"));
    //             System.out.println("表所属数据库：" + rs.getString("TABLE_CAT"));
    //             System.out.println("表备注：" + rs.getString("REMARKS"));
    //             //tables.add(rs.getString("TABLE_NAME"));
    //             tableNames.add(rs.getString(3));
    //         }
    //     } catch (SQLException e) {
    //         LOGGER.error("getTableNames failure", e);
    //     } finally {
    //         try {
    //             rs.close();
    //             closeConnection(conn);
    //         } catch (SQLException e) {
    //             LOGGER.error("close ResultSet failure", e);
    //         }
    //     }
    //     return tableNames;
    // }

    /**
     * 获取表中所有字段名称
     * @param tableName 表名
     * @return
     */
    public static List<String> getColumnNames(String tableName) {
        List<String> columnNames = new ArrayList<>();
        //与数据库的连接
        Connection conn = DatabaseConnUtil.getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnNames.add(rsmd.getColumnName(i + 1));
            }
        } catch (SQLException e) {
            LOGGER.error("getColumnNames failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    DatabaseConnUtil.closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnNames close pstem and connection failure", e);
                }
            }
        }
        return columnNames;
    }

    /**
     * 获取表中所有字段类型
     * @param tableName
     * @return
     */
    public static List<String> getColumnTypes(String tableName) {
        List<String> columnTypes = new ArrayList<>();
        //与数据库的连接
        Connection conn =  DatabaseConnUtil.getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnTypes.add(rsmd.getColumnTypeName(i + 1));
            }
        } catch (SQLException e) {
            LOGGER.error("getColumnTypes failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    DatabaseConnUtil.closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnTypes close pstem and connection failure", e);
                }
            }
        }
        return columnTypes;
    }

    /**
     * 获取表中字段的所有注释
     * @param tableName
     * @return
     */
    public static List<String> getColumnComments(String tableName) {
        List<String> columnTypes = new ArrayList<>();
        //与数据库的连接
        Connection conn =  DatabaseConnUtil.getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        List<String> columnComments = new ArrayList<>();//列名注释集合
        ResultSet rs = null;
        try {
            pStemt = conn.prepareStatement(tableSql);
            rs = pStemt.executeQuery("show full columns from " + tableName);
            while (rs.next()) {
                columnComments.add(rs.getString("Comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    DatabaseConnUtil.closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnComments close ResultSet and connection failure", e);
                }
            }
        }
        return columnComments;
    }
    public static void main(String[] args) {
        // List<String> tableNames = getTableNames();
        List<String> tableNames = getTableNames("test","t_role");
        System.out.println("tableNames:" + tableNames);
        /*for (String tableName : tableNames) {
            System.out.println("ColumnNames:" + getColumnNames(tableName));
            System.out.println("ColumnTypes:" + getColumnTypes(tableName));
            System.out.println("ColumnComments:" + getColumnComments(tableName));
        }*/
    }

    /**
     *
     * @param dbName 数据库名
     * @param tableName 表名
     * @return
     */
    public static List<String> getTableNames(String dbName,
        String tableName) {
        List<String> tableNames = new ArrayList<>();
        Connection conn =  DatabaseConnUtil.getConnection();
        ResultSet rs = null;
        try {
            //获取数据库的元数据
            DatabaseMetaData db = conn.getMetaData();
            //从元数据中获取到所有的表名
            rs = db.getTables(dbName, null, tableName, new String[] { "TABLE" });
            while(rs.next()) {
                System.out.println("表名：" + rs.getString("TABLE_NAME"));
                System.out.println("表类型：" + rs.getString("TABLE_TYPE"));
                System.out.println("表所属数据库：" + rs.getString("TABLE_CAT"));
                System.out.println("表备注：" + rs.getString("REMARKS"));
                //tables.add(rs.getString("TABLE_NAME"));
                tableNames.add(rs.getString(3));
            }
        } catch (SQLException e) {
            LOGGER.error("getTableNames failure", e);
        } finally {
            try {
                rs.close();
                DatabaseConnUtil.closeConnection(conn);
            } catch (SQLException e) {
                LOGGER.error("close ResultSet failure", e);
            }
        }
        return tableNames;
    }
}
