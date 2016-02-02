package com.greatsean.parser;

import com.google.gson.Gson;

/**
 * Json同步解析器。注解：此解析器依赖于GSON库， 在android打包使用proguard混淆时需要将entity类keep
 * 
 * @author lixiaohui
 * @param <T> T是继承自Response的类型
 */
public class JsonParser<T> implements Parser<T>
{
    /**
     * 实体类T对应Class对象
     */
    private Class<T> mEntityClass;

    /**
     * 构造方法
     * 
     * @param entityClass 实体类T对应Class对象
     */
    public JsonParser(Class<T> entityClass)
    {
        mEntityClass = entityClass;
    }

    @Override
    public T parse(byte[] result) throws Exception
    {
        return parse(new String(result));
    }

    @Override
    public T parse(String result) throws Exception
    {
        T response = null;
        if (result != null)
        {
            response = getConfig().getGson().fromJson(result, mEntityClass);
        }
        return response;
    }

    public class JsonParserConfig
    {
        private Gson gson = new Gson();

        public Gson getGson()
        {
            return gson;
        }

        public void setGson(Gson gson)
        {
            this.gson = gson;
        }

    }

    private JsonParserConfig config = new JsonParserConfig();

    public JsonParserConfig getConfig()
    {
        return config;
    }
}
