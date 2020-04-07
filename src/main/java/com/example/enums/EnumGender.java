package com.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 *
 * @author tangaq@xxx.cn
 * @since 2019/7/20 17:31
 */
@Getter
@AllArgsConstructor
public enum EnumGender{
    /** 描述 */
    MALE("M", "男"),
    FEMALE("F", "女"),
    UNKNOWN("u", "未知"),
    ;

    /** 状态码 */
    private String code;

    /** 状态描述 */
    private String description;

    /**
     * 根据编码查找枚举
     *
     * @param code 编码
     * @return {@link EnumGender } 实例
     **/
    public static EnumGender find(String code) {
        for (EnumGender instance : EnumGender.values()) {
            if (instance.getCode()
                .equals(code)) {
                return instance;
            }
        }
        return null;
    }
}