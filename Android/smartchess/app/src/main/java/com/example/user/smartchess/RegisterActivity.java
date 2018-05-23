package com.example.user.smartchess;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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



public class RegisterActivity extends AppCompatActivity {

    EditText etName;
    EditText etId;
    EditText etPassword;
    Button btnRegister;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mHandler = new Handler();
        etName = (EditText)findViewById(R.id.etName);                   // 이름 입력필드
        etId = (EditText)findViewById(R.id.etId);                       // id 입력필드
        etPassword = (EditText)findViewById(R.id.etPassword);           // pw 입력필드

        btnRegister = (Button)findViewById(R.id.btnRegister);           // 회원가입 버튼
        Button btnCancel = (Button)findViewById(R.id.btnCancel);            // 뒤로가기버튼

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread() {
                    @Override
                    public void run() {
                        doProcess();        // 회원가입 조건 판별 함수 호출
                    }
                }.start();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("btnLog","btn_Register");
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);    // 로그인화면으로
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onStop() {
        super.onStop();
    }


    private void doProcess(){

        etName = (EditText)findViewById(R.id.etName);
        etId = (EditText)findViewById(R.id.etId);
        etPassword = (EditText)findViewById(R.id.etPassword);

        String name = etName.getText().toString();
        String id = etId.getText().toString();
        String pw = etPassword.getText().toString();

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://192.168.0.113:8080/Chess/insertRegister.do");      // 입력정보 DB insert

        ArrayList<NameValuePair> nameValues = new ArrayList<NameValuePair>();

        try {
            Log.i("testLog", "name : " + name);
            nameValues.add(new BasicNameValuePair("nikname", URLDecoder.decode(name, "UTF-8")));
            nameValues.add(new BasicNameValuePair("mid", URLDecoder.decode(id, "UTF-8")));
            nameValues.add(new BasicNameValuePair("mpw", URLDecoder.decode(pw, "UTF-8")));

            post.setEntity(new UrlEncodedFormEntity(nameValues, "UTF-8"));

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

            if(insert_result.equals("insertgood")){                 // 가입정보 확인 완료
                Log.i("TEstlog", "insert result");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this.getApplicationContext(),"회원가입이 완료 되었습니다.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
            else {                                      // 동일아이디 존재, 입력값 없을 경우
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this.getApplicationContext(),"가입실패, 정보를 다시 확인해주세요.",Toast.LENGTH_SHORT).show();
                    }
                });
            }


        } catch (ClientProtocolException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (IOException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
