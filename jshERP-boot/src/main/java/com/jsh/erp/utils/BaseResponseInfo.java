package com.jsh.erp.utils;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponseInfo implements Serializable {
	public int code;
	public Object data;
	private String msg;
	
	public BaseResponseInfo() {
		code = 400;
		data = null;
	}

	public BaseResponseInfo(int code, Object data, String msg) {
		this.code = code;
		this.data = data;
		this.msg = msg;
	}

	public BaseResponseInfo(int code, Object data) {
		this.code = code;
		this.data = data;
	}
}
