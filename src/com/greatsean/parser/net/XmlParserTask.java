package com.greatsean.parser.net;

import java.util.Map;

import com.greatsean.parser.BaseHandler;
import com.greatsean.parser.Response;
import com.greatsean.parser.XmlParser;

import android.content.Context;

/**
 * xml异步任务解析器。
 * 
 * @author lixiaohui
 * @param <T> T是继承自Response的类型
 */
public abstract class XmlParserTask<T extends Response> extends AbstractParserTask<T>
{

    /**
     * 有加载框的构造方法
     * 
     * @param reqPath 请求路径(不含请求参数)
     * @param reqParams 请求参数，可以为null;
     * @param reqMethod 请求方式
     * @param handler 使用sax解析xml时的DefaultHandler类
     * @param context Context实例(执行异步任务时)是否显示等待加载框，如加载请传入该参数
     */
    public XmlParserTask(String reqPath, Map<String, String> reqParams, int reqMethod, BaseHandler<T> handler,
            Context context)
    {
        super(reqPath, reqParams, reqMethod, context, new XmlParser<T>(handler));
    }

}
