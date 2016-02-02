package com.greatsean.parser;

/**
 * 
 * @author lixiaohui
 * 
 * @param <T>
 */
public interface Parser<T>
{
    public T parse(byte[] result) throws Exception;

    public T parse(String result) throws Exception;
}
