package com.greatsean.parser.net;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.GZIPInputStream;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.basefunction.net.VolleyQueue;
import so.contacts.hub.basefunction.net.exception.PutaoException;
import so.contacts.hub.basefunction.net.exception.PutaoExceptionCode;
import so.contacts.hub.basefunction.net.exception.PutaoExceptionTool;
import so.contacts.hub.basefunction.net.exception.PutaoTimeOutException;
import so.contacts.hub.basefunction.utils.NetUtil;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Request.Priority;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.greatsean.parser.Parser;
import com.lives.depend.utils.LogUtil;

/**
 * 异步任务解析器抽象类
 * 
 * @author lixiaohui
 * @param <T>
 */
public abstract class AbstractParserTask<T> implements ParserTask<T>
{

    private static final String TAG = "ParserTask";

    /** The default socket timeout in milliseconds */
    public static final int DEFAULT_TIMEOUT_MS = 5000;// 实际上的超时时间是5000*2

    private static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * 采用的解析器
     */
    protected Parser<T> mParser;

    /**
     * Context实例
     */
    protected Reference<Context> mContextRef;

    /**
     * 请求路径
     */
    private String mReqPath;

    /**
     * 请求参数
     */
    private Map<String, String> mReqParams;

    /**
     * 请求方式.默认是NORMAL级别
     */
    private int mReqPriority = PRIORITY_NORMAL;

    /**
     * 请求方式，POST、GET等
     */
    private int mReqMethod;

    /** The current timeout in milliseconds. */
    private int mCurrentTimeoutMs;

    /**
     * 请求内容体，写在body中的内容
     */
    private String mRequestBody;

    /**
     * volley请求
     */
    private Request<T> mRequest;

    /**
     * 解析是否正在进行中
     */
    private boolean mIsRunning;

    /**
     * 默认请求头集合
     */
    private HashMap<String, String> defaultHeaders = new HashMap<String, String>();

    /**
     * 请求数据内容提供形式
     */
    private ReqContentType mReqContentType;

    /**
     * 请求各状态的回调
     */
    private TaskStatusCallback<T> mTaskStatusCallback;

    /**
     * FORM表单方式提交参数
     * 
     * @param reqPath 请求路径(不含请求参数)
     * @param reqParams 请求参数，可以为null;
     * @param reqMethod 请求方式
     * @param context Context实例
     */
    public AbstractParserTask(String reqPath, Map<String, String> reqParams, int reqMethod, Context context,
            Parser<T> parser)
    {
        this.mReqMethod = reqMethod;
        this.mReqPath = reqPath;
        this.mReqParams = reqParams;
        this.mContextRef = new SoftReference<Context>(context);
        this.mParser = parser;
        this.mCurrentTimeoutMs = DEFAULT_TIMEOUT_MS;
        this.mReqContentType = ReqContentType.FORM;
    }

    /**
     * 构造以JSON方式提交参数的请求任务
     * 
     * @param reqPath 请求路径
     * @param requestBody 提交内容体
     * @param reqMethod 请求方式
     * @param context 上下文
     * @param parser 采用的解析器
     */
    public AbstractParserTask(String reqPath, String requestBody, int reqMethod, Context context, Parser<T> parser)
    {
        this.mReqMethod = reqMethod;
        this.mReqPath = reqPath;
        this.mRequestBody = requestBody;
        this.mContextRef = new SoftReference<Context>(context);
        this.mParser = parser;
        this.mCurrentTimeoutMs = DEFAULT_TIMEOUT_MS;
        this.mReqContentType = ReqContentType.JSON;
    }

    /**
     * Define the http request content type for request
     * 
     * @author lixiaohui
     */
    enum ReqContentType
    {
        /**
         * FORM表单提交数据
         */
        FORM,
        /**
         * JSON提交数据
         */
        JSON
    }

    /**
     * 设置超时时间，单位为毫秒
     * 
     * @param currentTimeoutMs
     */
    public void setCurrentTimeoutMs(int currentTimeoutMs)
    {
        this.mCurrentTimeoutMs = currentTimeoutMs;
    }

    /**
     * 获得超时时间
     * 
     * @return
     */
    public int getCurrentTimeoutMs()
    {
        return this.mCurrentTimeoutMs;
    }

    @Override
    public void asyncParse()
    {
        asyncParse(null);
    }

    @Override
    public void asyncParse(TaskStatusCallback<T> taskStatusCallback)
    {
        this.mTaskStatusCallback = taskStatusCallback;
        mIsRunning = true;
        onTaskStart();
        if (NetUtil.checkNet(ContactsApp.getInstance()))
        {// 有网络时才发起http请求
            ErrorListener errorListener = new ErrorListener()
            {

                @Override
                public void onErrorResponse(VolleyError volleyError)
                {

                    onTaskFailure(PutaoExceptionTool.parseVolleyError(volleyError));
                    onTaskFinish();
                    mIsRunning = false;
                }
            };
            initRequest(null, errorListener);
            getRequestQueue().add(mRequest);
        }
        else
        {
            onTaskFailure(PutaoExceptionCode.EXCEPTION_CODE_NO_CONNECTION);
            onTaskFinish();
            mIsRunning = false;
        }
    }

    @Override
    public T syncParse() throws PutaoException
    {
        T t = null;
        RequestFuture<T> future = RequestFuture.newFuture();
        initRequest(future, future);
        future.setRequest(mRequest);
        getRequestQueue().add(mRequest);
        try
        {
            t = future.get(getCurrentTimeoutMs(), TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            LogUtil.w(TAG, "catch InterruptedException throw by sync.", e);
            throw new PutaoException();
        }
        catch (ExecutionException e)
        {
            LogUtil.w(TAG, "catch ExecutionException throw by sync.", e);
            PutaoExceptionTool.parseExecutionException(e);
        }
        catch (TimeoutException e)
        {
            LogUtil.w(TAG, "catch TimeoutException throw by sync.", e);
            throw new PutaoTimeOutException();
        }
        return t;
    }

    public RequestQueue getRequestQueue()
    {
        // 默认采用葡萄自带队列
        return VolleyQueue.getQueue();
    }

    @Override
    public void cancel()
    {
        cancelRequest();
    }

    private void cancelRequest()
    {
        if (mRequest != null)
        {
            mRequest.cancel();
            mRequest = null;
        }
    }

    @Override
    public boolean isRunning()
    {
        return mIsRunning;
    }

    @Override
    public int getMethod()
    {
        return mReqMethod;
    }

    @Override
    public int getPriority()
    {
        return mReqPriority;
    }

    /**
     * 获得volley对应的优先级枚举
     * 
     * @return
     */
    private Priority getVolleyPriority()
    {
        if (getPriority() == PRIORITY_LOW)
        {
            return Priority.LOW;
        }
        else if (getPriority() == PRIORITY_NORMAL)
        {
            return Priority.NORMAL;
        }
        else if (getPriority() == PRIORITY_HIGH)
        {
            return Priority.HIGH;
        }
        else if (getPriority() == PRIORITY_IMMEDIATE)
        {
            return Priority.IMMEDIATE;
        }
        return Priority.NORMAL;
    }

    @Override
    public Map<String, String> getParams()
    {
        return mReqParams;
    }

    @Override
    public String getPath()
    {
        if (getMethod() == GET)
        {
            if ((getParams() != null && !getParams().isEmpty()))
            {
                // 如果是get请求并且getParams()有参数就拼装参数到path中
                if (mReqPath == null)
                {
                    return null;
                }
                StringBuffer pathSb = new StringBuffer(mReqPath);
                if (pathSb.indexOf("?") == -1)// 如果没有问号就追加，支持mReqParams和mReqPath上等参数组合拼接
                {
                    pathSb.append("?");
                }
                String enparam = encodeParameters(getParams(), PROTOCOL_CHARSET);
                if (enparam.length() > 0)//
                {
                    pathSb.append(enparam);
                }
                mReqPath = pathSb.toString();
            }
            printInfo("GET>>Full req path:" + mReqPath);
        }
        return mReqPath;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded
     * encoded string.
     */
    private String encodeParameters(Map<String, String> params, String paramsEncoding)
    {
        StringBuilder encodedParams = new StringBuilder();
        try
        {
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                if (entry.getKey() != null && entry.getValue() != null)
                {
                    encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                    encodedParams.append('=');
                    encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                    encodedParams.append('&');
                }
            }
            String pstr = encodedParams.toString();
            if (pstr.length() > 0)
            {
                pstr = pstr.substring(0, pstr.length() - 1);
            }
            return pstr;
        }
        catch (UnsupportedEncodingException uee)
        {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    /**
     * 默认不压缩，需要压缩请重写该方法
     * 
     * @return
     */
    public boolean isZip()
    {
        return false;
    }

    private void initRequest(final Listener<T> listener, ErrorListener errorListener)
    {
        // add for print http req headers
        printInfo("Req headers-->" + getHeaders());
        // add for print http req params
        printInfo("Req params-->" + getParams());
        if (mReqContentType == ReqContentType.FORM)
        {
            mRequest = new Request<T>(getMethod(), getPath(), errorListener)
            {
                @Override
                protected void deliverResponse(T respObj)
                {
                    if (listener != null)
                    {
                        listener.onResponse(respObj);
                    }
                    if (respObj != null)
                    {
                        onTaskSuccess(respObj);
                    }
                    else
                    {
                        onTaskFailure(PutaoExceptionCode.EXCEPTION_CODE_PARSE_ERROR);
                    }
                    onTaskFinish();
                    mIsRunning = false;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    return AbstractParserTask.this.getParams();
                }

                /*
                 * modified by lxh 2015-4-29 start
                 * 重写该方法解决Volley调用encodeParameters时里面key或者value为null时报空指针的问题
                 */
                @Override
                public byte[] getBody() throws AuthFailureError
                {
                    Map<String, String> params = getParams();
                    if (params != null && params.size() > 0)
                    {
                        byte[] body = null;
                        try
                        {
                            String pstr = encodeParameters(params, PROTOCOL_CHARSET);
                            printInfo("POST>>Full req path:" + getPath() + "?" + pstr);
                            body = pstr.getBytes(PROTOCOL_CHARSET);
                        }
                        catch (UnsupportedEncodingException e)
                        {
                            e.printStackTrace();
                        }
                        return body;
                    }
                    return null;
                }

                // end 2015-4-29 by lxh

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    return AbstractParserTask.this.getHeaders();
                }

                @Override
                protected Response<T> parseNetworkResponse(NetworkResponse response)
                {
                    try
                    {
                        byte respData[] = response.data;
                        String realString = getRealString(respData);
                        // 此处打印服务器端请求返回
                        printInfo(realString);
                        return Response
                                .success(mParser.parse(realString), HttpHeaderParser.parseCacheHeaders(response));
                    }
                    catch (Exception e)
                    {
                        // Json转换等异常在此处理
                        return Response.error(new VolleyError(e.getCause()));
                    }
                }

                public Priority getPriority()
                {
                    return getVolleyPriority();
                }

            };
        }
        else if (mReqContentType == ReqContentType.JSON)
        {
            printInfo("mRequestBody:" + mRequestBody);
            mRequest = new JsonRequest<T>(getMethod(), getPath(), mRequestBody, null, errorListener)
            {
                @Override
                protected void deliverResponse(T respObj)
                {
                    if (listener != null)
                    {
                        listener.onResponse(respObj);
                    }
                    if (respObj != null)
                    {
                        onTaskSuccess(respObj);
                    }
                    else
                    {
                        onTaskFailure(PutaoExceptionCode.EXCEPTION_CODE_PARSE_ERROR);
                    }
                    onTaskFinish();
                    mIsRunning = false;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    return AbstractParserTask.this.getHeaders();
                }

                @Override
                protected Response<T> parseNetworkResponse(NetworkResponse response)
                {
                    try
                    {
                        byte respData[] = response.data;
                        String realString = getRealString(respData);
                        // 此处打印服务器端请求返回
                        printInfo(realString);
                        return Response
                                .success(mParser.parse(realString), HttpHeaderParser.parseCacheHeaders(response));
                    }
                    catch (Exception e)
                    {
                        // Json转换等异常在此处理
                        return Response.error(new VolleyError(e.getCause()));
                    }
                }

                public Priority getPriority()
                {
                    return getVolleyPriority();
                }

            };

        }

        mRequest.setRetryPolicy(new DefaultRetryPolicy(getCurrentTimeoutMs(), 0, 1f));
    }

    /**
     * 输出打印一些信息
     * 
     * @author lxh
     * @since 2015-6-8
     * @param content
     */
    private void printInfo(String content)
    {
        LogUtil.i(TAG, content);
    }

    /**
     * 如果是压缩数据就需要解压缩
     * 
     * @param data
     * @return
     */
    private String getRealString(byte[] data)
    {
        if (isZip())
        {
            byte[] h = new byte[2];
            h[0] = (data)[0];
            h[1] = (data)[1];
            int head = getShort(h);
            boolean t = head == 0x1f8b;
            InputStream in;
            StringBuilder sb = new StringBuilder();
            try
            {
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                if (t)
                {
                    in = new GZIPInputStream(bis);
                }
                else
                {
                    in = bis;
                }
                BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
                for (String line = r.readLine(); line != null; line = r.readLine())
                {
                    sb.append(line);
                }
                in.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return sb.toString();
        }
        else
        {
            return new String(data);
        }

    }

    private int getShort(byte[] data)
    {
        return (int) ((data[0] << 8) | data[1] & 0xFF);
    }

    @Override
    public boolean onTaskSuccess(T response)
    {
        if (mTaskStatusCallback != null)
        {
            return mTaskStatusCallback.onTaskSuccess(response);
        }
        return false;
    }

    @Override
    public boolean onTaskFailure(int errorCode)
    {
        if (mTaskStatusCallback != null)
        {
            return mTaskStatusCallback.onTaskFailure(errorCode);
        }
        return false;
    }

    @Override
    public void onTaskFinish()
    {
        if (mTaskStatusCallback != null)
        {
            mTaskStatusCallback.onTaskFinish();
        }
    }

    @Override
    public void onTaskStart()
    {
        if (mTaskStatusCallback != null)
        {
            mTaskStatusCallback.onTaskStart();
        }
    }

    @Override
    public void reload()
    {
        // no op
    }

    @Override
    public Map<String, String> getHeaders()
    {
        return defaultHeaders;
    }

    /**
     * 添加请求头文信息
     * 
     * @param key
     * @param value
     */
    public void addHeader(String key, String value)
    {
        getHeaders().put(key, value);
    }

}
