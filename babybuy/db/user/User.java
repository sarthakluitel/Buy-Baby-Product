package np.com.yourname.babybuy.db.user;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "user_table")
public class User implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "fullName")
    public String fullName;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "password")
    public String password;
}
