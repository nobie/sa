package com.lanyoung.sesameandroid;

import com.lanyoung.sesameandroid.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message; 

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * An example activity that http access web server
 * 
 * 
 * @see 
 */
public class ButtonActivity extends Activity {
	
	private static final String destUrl="http://192.168.1.11/cgi-bin/luci/entry";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);
		
        findViewById(R.id.Go_1).setOnClickListener(new Button.OnClickListener(){ 
            public void onClick(View v)
            {
                //Toast提示
//                Toast.makeText(getApplicationContext(), "连接。。。",
//                		Toast.LENGTH_LONG).show();
                new Thread(urlConn).start();
            }
        });
		
        findViewById(R.id.Go_2).setOnClickListener(new Button.OnClickListener(){ 
            public void onClick(View v)
            {
                //Toast提示
//                Toast.makeText(getApplicationContext(), "连接。。。",
//                		Toast.LENGTH_LONG).show();
                new Thread(httpClientConn).start();
            }
	     });
	}

	
//Access using HttpClient    
	Runnable httpClientConn = new Runnable() {

    	public void run(){
	        //DefaultHttpClient
	
	        DefaultHttpClient httpclient = new DefaultHttpClient();
	           EditText passView =(EditText) findViewById(R.id.pwd);
	            String password=passView.getText().toString();
 	
	        //HttpGet
	        HttpGet httpget = new HttpGet("http://192.168.1.11/cgi-bin/luci/entry?ledtoggle=GO&password=123");
//        	String destUrl="http://192.168.1.11/cgi-bin/luci/entry";
        
	        try {
	            //instantiate HttpPost object from the url address
	            HttpEntityEnclosingRequestBase httpPost =new HttpPost(destUrl);

	           List <NameValuePair> params=new ArrayList<NameValuePair>();
	           params.add(new BasicNameValuePair("ledtoggle", "GO"));
	           params.add(new BasicNameValuePair("password", password));
		        httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));

		        //ResponseHandler
		        ResponseHandler<String> responseHandler = new BasicResponseHandler();
	              //execute the post and get the response from servers
	              String content = httpclient.execute(httpPost, responseHandler);


	                Message msg = new Message();
	                Bundle data = new Bundle();
	                data.putString("resp",content);
	                msg.setData(data);
	                handler.sendMessage(msg);
	
	        } catch (Exception e) {
	
	            e.printStackTrace();
	            Log.e("httpClientConn Exception:", e.toString());
	
	        } finally {
	
		        httpclient.getConnectionManager().shutdown();
	        }
	    }
	};
	
	
	//Access using URLConnection    
   	Runnable urlConn = new Runnable() {

	    public void run(){
	 
            HttpURLConnection httpconn = null;
            EditText passView =(EditText) findViewById(R.id.pwd);
            String password=passView.getText().toString();
            try {   
//	        	String destUrl="http://192.168.1.11/cgi-bin/luci/entry";
	            // URL   
	            URL url = new URL(destUrl);   
	            // HttpURLConnection   
	            httpconn = (HttpURLConnection) url.openConnection();
	            httpconn.setDoOutput(true);
	            httpconn.setUseCaches(false); 
	            httpconn.setRequestProperty("Content-type", "application/x-www-form-urlencoded"); 
	            httpconn.setDoOutput(true);
	            httpconn.setRequestMethod("POST");
	            String params="ledtoggle=GO&password="+password;
	            httpconn.getOutputStream().write(params.getBytes());
	            httpconn.getOutputStream().flush();
	            httpconn.getOutputStream().close();
	       
	            if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {   
	                // InputStreamReader   
	                InputStreamReader isr = new InputStreamReader(httpconn.getInputStream(), "utf-8");   
	                int i;   
	                String content = "";   
	                // read   
	                while ((i = isr.read()) != -1) {   
	                    content = content + (char) i;   
	                }   
	                isr.close();   
	                
	                Message msg = new Message();
	                Bundle data = new Bundle();
	                data.putString("resp",content);
	                msg.setData(data);
	                handler.sendMessage(msg);
	            }   
	  
	        } catch (Exception e) {   
	            e.printStackTrace();   
	            Log.e("urlConn Exception:", e.toString());
	        }finally {
	            //disconnect   
	            if(httpconn!=null)
	            	httpconn.disconnect();
	        }
	    }
    };

    Handler handler = new Handler(){
       @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
    		final TextView statusView = (TextView)findViewById(R.id.fullscreen_content);
            Bundle data = msg.getData();
            String strResponse = data.getString("resp");
            String resMsg = strResponse.substring(strResponse.indexOf("<status>")+8, strResponse.indexOf("</status>"));
            statusView.setText(resMsg);
    		final TextView msgView = (TextView)findViewById(R.id.message);
    		resMsg = strResponse.substring(strResponse.indexOf("<message>")+9, strResponse.indexOf("</message>"));
    		msgView.setText(resMsg);
        }
    };    
    
}
