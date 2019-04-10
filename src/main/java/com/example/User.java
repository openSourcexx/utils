package com.example;

import java.util.*;
import java.util.stream.Collectors;


public class User {

    private String age;

    private String name;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }

    public static void main(String[] args) {
        List<User> users = getUsers();
        Map<String,User> map = users.stream().collect(Collectors.toMap(User::getName,e->e));
        System.out.println(JSONUtil.toJSONString(map));
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                /*double n1 = o1.getAge();
                double n2 = o2.getAge();*/
                return o1.getAge().compareTo(o2.getAge());
            }
        });
        System.out.println(users);
    }

    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();
        User u1 = new User();
        u1.setAge("20");
        u1.setName("a");
        users.add(u1);
        User u2 = new User();
        u2.setAge("29");
        u2.setName("b");
        users.add(u2);
        User u3 = new User();
        u3.setAge("11");
        u3.setName("c");
        users.add(u3);
        return users;
    }

}
