package com.namlongsolutions.medicare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.namlongsolutions.medicare.R;
import com.namlongsolutions.medicare.ui_view.intro.AppIntro;
import com.namlongsolutions.medicare.ui_view.intro.slides.FirstSlide;
import com.namlongsolutions.medicare.ui_view.intro.slides.SecondSlide;
import com.namlongsolutions.medicare.ui_view.intro.slides.ThirdSlide;

public class WelcomeActivity extends AppIntro {
    private android.support.v7.app.ActionBar actionbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(t);


            }
        });
        Button btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(WelcomeActivity.this, SignupActivity.class);
                startActivity(t);


            }
        });

    }

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        addSlide(new FirstSlide());
        addSlide(new SecondSlide());
        addSlide(new ThirdSlide());
        setSlideOverAnimation();
    }


}
