package com.leancloud.im.guide.newdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.leancloud.im.guide.AVImClientManager;
import com.leancloud.im.guide.R;

/**
 * @ Author: qiyue (ustory)
 * @ Email: qiyuekoon@foxmail.com
 * @ Data:2016/3/27
 */
public class LoginActivity extends Activity {
    protected EditText userNameView;

    protected Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userNameView = (EditText)findViewById(R.id.activity_login_et_username);
        loginButton = (Button)findViewById(R.id.activity_login_btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openClient(userNameView.getText().toString().trim());
            }
        });
    }

    private void openClient(String selfId) {
        if (TextUtils.isEmpty(selfId)) {
            showToast(R.string.login_null_name_tip);
            return;
        }
/*
        //
        AVUser user = new AVUser();
        user.setUsername(selfId);
        user.setPassword("123456");
        user.setEmail("qweq@sdfas.com");
        user.put("phone","1233123131");
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                showToast("注册成功");
            }
        });
*/


        AVUser.logInInBackground(selfId, "123456", new LogInCallback() {
            public void done(AVUser user, AVException e) {
                if (e == null) {
                    // 登录成功
                    loginButton.setEnabled(false);
                    userNameView.setEnabled(false);
                    AVImClientManager.getInstance().open(AVUser.getCurrentUser().getObjectId(), new AVIMClientCallback() {
                        @Override
                        public void done(AVIMClient avimClient, AVIMException e) {
                            if (e==null) {
                                showToast("连接成功");
                                Intent intent = new Intent(LoginActivity.this,ChatActivity.class);
                                String memberId = "56f79fa9a34131004d50f16d";
                                if ("56f79fa9a34131004d50f16d".equals(AVUser.getCurrentUser().getObjectId())){
                                    memberId = "56f79f5ada2f60004c675e38";
                                }
                                intent.putExtra("memberId",memberId);
                                startActivity(intent);
                                finish();
                            }else{
                                showToast("连接失败");
                            }
                        }
                    });
                   // Intent intent = new Intent(LoginActivity.this,ChatActivity.class);

                    showToast("登录成功");

                } else {
                    // 登录失败
                    showToast("登录失败");
                }
            }
        });

    }


    protected void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();

    }
    protected void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }


}
