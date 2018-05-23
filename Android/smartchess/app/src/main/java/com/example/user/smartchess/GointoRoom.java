package com.example.user.smartchess;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;


public class GointoRoom extends Activity{

    private TextView title_name;
    private Button btn;
    private String mnum1;

    String port;
    String mnum;
    String title;
    Intent intent;

    protected void onCreate(Bundle save) {
        super.onCreate(save);
        setContentView(R.layout.activity_gointoroom);       // 뷰와 코드 연결

        intent = getIntent();       // 인텐트 받기
        mnum = intent.getStringExtra("mnum");       // 유저 번호, 포트, 타이틀 받기
        port = intent.getStringExtra("port");
        title = intent.getStringExtra("title");

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        mnum1 = pref.getString("mnum1", "null");        // 방을 만든 유저의 번호

        Log.i("mnum1 = ", mnum1);

        title_name = (TextView)findViewById(R.id.title_name);   // 방 타이틀
        title_name.setText(title);

        btn = (Button)findViewById(R.id.chat_btn);
        btn.setOnClickListener(new View.OnClickListener() {     // 게임 room 입장 버튼
            @Override
            public void onClick(View v) {

                if(mnum1.equals("null")){       // 방만 생성되고 첫번째 플레이어가 입장하지 않은 경우
                    new Thread() {
                        @Override
                        public void run() {
                            update_mnum1_doProcess();

                            Intent intent = new Intent(GointoRoom.this, MainActivity.class);
                            Bundle bundle = new Bundle();

                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }.start();
                }
                else {      // 첫번째 플레이어가 입장한 경우 (내가 두번째 플레이어가 됨)
                    new Thread() {
                        @Override
                        public void run() {
                            update_mnum2_doProcess();

                            Intent intent = new Intent(GointoRoom.this, MainActivity.class);
                            Bundle bundle = new Bundle();

                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }.start();
                }


            }
        });
    }

    // mnum1 ->>>> update
    private void update_mnum1_doProcess(){

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        port = pref.getString("my_port", "null");       // 내 고유 포트번호
        mnum = pref.getString("mnum", "null");      // 내 유저 번호

        Log.i("portNUM ===== ", port);
        Log.i("mnum ===== ", mnum);

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://192.168.0.113:8080/Chess/mnum1_Update.do");      // DB JSON 주소

        ArrayList<NameValuePair> nameValues = new ArrayList<NameValuePair>();

        try {
            nameValues.add(new BasicNameValuePair("port", URLDecoder.decode(port, "UTF-8")));
            nameValues.add(new BasicNameValuePair("mnum1", URLDecoder.decode(mnum, "UTF-8")));

            post.setEntity(new UrlEncodedFormEntity(nameValues, "UTF-8"));

            Log.i(" > port == ", port);
            Log.i(" > mnum == ", mnum);


        } catch (UnsupportedEncodingException ex) {
            Log.i("insertLog", ex.toString());
        }

        try {
            HttpResponse response = client.execute(post);

            Log.i("Insert Log", response.toString());
            Log.i("Insert Log", "response.getStatusCode:" +response.getStatusLine().getStatusCode());

            for(Header x:response.getAllHeaders()){
                Log.i("Insert Log", x.toString());
            }
            InputStream is = null;
            if(response.getStatusLine().getStatusCode()>=300 &&
                    response.getStatusLine().getStatusCode()<400){
                Log.i("Insert Log", "response.getFirstHeader(\"Location\"):" +response.getFirstHeader("Location").toString());
                String location = response.getFirstHeader("Location").toString();
                String sPath = location.substring("Location: ".length());
                Log.i("Insert Log", "sPath:"+sPath);
                is = new URL("http://192.168.0.113:8080/Chess/").openStream();
            }else{
                is = response.getEntity().getContent();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            Log.i("Insert Log", sb.toString());

            JSONObject jsonObject = new JSONObject(sb.toString());
            Log.i("Insert Log", jsonObject.getString("result"));

            String insert_result = jsonObject.getString("result");


        } catch (ClientProtocolException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (IOException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }   // 첫번째 플레이어로 입장

    // mnum2 ->>>> update
    private void update_mnum2_doProcess(){

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        port = pref.getString("my_port", "null");
        mnum = pref.getString("mnum", "null");

        Log.i("portNUM ===== ", port);
        Log.i("mnum ===== ", mnum);

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://192.168.0.113:8080/Chess/mnum2_Update.do");      // DB JSON 주소

        ArrayList<NameValuePair> nameValues = new ArrayList<NameValuePair>();

        try {
            nameValues.add(new BasicNameValuePair("port", URLDecoder.decode(port, "UTF-8")));
            nameValues.add(new BasicNameValuePair("mnum2", URLDecoder.decode(mnum, "UTF-8")));

            post.setEntity(new UrlEncodedFormEntity(nameValues, "UTF-8"));

            Log.i(" > port == ", port);
            Log.i(" > mnum == ", mnum);


        } catch (UnsupportedEncodingException ex) {
            Log.i("insertLog", ex.toString());
        }

        try {
            HttpResponse response = client.execute(post);

            Log.i("Insert Log", response.toString());
            Log.i("Insert Log", "response.getStatusCode:" +response.getStatusLine().getStatusCode());

            for(Header x:response.getAllHeaders()){
                Log.i("Insert Log", x.toString());
            }
            InputStream is = null;
            if(response.getStatusLine().getStatusCode()>=300 &&
                    response.getStatusLine().getStatusCode()<400){
                Log.i("Insert Log", "response.getFirstHeader(\"Location\"):" +response.getFirstHeader("Location").toString());
                String location = response.getFirstHeader("Location").toString();
                String sPath = location.substring("Location: ".length());
                Log.i("Insert Log", "sPath:"+sPath);
                is = new URL("http://192.168.0.113:8080/Chess/").openStream();
            }else{
                is = response.getEntity().getContent();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            Log.i("Insert Log", sb.toString());

            JSONObject jsonObject = new JSONObject(sb.toString());
            Log.i("Insert Log", jsonObject.getString("result"));

            String insert_result = jsonObject.getString("result");


        } catch (ClientProtocolException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (IOException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }   // 두번째 플레이어로 입장
}

