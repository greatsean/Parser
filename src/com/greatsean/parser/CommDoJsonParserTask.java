package com.greatsean.parser;

import java.util.Map;

import com.greatsean.parser.net.JsonParserTask;
import com.greatsean.parser.net.ParserTask;

import so.contacts.hub.basefunction.net.exception.PutaoExceptionCode;
import so.contacts.hub.basefunction.utils.ToastUtil;
import so.contacts.hub.basefunction.utils.YellowUtil;
import so.contacts.hub.basefunction.widget.CommEmptyView;
import android.content.Context;
import android.text.TextUtils;

/**
 * 
 ************************************************ <br>
 * 文件名称: CommDoJsonParserTask.java <br>
 * 版权声明: <b>深圳市葡萄信息技术有限公司</b> 版权所有 <br>
 * 创建人员: lxh <br>
 * 文件描述: 通用处理操作集结于此<br>
 * 修改时间: 2016-1-15 下午1:32:17 <br>
 * 修改历史: 2016-1-15 1.00 初始版本 <br>
 ************************************************* 
 */
public abstract class CommDoJsonParserTask<T extends JsonResponse> extends JsonParserTask<T>
{
    /** 空视图 */
    private CommEmptyView mEmptyView;

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
    public CommDoJsonParserTask(String reqPath, Map<String, String> reqParams, int reqMethod, Class<T> clazz,
            Context context, CommEmptyView emptyView)
    {
        super(reqPath, reqParams, reqMethod, clazz, context);
        initData(emptyView);
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
    public CommDoJsonParserTask(String reqPath, String requestBody, Class<T> clazz, Context context,
            CommEmptyView emptyView)
    {
        super(reqPath, ParserTask.POST, requestBody, clazz, context);
        initData(emptyView);
    }

    /**
     * @param emptyView
     */
    private void initData(CommEmptyView emptyView)
    {
        addHeader("Accept-Encoding", "gzip");
        addHeader("Cookie", YellowUtil.getCookieParamVal());
        // 此处可定制Gson
        // ((JsonParser<T>)mParser).getConfig().setGson(new GsonBuilder());
        this.mEmptyView = emptyView;
        if (emptyView != null)
        {// 如果发起请求就隐藏空view
            emptyView.gone();
            emptyView.showBindview(true);
        }
    }

    @Override
    public boolean isZip()
    {
        return true;
    }

    @Override
    public boolean onTaskSuccess(T response)
    {
        if (isNoDataCode(response))
        {
            if (mEmptyView != null)
            {// 如果发起请求就显示无数据的view
                mEmptyView.showNodata();
                return true;
            }
        }
        else if (!isSuccessCode(response))
        {
            String msg = getMsg(response);
            // 此处处理些后台返回的公共的错误
            if (mContextRef != null && !TextUtils.isEmpty(msg))
            {
                Context context = mContextRef.get();
                if (context != null)
                {
                    ToastUtil.showShort(context, msg);
                }
                return true;
            }
        }
        return super.onTaskSuccess(response);
    }

    @Override
    public boolean onTaskFailure(int errorCode)
    {
        if (mEmptyView != null)
        {
            mEmptyView.setNonetworkTxt(PutaoExceptionCode.getExceptionMessageByCode(errorCode, false));
            mEmptyView.showNonetwork();
            return true;
        }
        else if (mContextRef != null)// 有Context就弹toast提示
        {
            Context context = mContextRef.get();
            if (context != null)
            {
                ToastUtil.showShort(context, PutaoExceptionCode.getExceptionMessageByCode(errorCode, true));
            }
            return true;
        }
        return super.onTaskFailure(errorCode);
    }

}
