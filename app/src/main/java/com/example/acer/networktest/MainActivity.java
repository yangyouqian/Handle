package com.example.acer.networktest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends Activity implements View.OnClickListener {

    public static final int SHOW_RESPONSE = 0;
    private Button sendResquest;
    private TextView responeText;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case SHOW_RESPONSE:
                    String respone = (String) msg.obj;
//                在这里进行UI操作 将结果显示到界面上
                    //responeText.setText(respone);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        sendResquest = (Button) findViewById(R.id.send_request);
        responeText = (TextView) findViewById(R.id.response_text);
        sendResquest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_request){
            sendResquestWithHttpURLConnection();
        }
    }
//TODO  理解异步消息处理机制
    private void sendResquestWithHttpURLConnection() {
//        开启线程来发起网络请求；
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
//                    URL url = new URL("http://ecjtu.me/get_data.xml");
                    URL url = new URL("http://ecjtu.org:8083/api/Servlet/CourseApi2?classId=201321110401");
                    // TODO 为何10.0.2.2这个地址不可以
                    // http://ecjtu.me/get_data.xml
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(18000);
                    connection.setReadTimeout(18000);
                    InputStream in = connection.getInputStream();

                    FileOutputStream fos = new FileOutputStream("/data/data/com.example.acer.networktest/hosts.txt");
                    byte buf[] = new byte[1024];
                    int temp;
                    while ((temp = in.read(buf))!=-1){
                        fos.write(buf, 0,temp);
                    }

//                   下面对获取的输入流进行读取
                    //BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    /*StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine())!= null){
                        response.append(line);
                    }
                    String xmlData = response.toString();*/

                    in.close();
                    fos.close();
                    //reader.close();
                    Message message = new Message();
                    message.what = SHOW_RESPONSE;
//                    将服务器返回的结果存放到Message
                   // message.obj = response.toString();//这里可以把源代码输出到屏幕上
                    handler.sendMessage(message);
                    //parseXMLWithPull(xmlData);
//                    parseXMLWithSAX(xmlData);
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void parseXMLWithPull(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullPaser = factory.newPullParser();
            xmlPullPaser.setInput(new StringReader(xmlData));
            int eventType = xmlPullPaser.getEventType();//得到当前的解析事件
            String id = "";
            String name = "";
            String version = "";
            while (eventType != XmlPullParser.END_DOCUMENT){//XmlPullParser.END_DOCUMENT: XML文档的结束  getEventType()的返回值
                String nodeName = xmlPullPaser.getName();
                switch (eventType){
                   case XmlPullParser.START_TAG:{//XmlPullParser.START_TAG:标签的开始  getEventType()的返回值
                        if ("id".equals(nodeName)){
                            id = xmlPullPaser.nextText();
                        }else if("name".equals(nodeName)){
                            name = xmlPullPaser.nextText();
                        }else if("version".equals(nodeName)){
                            version = xmlPullPaser.nextText();
                        }
                        break;
                    }//完成某个解析的结点
                    case XmlPullParser.END_TAG:{//XmlPullParser.END_TAG:标签的结束  getEventType()的返回值
                        if ("app".equals(nodeName)){
                            Log.i("MainActivity","id is "+id);
                            Log.i("MainActivity","name is "+name);
                            Log.i("MainActivity","version is "+version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullPaser.next();// 获得下一个解析时间
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseXMLWithSAX(String xmlData){
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            ContentHandler handler = new ContentHandler();
//            将ContentHandler的实例设置到XMLReader中
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(new StringReader(xmlData)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
