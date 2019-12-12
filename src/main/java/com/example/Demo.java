package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class Demo {
    public static void main(String[] args) throws Exception {

        // s2 19764
        BufferedReader br2 = new BufferedReader(new FileReader(new File("C:\\Users\\Q\\Downloads\\20191204还款计划同步\\JC_repayment_plan_20191204_去掉结清后同步还款计划_少了30条.txt")));
        String l2;
        Set<String> s2 = new HashSet<>();
        while ((l2 = br2.readLine()) != null) {
            String[] split = l2.split("\\|");
            s2.add(split[0]);
        }

        // s1 19794
        BufferedReader br1 = new BufferedReader(new FileReader(new File("C:\\Users\\Q\\Downloads\\20191204还款计划同步\\FQL_repayment_plan_20191204_remove_has_repay.txt")));
        String l1;
        Set<String> s1 = new HashSet<>();
        while ((l1 = br1.readLine()) != null) {
            String[] split = l1.split("\\|");
            s1.add(split[0]);
        }
        Set<String> s3 = new HashSet<>();
        // s2 19764 s1 19794
        s1.forEach(e->{
            if (!s2.contains(e)) {
                System.out.println(e);
                s3.add(e);
            }
        });


        br1.close();
        br2.close();
        // System.out.println(s2);
        // System.out.println(s2.size());
        // System.out.println(s1.size());
        System.out.println(s3.size());
    }
}
