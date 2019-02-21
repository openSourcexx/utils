package com.example;

import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamTest {
    public static void main(String[] args) {
        List<User> users = User.getUsers();
        Map<String,User> map = users.stream().collect(Collectors.toMap(User::getName, e->e));
        System.out.println(JSONUtil.toJSONString(map));
    }

}
