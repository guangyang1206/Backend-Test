package com.revolut.util;

import java.io.Serializable;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: BaseResponse
 * @Package com.revolut.util
 * @Description: BaseResponse Class to customize response result
 */
public class BaseResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    public String errorCode;

    public String errorMessage;

    public BaseResponse() {
    }

    public BaseResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getReturnCode() {
        return errorCode;
    }

    public void setReturnCode(String returnCode) {
        this.errorCode = returnCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
