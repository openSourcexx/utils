package com.example.dbutils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 通过类生成表
 * @author admin
 * @since 2019/10/11 11:46
 */
public class Bean2Column {
    /**
     * CREATE TABLE `security_user` (
     * `id` bigint(32) NOT NULL COMMENT '主键id',
     * `account` varchar(50) DEFAULT NULL COMMENT '账号',
     * `username` varchar(50) DEFAULT NULL COMMENT '用户名称',
     * `password` varchar(255) DEFAULT NULL COMMENT '密码',
     * `enabled` tinyint(1) DEFAULT NULL COMMENT '是否可用',
     * `account_non_expired` tinyint(1) DEFAULT NULL COMMENT '是否过期',
     * `account_non_locked` tinyint(1) DEFAULT NULL COMMENT '是否被锁',
     * `credentials_non_expired` tinyint(1) DEFAULT NULL COMMENT '凭证是否过期',
     * `sex` char(2) DEFAULT NULL COMMENT '性别',
     * `position` varchar(20) DEFAULT NULL COMMENT '职位',
     * `mobile` varchar(11) DEFAULT NULL COMMENT '手机号',
     * `telephone` varchar(11) DEFAULT NULL COMMENT '办公电话',
     * `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
     * `default_org_id` bigint(10) DEFAULT NULL COMMENT '默认机构',
     * `status` varchar(2) DEFAULT NULL COMMENT '状态',
     * `type` char(2) DEFAULT NULL COMMENT '类别',
     * `extend` varchar(255) DEFAULT NULL COMMENT '扩展属性',
     * `comment` varchar(255) DEFAULT NULL COMMENT '说明',
     * `user_state` tinyint(1) DEFAULT NULL COMMENT '用户状态',
     * `update_by` bigint(32) DEFAULT NULL COMMENT '修改人',
     * `update_date` datetime DEFAULT NULL COMMENT '修改时间',
     * `create_by` bigint(32) DEFAULT NULL COMMENT '创建人',
     * `create_date` datetime DEFAULT NULL COMMENT '创建时间',
     * PRIMARY KEY (`id`)
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
     */
    public static void main(String[] args) throws ClassNotFoundException {

        List<Class> classes = listFileByPackage(Bean2Column.class);
        for (Class clazz : classes) {
            bean2Column(clazz);
        }
    }

    /**
     * 获取类所在包下所有类clazz
     *
     * @param clazz
     * @return
     */
    private static List<Class> listFileByPackage(Class<?> clazz) {
        URL resource = clazz.getResource("");
        String packageName = clazz.getPackage()
            .getName()
            .replace("package", "");
        String path = resource.getPath();
        List<Class> classes = new ArrayList<>();
        listFile(classes,path, packageName);
        return classes;
    }

    /**
     * 获取文件名
     * @param classes
     * @param path
     * @param packageName
     * @return
     */
    private static List<Class> listFile(List<Class> classes,String path, String packageName) {
        File file = new File(path);
        File[] files = file.listFiles();
        String dir = packageName;
        for (File f : files) {
            if (f.isDirectory()) {
                dir = packageName + "." + f.getName();
                listFile(classes,f.getPath(), dir);
            } else if (f.isFile()) {
                String classPath = packageName + "." + f.getName()
                    .replace(".class", "");
                try {
                    Class<?> aClass = Class.forName(classPath);
                    classes.add(aClass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }

    /**
     * 通过类生成建表
     * @param clazz
     */
    private static void bean2Column(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        String simpleName = clazz.getSimpleName();

        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        String tableName = buildTableName(simpleName);
        sb.append(tableName);
        sb.append(" (\n");
        for (int i = 0; i < fields.length; i++) {
            sb.append("\t");
            Field field = fields[i];
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            if (isStatic) {
                continue;
            }

            String bean = field.getName();
            bean = filed2Column(bean);

            sb.append(bean);
            getType(sb, field);
            sb.append("\n");
        }
        sb.append("PRIMARY KEY (`id`)\n");
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        sb.append("\n");
        System.out.println(sb.toString());
    }

    private static String buildTableName(String simpleName) {
        return filed2Column(simpleName);
    }

    private static String filed2Column(String bean) {
        char[] chars = bean.toCharArray();
        StringBuilder column = new StringBuilder();
        column.append("`");
        int i = 0;
        for (char c : chars) {
            if (StringUtils.isAllUpperCase(String.valueOf(c))) {
                if (i != 0) {
                    column.append("_");
                }
                column.append(String.valueOf(c)
                    .toLowerCase());
            } else {
                column.append(c);
            }
            i++;
        }
        column.append("`");
        return column.toString();
    }

    /**
     * 生成字段类型
     * @param builder
     * @param field
     */
    private static void getType(StringBuilder builder, Field field) {
        if ("class java.lang.String".equals(field.getType()
            .toString())) {
            builder.append(" varchar(50) DEFAULT NULL COMMENT '',");
        } else if ("class java.lang.Integer".equals(field.getType()
            .toString()) || "int".equals(field.getType()
            .toString())) {
            builder.append(" int(32) NOT NULL COMMENT '',");
        } else if ("class java.lang.Double".equals(field.getType()
            .toString()) || "double".equals(field.getType()
            .toString())) {
            builder.append(1.0d);
        } else if ("class java.lang.Boolean".equals(field.getType()
            .toString()) || "boolean".equals(field.getType()
            .toString())) {
            builder.append(" tinyint(1) DEFAULT NULL COMMENT ''");
        } else if ("class java.lang.Long".equals(field.getType()
            .toString()) || "long".equals(field.getType()
            .toString())) {
            builder.append(" bigint(32) NOT NULL COMMENT '',");
        } else if ("class java.util.Date".equals(field.getType()
            .toString())) {
            builder.append(" datetime DEFAULT NULL COMMENT '',");
        } else if ("class java.math.BigDecimal".equals(field.getType()
            .toString())) {
            builder.append(" bigint(16,4) DEFAULT 0 COMMENT '',");
        }
    }
}
