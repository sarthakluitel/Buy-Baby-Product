package np.com.yourname.babybuy;

import java.io.Serializable;

/**
 * Created  on 23/11/2022.
 */
public class UserCredentials implements Serializable {
    private final String userEmail;
    private final String userPassword;

    public UserCredentials(String userEmail, String userPassword) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }
}
