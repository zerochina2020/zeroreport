package com.zerochina.report.domain;

import lombok.Data;

/**
 * 接口相应
 * @author jiaquan
 * @date 2020/9/20
 */
@Data
public class Result {
    private String errorCode;
    private String message;
    private String totalSize;
    private Object data;

    public Result() {
    }

    public Result(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
