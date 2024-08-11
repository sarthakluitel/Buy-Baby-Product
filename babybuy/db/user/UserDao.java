package np.com.yourname.babybuy.db.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("Select * from user_table")
    List<User> getAllUsers();

    @Insert
    void insertUser(User user);

    @Query("Select * from user_table where email = :email and password = :password LIMIT 1")
    User getUserByLoginCredentials(String email, String password);

    @Delete
    void deleteUser(User user);
}
