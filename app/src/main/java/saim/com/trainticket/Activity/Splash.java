package saim.com.trainticket.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import saim.com.trainticket.R;
import saim.com.trainticket.Utils.SharedPrefDatabase;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeLogin);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (TextUtils.isEmpty(new SharedPrefDatabase(getApplicationContext()).RetriveID())) {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }


            }
        }, 3000);
    }
}
