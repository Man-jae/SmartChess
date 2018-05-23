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

import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity{

    static EditText etId;
    EditText etPassword;
    String stEmail;
    String stPassword;
    Handler mHandler;
    Button btnCancel;

    private long lastPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etId = (EditText)findViewById(R.id.etId);                       // 아이디입력창
        etPassword = (EditText)findViewById(R.id.etPassword);       // 비밀번호입력창

        mHandler = new Handler();

        Button btnRegister = (Button)findViewById(R.id.btnRegister);    // 회원가입버튼
        Button btnLogin = (Button)findViewById(R.id.btnLogin);          // 로그인버튼
        btnCancel = (Button)findViewById(R.id.btnCancel) ;              // 취소 (앱종료)버튼

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("btnLog","btn_Register");
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);     // 회원가입 액티비티로
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stEmail = etId.getText().toString();
                stPassword = etPassword.getText().toString();
                if(stEmail.trim().isEmpty() || stPassword.trim().isEmpty()){       // 아이디, 비밀번호 공백 여부 확인 (trim으로 수정)
                    Toast.makeText(LoginActivity.this, "입력이 필요합니다.",Toast.LENGTH_SHORT).show();
                }

                else {
                    new Thread() {
                        @Override
                        public void run() {
                            doProcess();        // DB에 저장된 아이디,패스워드 비교 함수 호출
                        }
                    }.start();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("btnLog","btnCancel");

                moveTaskToBack(true);       //현재 실행중인 어플리케이션 백그라운드 전환

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
    }       // backpress 두번 눌리면 앱 종료

    private void doProcess(){

        etId = (EditText)findViewById(R.id.etId);
        etPassword = (EditText)findViewById(R.id.etPassword);

        String id = etId.getText().toString();
        String pw = etPassword.getText().toString();

        HttpClient client = new DefaultHttpClient();        // http client 라이브러리 사용
        HttpPost post = new HttpPost("http://192.168.0.113:8080/Chess/selectlogin.do");      // id, pass 확인

        ArrayList<NameValuePair> nameValues = new ArrayList<NameValuePair>();

        try {
            nameValues.add(new BasicNameValuePair("mid", URLDecoder.decode(id, "UTF-8")));      // 필드에 입력된 id, pw 값 nameValues에 저장해서 post
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
            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            Log.i("Insert Log", sb.toString());

            JSONObject jsonObject = new JSONObject(sb.toString());
            Log.i("Insert Log", jsonObject.getString("result"));

            String insert_result = jsonObject.getString("result");      // 서버에서 반환받을 값

            final String mnum = jsonObject.getString("num");
            final String nikname = jsonObject.getString("nikname");
            final String win = jsonObject.getString("win");
            final String lose = jsonObject.getString("lose");

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);        // 로그인할때 DB와 연결해서 받아온 정보를 SharedPreferences를 이용해서 저장해두고 다른 액티비티에서 불러 사용함
            // sharedPreferences 로 값 저장
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("mnum", mnum);             // 유저 번호
            editor.putString("player", nikname);        // 유저 닉네임
            editor.putString("win",win);                // 유저 승 횟수
            editor.putString("lose", lose);             // 유저 패 횟수
            editor.commit();

            if(insert_result.equals("good")){       // id,pw 일치 -> 서버에서 good 리턴
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this.getApplicationContext(),"반갑습니다",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),RoomActivity.class);     // room 액티비티로
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("mnum", mnum);
                        startActivity(intent);
                    }
                });
            }   // 로그인 성공
            else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this.getApplicationContext(),"정보를 다시 확인해주세요.",Toast.LENGTH_SHORT).show();
                    }
                });
            }   // 로그인 실패

        } catch (ClientProtocolException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (IOException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}// end class

