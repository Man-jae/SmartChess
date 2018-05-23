package com.example.user.smartchess;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.smartchess.utils.AudioWriterPCM;
import com.naver.speech.clientapi.SpeechRecognitionResult;

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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AbsRuntimePermission {        // 마이크 권한요청 상속
    final Handler listHandler = new Handler();

    private static final int REQUEST_PERMISSION = 10;       // 마이크 권한설정

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "naver client ID";

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;

    private TextView txtResult;
    private TextView txtfinalResult;

    private Button btnStart;
    private Button btnBack;
    private String mResult;
    private String finalResult;
    private String port;

    private String emptyport;
    private int jsonEx = 0;

    private String mnum;
    private String lose;
    private String win;

    private String[] aaaa = new String[5];      //문자 판별을 위한 함수

    private Handler mHandler = new Handler();

    private AudioWriterPCM writer;

    private Socket s;               // 라즈베리 서버와 연결을 위함
    private PrintWriter pw;

    String hostv;
    int portv;


    private String[] abc(String[] identification_1){

        String[] redefinition = new String[5];

        if((identification_1[0].contains("alpha")) || (identification_1[0].contains("알파")) || (identification_1[0].contains("a")) ||
                (identification_1[0].contains("bravo")) || (identification_1[0].contains("브라보")) || (identification_1[0].contains("b")) ||
                (identification_1[0].contains("charlie")) || (identification_1[0].contains("찰리")) || (identification_1[0].contains("c")) ||
                (identification_1[0].contains("delta")) || (identification_1[0].contains("델타")) || (identification_1[0].contains("d")) ||
                (identification_1[0].contains("echo")) || (identification_1[0].contains("에코")) || (identification_1[0].contains("e")) ||
                (identification_1[0].contains("foxtrot")) || (identification_1[0].contains("폭스트롯")) || (identification_1[0].contains("f")) ||
                (identification_1[0].contains("golf")) || (identification_1[0].contains("골프")) || (identification_1[0].contains("g")) ||
                (identification_1[0].contains("hotel")) || (identification_1[0].contains("호텔")) || (identification_1[0].contains("h"))){

            if((identification_1[0].contains("alpha"))          || (identification_1[0].contains("알파")) || (identification_1[0].contains("a")))    { redefinition[0] = "A"; }
            else if((identification_1[0].contains("bravo"))     || (identification_1[0].contains("브라보"))  || (identification_1[0].contains("b")))  { redefinition[0] = "B"; }
            else if((identification_1[0].contains("charlie"))   || (identification_1[0].contains("찰리"))  || (identification_1[0].contains("c")))    { redefinition[0] = "C"; }
            else if((identification_1[0].contains("delta"))     || (identification_1[0].contains("델타"))  || (identification_1[0].contains("d")))    { redefinition[0] = "D"; }
            else if((identification_1[0].contains("echo"))      || (identification_1[0].contains("에코"))  || (identification_1[0].contains("e")))    { redefinition[0] = "E"; }
            else if((identification_1[0].contains("foxtrot"))   || (identification_1[0].contains("폭스트롯"))  || (identification_1[0].contains("f"))){ redefinition[0] = "F"; }
            else if((identification_1[0].contains("golf"))      || (identification_1[0].contains("골프"))  || (identification_1[0].contains("g")))    { redefinition[0] = "G"; }
            else if((identification_1[0].contains("hotel"))     || (identification_1[0].contains("호텔"))  || (identification_1[0].contains("h")))    { redefinition[0] = "H"; }


            if((identification_1[1].contains("1")) || (identification_1[1].contains("2")) ||
                    (identification_1[1].contains("3")) || (identification_1[1].contains("4")) ||
                    (identification_1[1].contains("5")) || (identification_1[1].contains("6")) ||
                    (identification_1[1].contains("7")) || (identification_1[1].contains("8")) ||
                    (identification_1[1].contains("원")) || (identification_1[1].contains("투")) ||
                    (identification_1[1].contains("쓰리")) || (identification_1[1].contains("포")) ||
                    (identification_1[1].contains("파이브")) || (identification_1[1].contains("식스")) ||
                    (identification_1[1].contains("세븐")) || (identification_1[1].contains("에잇"))){

                if((identification_1[1].contains("1"))          || (identification_1[1].contains("원")))      { redefinition[1] = "1"; }
                else if((identification_1[1].contains("2"))     || (identification_1[1].contains("투")))      { redefinition[1] = "2"; }
                else if((identification_1[1].contains("3"))     || (identification_1[1].contains("쓰리")))    { redefinition[1] = "3"; }
                else if((identification_1[1].contains("4"))     || (identification_1[1].contains("포")))      { redefinition[1] = "4"; }
                else if((identification_1[1].contains("5"))     || (identification_1[1].contains("파이브")))  { redefinition[1] = "5"; }
                else if((identification_1[1].contains("6"))     || (identification_1[1].contains("식스")))    { redefinition[1] = "6"; }
                else if((identification_1[1].contains("7"))     || (identification_1[1].contains("세븐")))    { redefinition[1] = "7"; }
                else if((identification_1[1].contains("8"))     || (identification_1[1].contains("에잇")))    { redefinition[1] = "8"; }

                redefinition[2] = " / ";

                if((identification_1[2].contains("alpha"))      || (identification_1[2].contains("알파"))     ||  (identification_1[2].contains("a")) ||
                        (identification_1[2].contains("bravo"))      || (identification_1[2].contains("브라보"))   ||  (identification_1[2].contains("b")) ||
                        (identification_1[2].contains("charlie"))    || (identification_1[2].contains("찰리"))     ||  (identification_1[2].contains("c")) ||
                        (identification_1[2].contains("delta"))      || (identification_1[2].contains("델타"))     ||  (identification_1[2].contains("d")) ||
                        (identification_1[2].contains("echo"))       || (identification_1[2].contains("에코"))     ||  (identification_1[2].contains("e")) ||
                        (identification_1[2].contains("foxtrot"))    || (identification_1[2].contains("폭스트롯")) || (identification_1[2].contains("f")) ||
                        (identification_1[2].contains("golf"))       || (identification_1[2].contains("골프"))     ||  (identification_1[2].contains("g")) ||
                        (identification_1[2].contains("hotel"))      || (identification_1[2].contains("호텔"))     || (identification_1[2].contains("h"))){

                    if((identification_1[2].contains("alpha"))          || (identification_1[2].contains("알파")) || (identification_1[2].contains("a")))    { redefinition[3] = "A"; }
                    else if((identification_1[2].contains("bravo"))     || (identification_1[2].contains("브라보")) || (identification_1[2].contains("b")))  { redefinition[3] = "B"; }
                    else if((identification_1[2].contains("charlie"))   || (identification_1[2].contains("찰리")) || (identification_1[2].contains("c")))    { redefinition[3] = "C"; }
                    else if((identification_1[2].contains("delta"))     || (identification_1[2].contains("델타")) || (identification_1[2].contains("d")))    { redefinition[3] = "D"; }
                    else if((identification_1[2].contains("echo"))      || (identification_1[2].contains("에코")) || (identification_1[2].contains("e")))    { redefinition[3] = "E"; }
                    else if((identification_1[2].contains("foxtrot"))   || (identification_1[2].contains("폭스트롯")) || (identification_1[2].contains("f"))){ redefinition[3] = "F"; }
                    else if((identification_1[2].contains("golf"))      || (identification_1[2].contains("골프")) || (identification_1[2].contains("g")))    { redefinition[3] = "G"; }
                    else if((identification_1[2].contains("hotel"))     || (identification_1[2].contains("호텔")) || (identification_1[2].contains("h")))    { redefinition[3] = "H"; }

                    if((identification_1[3].contains("1")) || (identification_1[3].contains("2")) ||
                            (identification_1[3].contains("3")) || (identification_1[3].contains("4")) ||
                            (identification_1[3].contains("5")) || (identification_1[3].contains("6")) ||
                            (identification_1[3].contains("7")) || (identification_1[3].contains("8")) ||
                            (identification_1[3].contains("원")) || (identification_1[3].contains("투")) ||
                            (identification_1[3].contains("쓰리")) || (identification_1[3].contains("포")) ||
                            (identification_1[3].contains("파이브")) || (identification_1[3].contains("식스")) ||
                            (identification_1[3].contains("세븐")) || (identification_1[3].contains("에잇"))){

                        if((identification_1[3].contains("1"))          || (identification_1[3].contains("원")))     { redefinition[4] = "1"; }
                        else if((identification_1[3].contains("2"))     || (identification_1[3].contains("투")))     { redefinition[4] = "2"; }
                        else if((identification_1[3].contains("3"))     || (identification_1[3].contains("쓰리")))   { redefinition[4] = "3"; }
                        else if((identification_1[3].contains("4"))     || (identification_1[3].contains("포")))     { redefinition[4] = "4"; }
                        else if((identification_1[3].contains("5"))     || (identification_1[3].contains("파이브"))) { redefinition[4] = "5"; }
                        else if((identification_1[3].contains("6"))     || (identification_1[3].contains("식스")))    { redefinition[4] = "6"; }
                        else if((identification_1[3].contains("7"))     || (identification_1[3].contains("세븐")))    { redefinition[4] = "7"; }
                        else if((identification_1[3].contains("8"))     || (identification_1[3].contains("에잇")))    { redefinition[4] = "8"; }


                    }
                }
            }
        }

        return redefinition;    // 음성인식 값을 원하는 좌표값으로 변환하여 리턴
    }       // 문자 판별 함수


    // Declare handler for handling SpeechRecognizer thread's Messages.
    static class RecognitionHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        RecognitionHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }   // 음성인식을 위한 핸들러

    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:      // 음성인식 기능 준비
                // Now an user can speak.
                txtResult.setText("Connected");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);
                txtResult.setText(mResult);
                break;

            case R.id.finalResult:      // 최종 인식 결과
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                for(String result : results) {      // results 에 음성인식 값 append
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                mResult = strBuf.toString();
                txtResult.setText(results.get(0)); // 맨 처음 결과 값이 가장 정확한 음성인식 결과값이라 그부분을 사용

                String[] identification_1 = new String[4];      // 음성인식 결과값을 현재 x,y 좌표 / 목표 x,y 좌표 값으로 나누어 담기

                identification_1 = results.get(0).split(" ");   // 공백으로 spilt

//                for(int i=0; i<identification_1.length; i++){   Log.i("(identification_1) :", identification_1[i]);  }    // identification_1 값 확인

                try{

                    aaaa = abc(identification_1);       // 좌표로 변환

                    StringBuilder strBuf2 = new StringBuilder();

//                    Log.i("testLog :", "Test : " + aaaa[0]);
//                    Log.i("testLog :", "Test : " + aaaa[1]);
//                    Log.i("testLog :", "Test : " + aaaa[2]);
//                    Log.i("testLog :", "Test : " + aaaa[3]);
//                    Log.i("testLog :", "Test : " + aaaa[4]);      // 변환값 확인

                    for (String result : aaaa){ strBuf2.append(result); }   // 변환값 append

                    finalResult = strBuf2.toString();       // finalResult == 최종 좌표
                    Log.i("finalResult", finalResult);

                    txtfinalResult.setText(finalResult);    // 좌표 출력

                    new Thread() {
                        @Override
                        public void run() {
                            coordi_doProcess();     // 좌표값 DB 저장 함수 실행
                        }
                    }.start();
                } catch (IndexOutOfBoundsException e) {     // 좌표값이 제대로 들어오지 않았을때

                    Log.i("IndexOut", "오류발생!!!!");
                    Toast.makeText(getApplicationContext(), "제대로 된 입력이 되지않았습니다. (좌표값 없음)", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }

                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;
        }
    }// 음성인식 처리 메인

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        hostv = bundle.getString("host");
        portv = bundle.getInt("port");

        class Thread1 extends Thread{
            @Override
            public void run() {
                testServer();
            }
        }

        Thread1 t1 = new Thread1();

        t1.start();

        txtResult = (TextView) findViewById(R.id.txt_result);            // 문자 판별한 결과값
        txtfinalResult = (TextView) findViewById(R.id.txt_final_result);  // 최종 판별 결과값
        btnStart = (Button) findViewById(R.id.btn_start);                 // 음성인식 시작버튼
        btnBack = (Button) findViewById(R.id.btn_back);                   // 뒤로가기 버튼

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    mResult = "";
                    txtResult.setText("Connecting...");
                    btnStart.setText(R.string.str_stop);
                    naverRecognizer.recognize();
                } else {
                    Log.d(TAG, "stop and wait Final Result");
                    btnStart.setEnabled(false);

                    naverRecognizer.getSpeechRecognizer().stop();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {     // 게임화면에서 메인화면으로 돌아가는 버튼
            @Override
            public void onClick(View v) {

                jsonEx = 0;

                new Thread() {              // 스레드 사용.
                    @Override
                    public void run() {
                        check_del();
                    }
                }.start();

            }
        });

        requestAppPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, R.string.msg,REQUEST_PERMISSION);
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    } // protected void onStart() 위에 추가!!

    @Override
    protected void onStart() {
        super.onStart();
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mResult = "";
        txtResult.setText("");
        btnStart.setText(R.string.str_start);
        btnStart.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        naverRecognizer.getSpeechRecognizer().release();
    }

    @Override
    public void onBackPressed() {

        jsonEx = 0;

        new Thread() {              // 스레드 사용.
            @Override
            public void run() {
                check_del();
            }
        }.start();      // 뒤로가기 버튼과 같은 기능

    } // 뒤로가기 기권

    /////////////////// 함수 모음 /////////////////

    // check_del 게임화면-> 메인화면으로 돌아갈 때 실행되는 함수
    private void check_del(){

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        port = pref.getString("my_port", "null");       // 방 포트 번호

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://192.168.0.113:8080/Chess/APortSelectOne.do");      // DB에서 해당 방 포트번호 확인

        ArrayList<NameValuePair> nameValues = new ArrayList<NameValuePair>();

        try {
            nameValues.add(new BasicNameValuePair("port", URLDecoder.decode(port, "UTF-8")));

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

        } catch (ClientProtocolException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (IOException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (JSONException e) {

            jsonEx = 1;
            emptyport = "null";     // 방이 존재하는지 확인


            e.printStackTrace();
        }

        if(jsonEx == 0){ emptyport = "1"; }

        if(emptyport == "null"){    // 방이 없을 경우 -> 게임이 끝나서 해당 포트 사라짐
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            // 알림창의 속성 설정
            builder.setTitle("알림")
                    .setMessage("대기방으로 이동합니다.")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener(){

                        public void onClick(DialogInterface dialog, int whichButton){

                            new Thread() {              // 스레드 사용.
                                @Override
                                public void run() {
                                    update_win_score();     // 게임승리 -> 게임 패배 케이스 없음
                                }
                            }.start();

                            finish();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                        // 취소 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton){
                            dialog.cancel();
                        }
                    });

            listHandler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();    // 알림창 띄우기
                }
            });
        }
        else {      // 방이 있을 경우 -> 게임이 끝나지 않은 상태라 기권 여부 표시
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            // 알림창의 속성 설정
            builder.setTitle("주의!")
                    .setMessage("지금 나가면 패배처리 됩니다. 기권 하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            new Thread() {
                                @Override
                                public void run() {
                                    delete_gameroom();      // 게임에서 기권했기 때문에 방 정보 삭제 함수 실행
                                }
                            }.start();

                            new Thread() {
                                @Override
                                public void run() {
                                    update_lose_score();       // 게임패배
                                }
                            }.start();

                            finish();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

            listHandler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }

    private void delete_gameroom(){     // 방 삭제

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        port = pref.getString("my_port", "null");

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://192.168.0.113:8080/Chess/roomdelete.do");      // 방 삭제

        ArrayList<NameValuePair> nameValues = new ArrayList<NameValuePair>();

        try {
            nameValues.add(new BasicNameValuePair("port", URLDecoder.decode(port, "UTF-8")));

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


        } catch (ClientProtocolException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (IOException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    // 좌표값 저장
    private void coordi_doProcess(){

        final String text_starta = aaaa[0];     // 음성인식 좌표값
        final String text_startx = aaaa[1];
        final String text_desta = aaaa[3];
        final String text_desty = aaaa[4];

        try{

            HttpClient client = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://192.168.0.113:8080/Chess/oinsertOK.do");     // 좌표값 DB 저장

            ArrayList<NameValuePair> nameValues = new ArrayList<NameValuePair>();
            try {
                nameValues.add(new BasicNameValuePair("starta", URLDecoder.decode(text_starta, "UTF-8")));
                nameValues.add(new BasicNameValuePair("startx", URLDecoder.decode(text_startx, "UTF-8")));
                nameValues.add(new BasicNameValuePair("desta", URLDecoder.decode(text_desta, "UTF-8")));
                nameValues.add(new BasicNameValuePair("desty", URLDecoder.decode(text_desty, "UTF-8")));

                Log.i("text_starta = ", text_starta);
                Log.i("text_startx = ", text_startx);
                Log.i("text_desta = ", text_desta);
                Log.i("text_desty = ", text_desty);

                post.setEntity(new UrlEncodedFormEntity(nameValues, "UTF-8"));

            } catch (UnsupportedEncodingException ex) {
                Log.e("Insert Log--", ex.toString());

            }

            try {
                HttpResponse response = client.execute(post);

                Log.i("Insert Log1", response.toString());
                Log.i("Insert Log2", "response.getStatusCode:" +response.getStatusLine().getStatusCode());

                for(Header x:response.getAllHeaders()){
                    Log.i("Insert Log3", x.toString());
                }
                InputStream is = null;
                if(response.getStatusLine().getStatusCode()>=300 &&
                        response.getStatusLine().getStatusCode()<400){
                    Log.i("Insert Log4", "response.getFirstHeader(\"Location\"):" +response.getFirstHeader("Location").toString());
                    String location = response.getFirstHeader("Location").toString();
                    String sPath = location.substring("Location: ".length());
                    Log.i("Insert Log5", "sPath:"+sPath);
                    is = new URL("http://192.168.0.113:8080/Chess/oinsertOK.do/"+sPath).openStream();
                }else{
                    is = response.getEntity().getContent();
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                JSONObject jsonObject = new JSONObject(sb.toString());

            } catch (ClientProtocolException ex) {
                Log.e("Insert Log", ex.toString());
            } catch (IOException ex) {
                Log.e("Insert Log", ex.toString());
            } catch (JSONException ex) {
                Log.e("Insert Log", ex.toString());
            }
        }catch (NullPointerException e){        // 좌표에 null 값이 있으면 toast 실행
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "제대로 된 입력이 되지 않았습니다. (Null)", Toast.LENGTH_LONG).show();
                }
            });

        }
    } // 좌표값 DB 저장 함수

    // 라즈베리파이에서 게임 room 서버생성
    private boolean testServer() {
        try {
            s = new Socket(hostv,portv);
            pw = new PrintWriter(new BufferedOutputStream(s.getOutputStream()),true);
            pw.println("room/chat/all/"+"a"+"/");
            return true;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    // 게임 승리 시 win+1
    private void update_win_score(){

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        port = pref.getString("my_port", "null");
        mnum = pref.getString("mnum", "null");
        lose = pref.getString("lose", "null");  // 현재 자신의 패 전적
        win = pref.getString("win", "null");  // 현재 자신의 승 전적

        Log.i("portNUM ===== ", port);
        Log.i("mnum ===== ", mnum);
        Log.i("win ===== ", win);
        Log.i("lose ===== ", lose);

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://192.168.0.113:8080/Chess/scoreupdateOK.do");      // win 값을 +1 후에 update

        ArrayList<NameValuePair> nameValues = new ArrayList<NameValuePair>();

        try {
            nameValues.add(new BasicNameValuePair("mnum", URLDecoder.decode(mnum, "UTF-8")));
            nameValues.add(new BasicNameValuePair("win", URLDecoder.decode(win+1, "UTF-8")));
            nameValues.add(new BasicNameValuePair("lose", URLDecoder.decode(lose, "UTF-8")));

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

            SharedPreferences.Editor editor = pref.edit();      // 승,패 정보 업데이트
            editor.putString("win",win);
            editor.putString("lose", lose);
            editor.commit();


        } catch (ClientProtocolException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (IOException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }       // '승' 함수

    // 패배시 lose +1
    private void update_lose_score(){

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        port = pref.getString("my_port", "null");
        mnum = pref.getString("mnum", "null");
        lose = pref.getString("lose", "null");  // 현재 자신의 패 전적
        win = pref.getString("win", "null");  // 현재 자신의 승 전적

        Log.i("portNUM ===== ", port);
        Log.i("mnum ===== ", mnum);
        Log.i("win ===== ", win);
        Log.i("lose ===== ", lose);

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://192.168.0.113:8080/Chess/scoreupdateOK.do");      // lose+1
        ArrayList<NameValuePair> nameValues = new ArrayList<NameValuePair>();

        try {
            nameValues.add(new BasicNameValuePair("mnum", URLDecoder.decode(mnum, "UTF-8")));
            nameValues.add(new BasicNameValuePair("win", URLDecoder.decode(win, "UTF-8")));
            nameValues.add(new BasicNameValuePair("lose", URLDecoder.decode(lose+1, "UTF-8")));

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

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("win",win);
            editor.putString("lose", lose);
            editor.commit();


        } catch (ClientProtocolException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (IOException ex) {
            Log.e("Insert Log", ex.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }       // '패' 함수

}

