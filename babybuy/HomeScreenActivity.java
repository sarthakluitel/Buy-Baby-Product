package np.com.yourname.babybuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class HomeScreenActivity extends AppCompatActivity {

    private MaterialButton mbProceedLogin;
    private MaterialButton mbProceedRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        mbProceedLogin = findViewById(R.id.mb_homescreen_login);
        mbProceedRegister = findViewById(R.id.mb_homescreen_register);

        mbProceedLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        HomeScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mbProceedRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        HomeScreenActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}