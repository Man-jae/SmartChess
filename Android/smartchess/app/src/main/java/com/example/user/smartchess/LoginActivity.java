package com.example.user.smartchess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import java.net.URL;

import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;



public class LoginActivity extends AppCompatActivity{

    String TAG = "LoginActivity";
    static EditText etId;
    EditText etPassword;
    String stEmail;
    String stPassword;
    ProgressBar pbLogin;
    Handler mHandler;
    Button btnCancel;

    private long lastPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etId = (EditText)findViewById(R.id.etId);
        etPassword = (EditText)findViewById(R.id.etPassword);
        pbLogin = (ProgressBar)findViewById(R.id.pbLogin);

        mHandler = new Handler();

        Button btnRegister = (Button)findViewById(R.id.btnRegister);
        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        btnCancel = (Button)findViewById(R.id.btnCancel) ;

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("btnLog","btn_Register");
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stEmail = etId.getText().toString();
                stPassword = etPassword.getText().toString();
                if(stEmail.isEmpty() || stEmail.equals("") || stPassword.isEmpty() || stPassword.equals("")){
                    Toast.makeText(LoginActivity.this, "입력이 필요합니다.",Toast.LENGTH_SHORT).show();
                }

                else {
                    new Thread() {              // 스레드 사용.
                        @Override
                        public void run() {
                            doProcess();        // doProcess 함수 호출
                        }
                    }.start();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("btnLog","btnCancel");

                moveTaskToBack(true);

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

    @Override
    public void onBackPressed() {

        if(System.currentTimeMillis() - lastPressed < 1500){

            moveTaskToBack(true);

        }
        Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastPressed = System.currentTimeMillis();
    }

    private void doProcess(){

        etId = (EditText)findViewById(R.id.etId);
        etPassword = (EditText)findViewById(R.id.etPassword);

        String id = etId.getText().toString();
        String pw = etPassword.getText().toString();

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://192.168.0.113:8080/Chess/selectlogin.do");      // DB JSON 주소

        ArrayList<NameValuePair> nameValues = new ArrayList<NameValuePair>();

        try {
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

            final String mnum = jsonObject.getString("num");
            final String nikname = jsonObject.getString("nikname");
            final String win = jsonObject.getString("win");
            final String lose = jsonObject.getString("lose");

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            // sharedPreferences 로 값 저장
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("mnum", mnum);
            editor.putString("player", nikname);
            editor.putString("win",win);
            editor.putString("lose", lose);
            editor.commit();

            if(insert_result.equals("good")){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this.getApplicationContext(),"반갑습니다",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),RoomActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("mnum", mnum);
                        startActivity(intent);
                    }
                });
            }
            else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this.getApplicationContext(),"정보를 다시 확인해주세요.",Toast.LENGTH_SHORT).show();
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

}// end class

