package com.example.user.smartchess;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.naver.naverspeech.client.databinding.ActivityMain2Binding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class RoomActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    final Handler listHandler = new Handler();

    Button btn_score;
    Button btn_logout;

    String frnum;
    String mnum1;
    String mnum2;
    String rnum;
    String port;
    String win;
    String lose;
    String player;

    String title;
    Intent intent;

    private long lastPressed;

    private ArrayList<String> rtitle;
    private static final int LAYOUT = R.layout.activity_main2;
    private ActivityMain2Binding mainBinding;
    private RecyclerView.Adapter adapter;

    private ArrayList<RecyclerItem> mItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, LAYOUT);

        intent = getIntent();
        mnum1 = intent.getStringExtra("mnum"); //이전 activity에서 mnum받아옴

        class Thread1 extends Thread {
            @Override
            public void run() {
                getroomlist();
            }
        }

        Thread1 t1 = new Thread1();

        t1.setPriority(1);
        t1.start();

        btn_score = (Button) findViewById(R.id.btn_score);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        btn_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);

                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                player = pref.getString("player", "null");
                lose = pref.getString("lose", "null");  // 현재 자신의 패 전적
                win = pref.getString("win", "null");  // 현재 자신의 승 전적

                // 알림창의 속성 설정
                builder.setTitle("전적")
                        .setMessage("플레이어 : "+player+" /  승리 : "+win+" /  패배 : "+lose)
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){

                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
                // 알림창의 속성 설정
                builder.setTitle("LOG-OUT")
                        .setMessage("로그아웃 하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기
            }
        });

    }

    ArrayList<String> roominfo;
    ArrayList<String> roomcheck;
    ArrayList<String> rnumcheck;
    ArrayList<String> rport;
    ArrayList<String> rmum1;

    public void getroomlist() {

        HttpURLConnection conn = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            roominfo = new ArrayList<>();
            roomcheck = new ArrayList<>();
            rnumcheck = new ArrayList<>();
            rport = new ArrayList<>();
            rtitle = new ArrayList<>();
            rmum1 = new ArrayList<>();

            URL url = new URL("http://192.168.0.113:8080/Chess/ARoomSeletAll.do");
            conn = (HttpURLConnection) url.openConnection();
            Log.i("RoomActivity", "getContentType>>" + conn.getContentType());
            Log.i("RoomActivity", "getResponseMessage>>" + conn.getResponseMessage());
            Log.i("RoomActivity", "getResponseCode>>" + conn.getResponseCode());
            Log.i("RoomActivity", "getContentLength>>" + conn.getContentLength());

            is = conn.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String str = null;
            StringBuilder sb = new StringBuilder();

            if ((str = br.readLine()) != null) {
                sb.append(str);
            } else {
                sb.append("");
            }

            if (sb.toString() != "") {
                final String txtJSON = sb.toString();
                try {
                    JSONArray jsonArray = new JSONArray(txtJSON);

                    Log.i("RoomActivity", jsonArray.toString() + "확인!");
                    Log.i("RoomActivity", jsonArray.length() + "length!");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        title = jsonObject.getString("title");
                        Log.i("RoomActivity", "titles" + title);

                        port = jsonObject.getString("port");
                        Log.i("RoomActivity", "ports" + port);

                        rnum = jsonObject.getString("rnum");
                        Log.i("RoomActivity", "rnums" + rnum);

                        mnum1 = jsonObject.getString("mnum1");
                        Log.i("RoomActivity", "mnum1s" + mnum1);

                        mnum2 = jsonObject.getString("mnum2");
                        Log.i("RoomActivity", "mnum2s" + mnum2);

                        roominfo.add(rnum + "split" + mnum1 + "split" + mnum2 + "split" + port + "split" + title);
                        roomcheck.add(mnum2);
                        rnumcheck.add(rnum);
                        rport.add(port);
                        rtitle.add(title);
                        rmum1.add(mnum1);

                    }
                    Log.i("RoomActivity", rtitle.size() + "size!");
                    mItems.clear();
                    for (String name : rtitle) {
                        mItems.add(new RecyclerItem(name));
                    }

                    Log.i("RoomActivity", mItems.size() + "size!");

                    int length = jsonArray.length() - 1;
                    if (length>0) {
                        frnum = rnumcheck.get(length);
                    }else{
                        frnum = "0";
                    }

                    Log.i("jsonArray.length()", "" + jsonArray.length());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                port = "-";
                title = "-";

            }

            listHandler.post(new Runnable() {
                @Override
                public void run() {
                    setRecyclerView();
                }
            });setRefresh();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }

        }
    }

    @Override
    public void onBackPressed() {

        if(System.currentTimeMillis() - lastPressed < 1500){

            moveTaskToBack(true);

        }
        Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastPressed = System.currentTimeMillis();
    }

    private void setRecyclerView() {
        Log.i("RoomActivity", "setRecyclerView().....");
        mainBinding.recyclerView.setHasFixedSize(true);

        // RecyclerView 에 Adapter 를 설정해줍니다.
        adapter = new RecyclerAdapter(mItems);

        mainBinding.recyclerView.setAdapter(adapter);

        mainBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(this).getOrientation());
        mainBinding.recyclerView.addItemDecoration(dividerItemDecoration);

        mainBinding.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), mainBinding.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {


                        if (roomcheck.get(position).toString() == "null") {
                            Toast.makeText(getApplicationContext(), "즐거운 게임!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), GointoRoom.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Log.i("asdasd=== ", rport.get(position));
                            intent.putExtra("roominfo", roominfo.get(position));
                            intent.putExtra("port", rport.get(position));
                            intent.putExtra("title", rtitle.get(position));

                            Log.i("RoomActivity (man) = ", rmum1.get(position).toString());

                            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);    // sharedPreferences 로 값 저장
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("my_port", rport.get(position));
                            editor.putString("mnum1",rmum1.get(position).toString());
                            editor.commit();

                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "입장이 완료된 방입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        );

    }

    private void setRefresh() {
        mainBinding.swipeRefreshLo.setOnRefreshListener(this);
        mainBinding.swipeRefreshLo.setColorSchemeColors(getResources().getIntArray(R.array.google_colors));
    }

    @Override
    public void onRefresh() {
        mainBinding.recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {

                class Thread2 extends Thread {
                    @Override
                    public void run() {
                        getroomlist();
                    }
                }
                Thread2 t2 = new Thread2();
                t2.start();
                Snackbar.make(mainBinding.recyclerView, "Refresh Success", Snackbar.LENGTH_SHORT).show();
                mainBinding.swipeRefreshLo.setRefreshing(false);
            }
        }, 500);
    }

    private void setData() {
        Log.i("RoomActivity", "setData().....");
        mItems.clear();
        // RecyclerView 에 들어갈 데이터를 추가합니다.
        Log.i("rtitle",rtitle.toString());
        for (String name : rtitle) {
            mItems.add(new RecyclerItem(name));
        }
        adapter.notifyDataSetChanged();
    }

}

