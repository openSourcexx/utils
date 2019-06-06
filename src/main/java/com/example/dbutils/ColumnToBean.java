package com.example.dbutils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * @author tangaq
 * @date 2019/2/27
 */
public class ColumnToBean {
    public static void main(String[] args) {
        readFile();
    }

    private static void readFile() {
        String src = "com/example/webdemo/dao/mapper/column.txt";
        String des = "com/example/webdemo/dao/mapper/bean.txt";

        InputStream inputStream = ColumnToBean.class.getClassLoader()
            .getResourceAsStream(src);
        String path = ColumnToBean.class.getClassLoader()
            .getResource(des)
            .getPath();
        Reader in = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(in);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path);

            Writer writer = new OutputStreamWriter(outputStream);
            String line = reader.readLine();
            while (line != null) {
                String bean = setField(line);
                writer.write(bean + ";\n");
                // System.out.println("private String "+bean); // private String bean
                System.out.println("\""+bean+"\":"+"\"1"+"\","); // "bean":"1"
                // System.out.println("\"" + bean + "\":1,"); // "bean":"1"
                //setAndGetMethod(bean); // setM(m.getM())
                // setMethod(bean); // setM();
                writer.flush();
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setMethod(String m, String bean) {
        StringBuilder builder = new StringBuilder(m+".set");
        builder.append(String.valueOf(bean.charAt(0))
            .toUpperCase());
        builder.append(bean.substring(1));
        builder.append("(");
        builder.append(");");
        System.out.println(builder.toString());
    }

    public static void setAndGetMethod(String a, String b, String bean) {
        StringBuilder builder = new StringBuilder(a + ".set");
        builder.append(String.valueOf(bean.charAt(0))
            .toUpperCase());
        builder.append(bean.substring(1));
        builder.append("(");
        builder.append(b + ".get");
        builder.append(String.valueOf(bean.charAt(0))
            .toUpperCase());
        builder.append(bean.substring(1));
        builder.append("(");
        builder.append(")");
        builder.append(");");
        System.out.println(builder.toString());
    }

    public static String setField(String column) {
        if (!column.contains("_")) {
            return column;
        }
        String[] split = column.split("_");
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

}
