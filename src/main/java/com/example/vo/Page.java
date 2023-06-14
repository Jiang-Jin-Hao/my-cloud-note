package com.example.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Page implements Serializable {

    // 第几页
    private Integer page;
    // 每页多少条
    private Integer limit;

    // 通过第几页得出查询的下标
    public Long getStart() {
        return Long.valueOf((page - 1) * limit);
    }
}
