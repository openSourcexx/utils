package com.example.exception;

import java.io.Serializable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractBaseException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 3224070857516441815L;

    protected String errCode;

    protected String errMessage;

    public AbstractBaseException() {
        super();
    }

    public AbstractBaseException(String message) {
        super(message);
        this.errMessage = message;
    }

    public AbstractBaseException(String message, Throwable cause) {
        super(message, cause);
        this.errMessage = message;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    protected static String format(String template, Object... values) {
        if (ArrayUtils.isEmpty(values)) {
            return template;
        } else {
            String replacedTemplate = template.replace("%", "%%");
            return StringUtils.isBlank(template) ? "" : String.format(replacedTemplate.replace("{}", "%s"), values);
        }
    }
}
