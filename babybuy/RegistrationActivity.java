package np.com.yourname.babybuy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import np.com.yourname.babybuy.db.BabyBuyDatabase;
import np.com.yourname.babybuy.db.user.User;
import np.com.yourname.babybuy.db.user.UserDao;


public class RegistrationActivity extends AppCompatActivity {

    private ImageButton imageButtonBack;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextFullName;
    private TextInputEditText textInputEditTextAddress;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextConfirmPassword;
    private MaterialButton materialButtonRegister;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        imageButtonBack = findViewById(R.id.ib_back);
        textInputEditTextEmail = findViewById(R.id.tiet_registration_email);
        textInputEditTextFullName = findViewById(R.id.tiet_registration_full_name);
        textInputEditTextAddress = findViewById(R.id.tiet_registration_address);
        textInputEditTextPassword = findViewById(R.id.tiet_registration_password);
        textInputEditTextConfirmPassword = findViewById(R.id.tiet_registration_confirm_password);
        materialButtonRegister = findViewById(R.id.mb_register);
        progressDialog = new ProgressDialog(this);
        materialButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startHomeScreenActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startHomeScreenActivity();
    }

    private void validateData() {
        String email = textInputEditTextEmail.getText().toString().trim();
        String fullName = textInputEditTextFullName.getText().toString().trim();
        String address = textInputEditTextAddress.getText().toString().trim();
        String password = textInputEditTextPassword.getText().toString().trim();
        String confirmPassword = textInputEditTextConfirmPassword.getText().toString().trim();
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";

        Pattern pattern = Pattern.compile(passwordRegex);
        String numberRegex = "\\d+";
        Pattern pattern2 = Pattern.compile(numberRegex);
        Matcher matcher2 = pattern2.matcher(fullName);
        Matcher matcher = pattern.matcher(password);


        if (email.isEmpty() )  {
            textInputEditTextEmail.setError("email is empty");
        }if (fullName.isEmpty()) {
            textInputEditTextFullName.setError("Please enter a valid name");
        }if (address.isEmpty()) {
            textInputEditTextAddress.setError("Please enter a valid address");
        }if (password.isEmpty()) {
            Toast.makeText(
                    RegistrationActivity.this,
                    "Password is empty",
                    Toast.LENGTH_SHORT
            ).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            textInputEditTextEmail.setError("Please enter a valid email");

        }
        else if (!matcher.matches()) {
            Toast.makeText(
                    RegistrationActivity.this,
                    "Please enter a password that is at least 8 characters long and contains at least one number, one uppercase letter, and one lowercase letter",
                    Toast.LENGTH_SHORT
            ).show();
        }
        else if (matcher2.find()) {
             textInputEditTextFullName.setError("Full name must not contain numeric value");

        }
        else if (password.length() < 8) {
            textInputEditTextPassword.setError("Password must be of at-least 8 character");

        }
        else if (!password.equalsIgnoreCase(confirmPassword)) {
            Toast.makeText(
                    RegistrationActivity.this,
                    "Password didn't match",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            progressDialog.setMessage("Registering user...");
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    insertUserInDatabase(email, fullName, address, password);
                }
            }, 2000);
        }
    }

    private void insertUserInDatabase(
            String email,
            String fullName,
            String address,
            String password
    ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    User user = new User();
                    user.email = email.toLowerCase(Locale.ROOT);
                    user.fullName = fullName;
                    user.address = address;
                    user.password = password;
                    BabyBuyDatabase babyBuyDatabase = BabyBuyDatabase
                            .getInstance(getApplicationContext());
                    UserDao userDao = babyBuyDatabase.getUserDao();
                    userDao.insertUser(user);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyRegistrationSuccess();
                        }
                    });
                } catch (Exception exception) {
                    exception.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyRegistrationFailure();
                        }
                    });
                }
            }
        }).start();
    }

    private void notifyRegistrationSuccess() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast.makeText(
                RegistrationActivity.this,
                "Registration Success",
                Toast.LENGTH_LONG
        ).show();
        startLoginActivity();
    }

    private void notifyRegistrationFailure() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast.makeText(
                RegistrationActivity.this,
                "Registration Failure. Please try again...",
                Toast.LENGTH_LONG
        ).show();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void startHomeScreenActivity() {
        Intent intent = new Intent(RegistrationActivity.this, HomeScreenActivity.class);
        startActivity(intent);
        finish();
    }
}