package com.autism.chat.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.autism.chat.R;
import com.autism.chat.base.BaseActivity;
import com.autism.chat.bean.UserModel;

import org.greenrobot.eventbus.Subscribe;

import cn.bmob.newim.event.MessageEvent;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class RegisterActivity extends BaseActivity {

    private EditText name;
    private EditText password;
    private Button login;
    private EditText passwor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        initView();
        initData();
    }


    private void initView() {
        name = (EditText) findViewById(R.id.et_user);
        password = (EditText) findViewById(R.id.password);
        passwor = (EditText) findViewById(R.id.password2);
        login = (Button) findViewById(R.id.login2);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                judge();
            }
        });
    }


    private void initData() {
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    judge();
                    return true;
                }
                return false;
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.length() > 4;
    }


    private void judge() {
        name.setError(null);
        password.setError(null);

        String name2 = name.getText().toString();
        String password2 = password.getText().toString();
        String password3 = passwor.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(name2)) {
            name.setError("账号不能为空");
            focusView = name;
            cancel = true;
        } else if (!isEmailValid(name2)) {
            name.setError("账号长度不符合");
            focusView = name;
            cancel = true;
        }

        if (TextUtils.isEmpty(password2) | TextUtils.isEmpty(password3)) {
            password.setError("密码不能为空");
            focusView = password;
            cancel = true;
        } else if (TextUtils.isEmpty(password3)) {
            passwor.setError("密码不能为空");
            focusView = passwor;
            cancel = true;
        } else if (password2.length() < 5) {
            password.setError("密码长度不符合规则");
            focusView = password;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {

            if (password2.equals(password3)) {

                final ProgressDialog bar = new ProgressDialog(this);
                bar.setMessage("注册中");
                bar.setCanceledOnTouchOutside(false);
                bar.show();


                UserModel.getInstance().register(name2, password2, new LogInListener() {
                    @Override
                    public void done(Object o, BmobException e) {
                        if (e == null) {
                            bar.dismiss();
                            toast("注册成功");
                            start(LoginActivity.class, null, true);
                        } else {
                            bar.dismiss();
                            toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                        }
                    }
                });
            } else {
                password.setText("");
                passwor.setText("");
                passwor.setError("两次密码不一致");
                passwor.requestFocus();
                cancel = true;
            }

        }
    }

    @Subscribe
    public void onEventMainThread(MessageEvent event) {
    }
}
