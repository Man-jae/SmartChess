package com.example.user.smartchess;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.user.smartchess.R.layout.activity_load;

public class LoadActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private TimerTask  timerTask;               // load 화면 표출 시간을 위해 사용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_load);
        Log.i("testLog","LoadActivity,onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("testLog","LoadActivity,onStart()");


        final long startTime = System.currentTimeMillis();      // 시간 처리용 메소드

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("timerTask", "timerTask.." + System.currentTimeMillis());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        long endTime = System.currentTimeMillis();
                        Log.i("timerTask", "endTime-startTime.." + (endTime-startTime));
                        if(endTime-startTime>=2000){
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));     // 로그인 액티비티로

                        }
                    }
                });     // 로딩화면 2초간 지속 후 Login 액티비티 실행

            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask,2000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("testLog","LoadActivity,onPause()");
        Log.i("timerTask", "timerTask.cancel()");
        timerTask.cancel();
    }

}//class
