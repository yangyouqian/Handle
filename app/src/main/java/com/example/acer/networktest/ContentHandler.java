package com.example.acer.networktest;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by acer on 2016/3/24.
 */
public class ContentHandler extends DefaultHandler {
    private String nodeName;
    private StringBuilder id;
    private StringBuilder name;
    private StringBuilder version;

    @Override
    public void startDocument() throws SAXException {
        //在开始解析XML时调用
        id = new StringBuilder();
        name = new StringBuilder();
        version = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //开始解析某个结点的时候调用
        nodeName = localName;//记录当前结点名
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //获得结点中内容的时候调用

        //根据当前的结点名判断将内容添加到哪一个StringBuolder对象中
        if ("id".equals(nodeName)){
            id.append(ch, start, length);
        }else if ("name".equals(nodeName)){
            name.append(ch, start, length);
        }else if ("version".equals(nodeName)){
            version.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("app".equals(localName)){
            Log.i("ContentHandler","id is" + id.toString().trim());
            Log.i("ContentHandler","name is" + name.toString().trim());
            Log.i("ContentHandler","version is" + version.toString().trim());
            //将StringBuilder清空掉
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }

    @Override
    public void endDocument() throws SAXException {

    }
}
