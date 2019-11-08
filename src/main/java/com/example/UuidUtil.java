package com.example;

import java.util.UUID;

/**
 * uuid
 *
 */
public class UuidUtil {
    public static String nextId() {
        return UUID.randomUUID().toString().trim().replaceAll("-", "").toUpperCase();
    }
}
