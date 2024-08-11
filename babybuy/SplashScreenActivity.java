package np.com.yourname.babybuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import np.com.yourname.babybuy.dashboard.DashboardActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserLogin();
            }
        }, 3000);
    }

    private void checkUserLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                "user_pref",
                MODE_PRIVATE
        );
        boolean defaultValue = false;
        boolean isLoggedAlready = sharedPreferences
                .getBoolean("is_logged_in", defaultValue);

        Intent intent;
        if (isLoggedAlready) {
            intent = new Intent(
                    SplashScreenActivity.this,
                    DashboardActivity.class
            );
        } else {
            intent = new Intent(
                    SplashScreenActivity.this,
                    HomeScreenActivity.class
            );
        }
        startActivity(intent);
        finish();
    }
}