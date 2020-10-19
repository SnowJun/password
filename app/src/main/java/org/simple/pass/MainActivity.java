package org.simple.pass;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.simple.password.PasswordView;

public class MainActivity extends AppCompatActivity {


    private LinearLayout lyRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lyRoot = findViewById(R.id.ly_root);


        PasswordView passwordView = new PasswordView(this);
        ViewGroup.LayoutParams params = passwordView.getLayoutParams();
        if (null == params) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        passwordView.setLayoutParams(params);

        passwordView.setHidden(false);
        passwordView.setOnlyCenter(false);

        passwordView.setPasswordFinishListener(new PasswordView.PasswordFinishListener() {
            @Override
            public void onPasswordFinish(String password) {
                Toast.makeText(MainActivity.this,"密码："+password,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String reason) {
                Toast.makeText(MainActivity.this,"密码："+reason,Toast.LENGTH_SHORT).show();
            }
        });

        lyRoot.addView(passwordView);
    }


}