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
import com.autism.chat.bean.User;
import com.autism.chat.bean.UserModel;

import org.greenrobot.eventbus.Subscribe;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends BaseActivity {

    private EditText name;
    private EditText password;
    private Button login;
    private TextView reg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        initView();
        initData();
    }

    private void initData() {
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    // judge(name2, password2);
                    return true;
                }

                return false;
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.length() > 4;
    }

    private boolean judge(String name2, String password2) {

        boolean cancel = false;
        View focusView = null;

        name.setError(null);
        password.setError(null);

        if (TextUtils.isEmpty(name2)) {
            name.setError("账号不能为空");
            focusView = name;
            cancel = true;
        } else if (!isEmailValid(name2)) {
            name.setError("账号长度不符合");
            focusView = name;
            cancel = true;
        }

        if (TextUtils.isEmpty(password2)) {
            password.setError("密码不能为空");
            focusView = password;
            cancel = true;
        } else if (password2.length() < 5) {
            password.setError("密码长度不符合规则");
            focusView = password;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        }

        return cancel;
    }

    private void initView() {
        name = (EditText) findViewById(R.id.et_user);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login3);
        reg = (TextView) findViewById(R.id.reg);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(RegisterActivity.class, null, true);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name2 = name.getText().toString();
                String password2 = password.getText().toString();
                if (!judge(name2, password2)) {

                    final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage("登陆中");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    UserModel.getInstance().login(name2, password2, new LogInListener() {
                        @Override
                        public void done(Object o, BmobException e) {
                            if (e == null) {
                                pd.dismiss();
                                //o就是当前的登陆对象
                                User user = (User) o;
                                //更新当前用户资料
                                BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar()));
                                start(Main2Activity.class, null, true);
                                toast("登陆成功");
                            } else {
                                pd.dismiss();
                                name.setError("账号或者密码错误");
                                name.requestFocus();
                                password.setText("");
                                toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                            }
                        }
                    });
                }
            }
        });
    }

    @Subscribe
    public void onEventMainThread(MessageEvent event) {
    }
}
