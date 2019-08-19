package com.revolut.util;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: BaseException
 * @Package com.revolut.util
 * @Description: BaseException Class to customize exception
 */
public class BaseException extends Exception {

	private static final long serialVersionUID = 1L;

	public BaseException(String msg) {
		super(msg);
	}

	public BaseException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
