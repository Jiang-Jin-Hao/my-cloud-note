package com.example.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String msg;
    // 返回的数据
    private T data;
    // 分页查询总记录数
    private Long count;

    public static Result<Object> OK() {
        return new Result<>(0, null, null, null);
    }

    public static Result<Object> error(String msg) {
        return new Result<>(-1, msg, null, null);
    }

    public static Result<Object> error() {
        return new Result<>(-1, null, null, null);
    }

    public static Result<Object> OK(Object data, Long count) {
        return new Result<>(0, null, data, count);
    }
}
