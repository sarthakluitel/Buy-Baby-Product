package np.com.yourname.babybuy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

import np.com.yourname.babybuy.dashboard.DashboardActivity;
import np.com.yourname.babybuy.db.BabyBuyDatabase;
import np.com.yourname.babybuy.db.user.User;
import np.com.yourname.babybuy.db.user.UserDao;

public class LoginActivity extends AppCompatActivity {
    private ImageButton imageButtonBack;
    private TextInputEditText textInputEditTextEmail, textInputEditTextPassword;
    private MaterialButton materialButtonLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        imageButtonBack = findViewById(R.id.ib_back);
        textInputEditTextEmail = findViewById(R.id.tiet_login_email);
        textInputEditTextPassword = findViewById(R.id.tiet_login_password);
        materialButtonLogin = findViewById(R.id.mb_login);
        progressDialog = new ProgressDialog(this);

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startHomeScreenActivity();
            }
        });

        materialButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 validateLogin();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startHomeScreenActivity();
    }

    private void startHomeScreenActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
        startActivity(intent);
        finish();
    }

    private void validateLogin() {
        String email = textInputEditTextEmail.getText().toString().trim();
        String password = textInputEditTextPassword.getText().toString().trim();

        if (email.isEmpty() ) {
            textInputEditTextEmail.setError("email is empty");
        }
        if (password.isEmpty() ) {
            Toast.makeText(
                    LoginActivity.this,
                    "password is empty",
                    Toast.LENGTH_LONG
            ).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            textInputEditTextEmail.setError("enter valid email address");

        }
        else if (password.isEmpty()) {
            Toast.makeText(
                    LoginActivity.this,
                    "Please enter a valid password",
                    Toast.LENGTH_LONG
            ).show();
        }
        else {
            progressDialog.setMessage("Authenticating user...");
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    authenticateUserInDatabase(email, password);
                }
            }, 2000);
        }
    }


    private void authenticateUserInDatabase(String email, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BabyBuyDatabase babyBuyDatabase = BabyBuyDatabase
                            .getInstance(getApplicationContext());
                    UserDao userDao = babyBuyDatabase.getUserDao();
                    User user = userDao.getUserByLoginCredentials(
                            email.toLowerCase(Locale.ROOT),
                            password
                    );
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleLoginResultFromDatabase(user);
                        }
                    });
                } catch (Exception exception) {
                    exception.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleFailureResultFromDatabase();
                        }
                    });
                }
            }
        }).start();
    }

    private void handleLoginResultFromDatabase(User user) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (user != null) {
            Toast.makeText(
                    LoginActivity.this,
                    "Login Success",
                    Toast.LENGTH_LONG
            ).show();
            saveUserLoggedStateInSharedPreferences(user);
            startDashboardActivity();
        } else {
            Toast.makeText(
                    LoginActivity.this,
                    "User doesn't exist...",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void saveUserLoggedStateInSharedPreferences(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                "user_pref",
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("logged_user_email", user.email);
        editor.putString("logged_user_full_name", user.fullName);
        editor.putString("logged_user_address", user.address);
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    private void startDashboardActivity() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleFailureResultFromDatabase() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast.makeText(
                LoginActivity.this,
                "Login Failed. Please try after sometime...",
                Toast.LENGTH_LONG
        ).show();
    }
}