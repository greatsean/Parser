package com.greatsean.parser.net;

/**
 * 异步任务状态
 * 
 * @author lixiaohui
 * 
 * @param <T>
 */
public interface TaskStatusCallback<T>
{
    /**
     * 任务结束（成功） server有返回的信息，并且是指定的数据格式返回
     * 
     * @param response
     * @return 此方法已经处理完毕返回TRUE，否则FALSE
     */
    public boolean onTaskSuccess(T response);

    /**
     * 任务结束（失败） 请求失败，有如下原因： 1.网络异常; 2.返回数据格式解析异常.
     * 
     * @param errorCode 捕捉的异常或者错误类别
     * @return 此方法已经处理完毕返回TRUE，否则FALSE
     */
    public boolean onTaskFailure(int errorCode);

    /**
     * 任务结束
     * 
     */
    public void onTaskFinish();

    /**
     * 任务开始
     */
    public void onTaskStart();
}
