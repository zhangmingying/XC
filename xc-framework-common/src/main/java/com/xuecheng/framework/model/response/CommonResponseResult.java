package com.xuecheng.framework.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CommonResponseResult<T> extends ResponseResult {

    private T data;

    public CommonResponseResult(ResultCode resultCode, T data) {
        super(resultCode);
        this.data = data;
    }
}
