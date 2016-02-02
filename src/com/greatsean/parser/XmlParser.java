package com.greatsean.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

/**
 * xml同步解析器。
 * 
 * @author lixiaohui
 * @param <T> T是继承自Response的类型
 */
public class XmlParser<T extends Response> implements Parser<T>
{
    private static final String CHARSET = "UTF-8";

    /**
     * 使用sax解析xml时的DefaultHandler类
     */
    private BaseHandler<T> handler;

    /**
     * 构造方法
     * 
     * @param handler 使用sax解析xml时的DefaultHandler类
     */
    public XmlParser(BaseHandler<T> handler)
    {
        this.handler = handler;
    }

    @Override
    public T parse(byte[] result) throws Exception
    {
        T response = null;
        if (result != null)
        {
            // try {
            // } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
            // } catch (ParserConfigurationException e) {
            // e.printStackTrace();
            // } catch (SAXException e) {
            // e.printStackTrace();
            // } catch (IOException e) {
            // e.printStackTrace();
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(dealSpecialChar(result)), CHARSET);
            InputSource is = new InputSource(reader);
            is.setEncoding(CHARSET);
            SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            SAXParser parser = saxFactory.newSAXParser();
            parser.parse(is, handler);
            response = handler.getResult();
        }
        return response;
    }

    @Override
    public T parse(String result) throws Exception
    {
        return parse(result.getBytes());
    }

    /**
     * 处理特殊字符如：& 等字符
     * 
     * @param source
     * @return
     */
    private byte[] dealSpecialChar(byte[] source)
    {
        String str = new String(source);
        String temp = "#1z#1z#";
        str = str.replaceAll("&amp;", temp).replaceAll("&", "&amp;").replaceAll(temp, "&amp;");
        return str.getBytes();
    }
}
