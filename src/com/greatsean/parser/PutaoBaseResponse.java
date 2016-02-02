package com.greatsean.parser;

import com.google.gson.annotations.SerializedName;

/**
 * 葡萄后台请求返回通用基类
 * 
 * @author lixiaohui
 */
public class PutaoBaseResponse extends JsonResponse
{

    @SerializedName("ret_code")
    protected String code;

    @SerializedName("msg")
    protected String msg;

    /**
     * 获得错误编码
     * 
     * @return
     */
    public String getCode()
    {
        return code;
    }

    /**
     * 设置错误编码
     * 
     * @param msg
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * 获得错误信息
     * 
     * @return
     */
    public String getError()
    {
        return msg;
    }

    /**
     * 设置错误信息
     * 
     * @param msg
     */
    public void setError(String msg)
    {
        this.msg = msg;
    }

}
