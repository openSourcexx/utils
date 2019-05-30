package com.example;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * 类转为json报文
 *
 * @author aq
 * @since 2019/5/30 15:59
 */
public class Bean2JsonUtil {
    public static void main(String[] args) {
        bean2Json(User.class);
    }

    private static void bean2Json(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder builder = new StringBuilder();
        for (int i = 0;i < fields.length;i++) {
            Field field = fields[i];
            builder.append("\"");
            builder.append(field.getName());
            builder.append("\" : ");
            getData4Type(builder,field);
            if (i < fields.length - 1) {
                builder.append(",");
            }
            builder.append("\n");
        }
        System.out.println(builder.toString());
    }

    private static void getData4Type(StringBuilder builder, Field field) {
        if ("class java.lang.Integer".equals(field.getType().toString()) || "int".equals(field.getType().toString())) {
            builder.append(1);
        } else if ("class java.lang.Double".equals(field.getType().toString()) || "double".equals(field.getType().toString())) {
            builder.append(1.0d);
        } else if ("class java.lang.Boolean".equals(field.getType().toString()) || "boolean".equals(field.getType().toString())) {
            builder.append(true);
        } else if ("class java.lang.Long".equals(field.getType().toString()) || "long".equals(field.getType().toString())) {
            builder.append(1L);
        } else if ("class java.util.Date".equals(field.getType().toString())) {
            builder.append("\"");
            builder.append(DateUtil.getDate(new Date(),DateUtil.DATE_FORMAT_2));
            builder.append("\"");
        } else {
            builder.append("\"");
            builder.append("1");
            builder.append("\"");
        }
    }
}
