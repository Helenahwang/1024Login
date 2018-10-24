package com.appdev.a503_02.a1024login;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText id, password;
    LinearLayout linearLayout;
    Button button;
    ProgressDialog progressDialog;

    //스레드로 작업 한 후 화면 갱신을 위한 객체
    //1개만 있으면 Message의 what을 구분해서 사용할 수 있기 때문에 바로 인스턴스 생성
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
                progressDialog.dismiss();

                if(msg.what == 1){
                    //linearLayout.setBackgroundColor(Color.DKGRAY);
                    Toast.makeText(MainActivity.this, "로그인 실패하였습니다", Toast.LENGTH_LONG).show();
                }else if(msg.what == 2){
                    //linearLayout.setBackgroundColor(Color.BLUE);
                    Toast.makeText(MainActivity.this, "로그인 성공하였습니다", Toast.LENGTH_LONG).show();

                }
            }



    };


    //비동기적으로 작업을 수행하기 위한 스레드 클래스
    //스레드는 인스턴스 재사용이 안되기 때문에, 필요할 때마다 클래스를 불러 인스턴스가 생성될 수 있게 하기 클래스로 만든다.
    class ThreadEx extends Thread{
        @Override
        public void run(){
            try{
                String addr = "http://192.168.0.230:8080/1024class/login?logid=";
                String id1 = id.getText().toString();
                String pw1 = password.getText().toString();
                addr = addr + id1 + "&logpw=" + pw1;

                //문자열 주소를 URL로 변경
                URL url = new URL(addr);

                //연결 객체 생성
                HttpURLConnection con = (HttpURLConnection)url.openConnection();

                //옵션 설정
                con.setUseCaches(false);//캐시(로컬에 저장해두고 사용) 사용 여부
                con.setConnectTimeout(30000); // 접속을 시도하는 최대 시간
                //30초 동안 접속이 안되면 예외를 발생시킨다.

                //문자열 다운로드를 받을 스트림 생성
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                //문자열 다운로드
                StringBuilder sb = new StringBuilder();

                while(true){
                    String line = br.readLine();

                    if(line==null){
                        break;
                    }

                    sb.append(line+"\n");
                }
                br.close();
                con.disconnect();
                //Log.e("data for downloading", sb.toString());


                //파싱할 때
                JSONObject result = new JSONObject(sb.toString()); //{ }를 없애기 위해 JSONObject 파싱함 {"nickname":"btsjk","id":"jungkook"}
                String x = result.getString("id");

                //파싱한 결과를 가지고 Message의 what을 달리해서 핸들러에게 전송
                Message msg = new Message();

                if(x.equals("null")){
                    //Log.e("로그인 여부", "실패");
                    msg.what=1;
                }else{
                    //Log.e("로그인 여부", "성공");
                    msg.what=2;
                }

                handler.sendMessage(msg);


            }catch (Exception e){
                Log.e("다운로드 실패", e.getMessage());
            }

        }
    }


    //Activity가 만들어질 때 호출되는 메소드 : onCreate
    //Activity가 실행될 때 무엇인가를 하고자 하는 경우는 onResume 메소드를 사용해야 한다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //layout 파일을 읽어서 메모리에 로드한 후 화면 출력을 준비하는 메소드를 호출
        setContentView(R.layout.activity_main);


        id=(EditText)findViewById(R.id.id);
        password=(EditText)findViewById(R.id.passwd);
        linearLayout=(LinearLayout)findViewById(R.id.layout01);
        button=(Button) findViewById(R.id.loginButton);

        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                //진행 대화상자를 출력
                progressDialog = ProgressDialog.show(MainActivity.this,"로그인","로그인 처리 중");

                //스레드를 만들어서 실행
                ThreadEx th = new ThreadEx();
                th.start();

            }
        });



    }
}
