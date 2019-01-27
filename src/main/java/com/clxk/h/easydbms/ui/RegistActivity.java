package com.clxk.h.easydbms.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.clxk.h.easydbms.R;
import com.clxk.h.easydbms.bean.User;

import java.io.IOException;

public class RegistActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_password;
    private Button btn_regist;
    private RadioGroup rg;
    private RadioButton rb_simple;
    private  RadioButton rb_super;
    private User user = new User();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        //find
        et_username = (EditText)findViewById(R.id.et_username);
        et_password = (EditText)findViewById(R.id.et_password);
        btn_regist = (Button)findViewById(R.id.btn_regist);
        rg = (RadioGroup)findViewById(R.id.rg);
        rb_simple = (RadioButton)findViewById(R.id.rb_simple);
        rb_super = (RadioButton)findViewById(R.id.rb_super);

        //setListener
        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean tag_1 = user.check(et_username.getText().toString().trim());
                boolean tag_2 = user.check(et_password.getText().toString().trim());
                Log.i("111111111111111111111","");
                if(!tag_1 || !tag_2) {
                    Toast.makeText(RegistActivity.this, "用户名或密码输入不合法! (3-15位)",Toast.LENGTH_SHORT).show();
                    et_username.setText("");
                    et_password.setText("");
                    et_username.requestFocus();
                    return;
                }
                Log.i("2222222222","");
                try {
                    if(user.is_have(et_username.getText().toString().trim())) {
                        Toast.makeText(RegistActivity.this, "当前用户名已存在！",Toast.LENGTH_SHORT).show();
                        et_username.setText("");
                        et_password.setText("");
                        et_username.requestFocus();
                        return;
                    } else {
                        user.insertIntoUser(et_username.getText().toString().trim(),et_password.getText().toString().trim());
                        Toast.makeText(RegistActivity.this,"注册成功！",Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(rb_simple.isChecked()) {
                    try {
                        user.insertIntoPro(et_username.getText().toString().trim(),1);
                        Intent intent = new Intent(RegistActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                } else if(rb_super.isChecked()) {
                    try {
                        user.insertIntoPro(et_username.getText().toString().trim(),2);
                        Intent intent = new Intent(RegistActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                } else {
                    Toast.makeText(RegistActivity.this,"请选择注册用户类型!",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}

