package com.greatsean.parser;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 解析xml时必须继承的DefaultHandler基类
 * 
 * @author lixiaohui
 * @param <T> T是继承自Response的类型
 */
public abstract class BaseHandler<T extends Response> extends DefaultHandler
{
    public T result;

    public StringBuilder currentValue = null;

    public T getResult()
    {
        return result;
    }

    public void characters(char[] ch, int start, int length) throws SAXException
    {
        super.characters(ch, start, length);
        if (null != currentValue)
        {
            currentValue.append(ch, start, length);
        }
    }
}
