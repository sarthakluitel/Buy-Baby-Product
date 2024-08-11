package np.com.yourname.babybuy.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import np.com.yourname.babybuy.R;
import np.com.yourname.babybuy.db.user.User;

public class DashboardActivity extends AppCompatActivity {
    public static String KEY_USER_EMAIL = "user_email";
    public static String KEY_USER_PASSWORD = "user_password";
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private PurchasedFragment purchasedFragment;
    private ProfileFragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        User user = getLoggedUserDataFromSharedPreference();
        homeFragment = HomeFragment.newInstance(user);
        purchasedFragment = PurchasedFragment.newInstance();
        profileFragment = ProfileFragment.newInstance(user);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(
                    @NonNull MenuItem item
            ) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.home:
                        loadFragmentInContainer(homeFragment);
                        break;


                    case R.id.purchased:
                        loadFragmentInContainer(purchasedFragment);
                        break;

                    case R.id.profile:
                        loadFragmentInContainer(profileFragment);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.home);

        //Reading data from SharedPreferences

    }

    private User getLoggedUserDataFromSharedPreference() {
        User user = new User();
        SharedPreferences sharedPreferences = getSharedPreferences(
                "user_pref",
                MODE_PRIVATE
        );
        String defaultValue = "";
        user.email = sharedPreferences.getString("logged_user_email", defaultValue);
        user.fullName = sharedPreferences.getString("logged_user_full_name", defaultValue);
        user.address = sharedPreferences.getString("logged_user_address", defaultValue);
        return user;
    }

    /*
     * Loading Fragments in container
     */
    private void loadFragmentInContainer(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view, fragment);
        fragmentTransaction.commit();
    }
}