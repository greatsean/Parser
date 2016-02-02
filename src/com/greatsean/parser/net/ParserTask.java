package com.greatsean.parser.net;

import java.util.Map;

import so.contacts.hub.basefunction.net.exception.PutaoException;

import com.android.volley.Request;

/**
 * 异步解析任务
 * 
 * @author lixiaohui
 */
public interface ParserTask<T> extends TaskStatusCallback<T>, Request.Method
{
    /** 请求优先级别-低 */
    public static final int PRIORITY_LOW = 0;

    /** 请求优先级别-普通 */
    public static final int PRIORITY_NORMAL = 1;

    /** 请求优先级别-高 */
    public static final int PRIORITY_HIGH = 2;

    /** 请求优先级别-立即 */
    public static final int PRIORITY_IMMEDIATE = 3;

    /**
     * 开始同步解析
     */
    void asyncParse();

    /**
     * 开始同步解析
     */
    void asyncParse(TaskStatusCallback<T> taskStatusCallback);

    /**
     * 取消解析
     */
    void cancel();

    /**
     * 重新加载当前的解析任务(暂未实现)
     */
    void reload();

    /**
     * 获取当前请求参数
     * 
     * @return
     */
    Map<String, String> getParams();

    /**
     * 获取当前请求路径
     * 
     * @return
     */
    String getPath();

    /**
     * 获取当前请求方式
     * 
     * @return
     */
    int getMethod();

    /**
     * 获取当前请求优先级
     * 
     * @return
     */
    int getPriority();

    /**
     * 解析任务是否正在进行
     * 
     * @return
     */
    boolean isRunning();

    /**
     * 获取当前请求头配置集合
     * 
     * @return
     */
    Map<String, String> getHeaders();

    /**
     * 开始同步解析
     * 
     * @author lxh
     * @since 2015-6-3
     * @return
     * @throws Exception
     */
    T syncParse() throws PutaoException;

    /**
     * 返回是否无数据码
     * 
     * @author lxh
     * @since 2016-1-15
     * @param response
     * @return
     */
    boolean isNoDataCode(T response);

    /**
     * 返回是否成功码
     * 
     * @author lxh
     * @since 2016-1-15
     * @param response
     * @return
     */
    boolean isSuccessCode(T response);

    /**
     * 返回的消息内容
     * 
     * @author lxh
     * @since 2016-1-15
     * @param response
     * @return
     */
    String getMsg(T response);
}
