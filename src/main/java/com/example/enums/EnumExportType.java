package com.example.enums;

/**
 * 导出excel类型
 *
 * @author tangaq25172@yunrong.cn
 * @since 2019/12/27 11:17
 */
public enum EnumExportType {
    /** */
    LOCAL("0","导出到本地"),
    UPLOAD_SERVER("1","上传到服务器"),
    STREAM("2","响应流文件"),
    ;

    private String code;
    private String desc;

    EnumExportType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
