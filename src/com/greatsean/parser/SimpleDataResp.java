package com.greatsean.parser;

/**
 * (通用)简单结果数据
 * 
 * @author lxh
 * @since 2015-4-29
 */
public class SimpleDataResp extends PutaoBaseResponse
{

    /** 数据内容 */
    private String data;

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

}
