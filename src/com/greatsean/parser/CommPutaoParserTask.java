package com.greatsean.parser;

import java.util.Map;

import so.contacts.hub.basefunction.widget.CommEmptyView;
import android.content.Context;

/**
 * 葡萄后台通用请求JSON解析器
 * 
 * @author lixiaohui
 * @param <T>
 */
public class CommPutaoParserTask<T extends PutaoBaseResponse> extends CommDoJsonParserTask<T>
{

    /** 有数据返回成功 */
    public static final String RET_CODE_SUCCESS = "0000";

    /** 系统错误 */
    public static final String RET_CODE_SYSTEMERR = "99999";

    /** 无相关数据 */
    public static final String RET_CODE_NODATA = "6000";

    /** 授权失败-后台验证pt_token不存在或者过期 */
    public static final String RET_CODE_AUTHERROR = "10000";

    /** 88开头的返回码显示服务器error信息 */
    public static final String RET_CODE_SHOW_ERROR = "88";

    /** 促销活动名额超限 */
    public static final String RET_CODE_OVER_NUM = "88103";

    /** 促销活动超时 */
    public static final String RET_CODE_OVER_TIME = "88102";

    /**
     * 订单价格有误
     */
    public static final String RET_CODE_PRICE_ERROR = "12101";

    /**
     * 所选时间不可用
     */
    public static final String RET_CODE_TIME_INVALID = "12129";

    /**
     * 促销活动已结束
     */
    public static final String RET_CODE_ACTIVITY_FINISHED = "88104";

    /**
     * FORM表单形式提交参数
     * 
     * @param reqPath
     * @param reqParams
     * @param reqMethod
     * @param clazz
     * @param context
     * @param emptyView
     */
    public CommPutaoParserTask(String reqPath, Map<String, String> reqParams, int reqMethod, Class<T> clazz,
            Context context, CommEmptyView emptyView)
    {
        super(reqPath, reqParams, reqMethod, clazz, context, emptyView);
        setCurrentTimeoutMs(10000);// 将超时时间设置为10s*2
    }

    /**
     * JSON方式提交参数
     * 
     * @param reqPath
     * @param requestBody
     * @param clazz
     * @param context
     * @param emptyView
     */
    public CommPutaoParserTask(String reqPath, String requestBody, Class<T> clazz, Context context,
            CommEmptyView emptyView)
    {
        super(reqPath, requestBody, clazz, context, emptyView);
        setCurrentTimeoutMs(10000);// 将超时时间设置为10s*2
    }

    @Override
    public boolean isNoDataCode(T response)
    {
        return RET_CODE_NODATA.equals(response.getCode());
    }

    @Override
    public boolean isSuccessCode(T response)
    {
        return RET_CODE_SUCCESS.equals(response.getCode());
    }

    @Override
    public String getMsg(T response)
    {
        return response.getError();
    }
}
