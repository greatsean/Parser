package com.greatsean.parser.net;

import java.util.Map;

import com.greatsean.parser.JsonParser;
import com.greatsean.parser.Response;

import android.content.Context;

/**
 * Json异步任务解析器。注解：此解析器依赖于GSON库， 在android打包使用proguard混淆时需要将entity类keep
 * 
 * @author lixiaohui
 * @param <T> T是继承自Response的类型
 */
public abstract class JsonParserTask<T extends Response> extends AbstractParserTask<T>
{

    /**
     * 构造FORM表单方式提交参数
     * 
     * @param reqPath 请求路径(不含请求参数)
     * @param reqParams 请求参数，可以为null;
     * @param reqMethod 请求方式 {@code HttpHelper.METHOD_GET} 和
     * {@code HttpHelper.METHOD_POST}
     * @param context Context实例(执行异步任务时)是否显示等待加载框，如加载请传入该参数
     */
    public JsonParserTask(String reqPath, Map<String, String> reqParams, int reqMethod, Class<T> entityClass,
            Context context)
    {
        super(reqPath, reqParams, reqMethod, context, new JsonParser<T>(entityClass));
    }

    /**
     * 构造JSON方式提交参数
     * 
     * @param reqPath
     * @param reqMethod
     * @param requestBody
     * @param entityClass
     * @param context
     */
    public JsonParserTask(String reqPath, int reqMethod, String requestBody, Class<T> entityClass, Context context)
    {
        super(reqPath, requestBody, reqMethod, context, new JsonParser<T>(entityClass));
    }
    
}
