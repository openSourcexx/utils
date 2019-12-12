package com.example.exception;

import java.io.Serializable;

/**
 * 业务异常
 *
 */
public class BizException extends AbstractBaseException implements Serializable {
    private static final long serialVersionUID = -677285011703824903L;

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String errCode,String message, Throwable cause) {
        super(message, cause);
        this.errCode = errCode;
        this.errMessage = message;
    }
}
