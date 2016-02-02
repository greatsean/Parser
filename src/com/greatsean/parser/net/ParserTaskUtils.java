package com.greatsean.parser.net;

public class ParserTaskUtils
{

    /**
     * 取消解析任务
     * 
     * @param task
     */
    public static void cancelParserTask(ParserTask task)
    {
        if (task != null)
        {
            task.cancel();
        }
    }
}
