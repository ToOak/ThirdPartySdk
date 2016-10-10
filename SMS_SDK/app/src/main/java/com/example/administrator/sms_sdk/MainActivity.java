package com.example.administrator.sms_sdk;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText phone, phoneRandom;
    private Button btnRegister, btnCheckPass;
    private EventHandler eh;
    private CountTimer countTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inttView();
        initEvent();
    }

    //初始化控件
    private void inttView() {
        phone = (EditText) findViewById(R.id.phone);
        phoneRandom = (EditText) findViewById(R.id.phoneRandom);
        btnCheckPass = (Button) findViewById(R.id.btnCheckPass);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        countTimer = new CountTimer(60000, 1000);
    }

    private void initEvent() {
        btnRegister.setOnClickListener(this);
        btnCheckPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCheckPass:
                if (phone.getText().toString().equals("") || phone.getText().toString().length() != 11
                        || (!phone.getText().toString().substring(0, 2).equals("13")) && (!phone.getText().toString().substring(0, 2).equals("15"))) {
                    Toast.makeText(MainActivity.this, "请正确输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendSMSRandom();
                countTimer.start();
                break;
            case R.id.btnRegister://点击了提交的按钮
                if (phoneRandom.getText().toString().length()!=4) {
                    Toast.makeText(MainActivity.this, "请输入正确验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                SMSSDK.submitVerificationCode("86", phone.getText().toString(), phoneRandom.getText().toString());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);//解除注册的回掉
    }

    //发送短信验证码的方法
    public void sendSMSRandom() {
        //初始化短信验证
        SMSSDK.initSDK(this, "fe42edb9cf9c", "d955f95b891af1d0f36842645d413432");
        //监听事件
        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, final Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        Message ms = Message.obtain();
                        ms.arg1 = 2;
                        ms.arg2 = 2;
                        ms.obj = data.toString().substring(21);
                        handler.sendMessage(ms);
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        Log.i("TTTTTTTTTT", data + "");
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {

                    Message ms = Message.obtain();
                    ms.arg1 = 1;
                    ms.arg2 = 1;
                    ms.obj = data.toString().substring(21);
                    handler.sendMessage(ms);
                    ((Throwable) data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
        SMSSDK.getVerificationCode("86", phone.getText().toString());
    }

    //每隔一分钟可以点击一次验证码
    public class CountTimer extends CountDownTimer {
        /**
         * @param millisInFuture    时间间隔是多长时间
         * @param countDownInterval 回调onTick方法，每隔多久执行一次
         */

        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //间隔时间内执行的操作
        @Override
        public void onTick(long millisUntilFinished) {
            //更新页面组件
            btnCheckPass.setText(millisUntilFinished / 1000 + "后重新发送");
            btnCheckPass.setBackgroundColor(Color.rgb(169, 169, 169));
            btnCheckPass.setClickable(false);
        }

        //间隔时间结束的时候才会调用
        @Override
        public void onFinish() {
            //更新页面的组件
            btnCheckPass.setText("获取验证码");
            btnCheckPass.setBackgroundColor(Color.rgb(255, 255, 255));
            btnCheckPass.setClickable(true);
        }
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String code = "";
            if (msg.arg1 == 1) {
                JSONObject object = null;
                try {
                    Log.i("TTTTTTTT", (msg.obj + ""));
                    object = new JSONObject(msg.obj + "");
                    code = object.getString("status");
                    //这里进行返回的码数来提醒用户
                    switch (code) {
                        case "466":
                            Toast.makeText(MainActivity.this, "校验的验证码为空", Toast.LENGTH_SHORT).show();
                            break;
                        case "467":
                            Toast.makeText(MainActivity.this, "校验验证码请求频繁", Toast.LENGTH_SHORT).show();
                            break;
                        case "468":
                            Toast.makeText(MainActivity.this, "需要校验的验证码错误", Toast.LENGTH_SHORT).show();
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("TTTTTTTT", e.getMessage());
                }
            }else if(msg.arg1 == 2){
                Toast.makeText(MainActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
            }
        }
    };
}
