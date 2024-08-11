package np.com.yourname.babybuy.db.product;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "product")
public class Product implements Serializable {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title", defaultValue = "")
    public String title;

    @ColumnInfo(name = "description", defaultValue = "")
    public String description;

    @ColumnInfo(name = "price", defaultValue = "")
    public String price;

    @ColumnInfo(name = "image", defaultValue = "")
    public String image;

    @ColumnInfo(name = "latitude")
    public String latitude;

    @ColumnInfo(name = "longitude")
    public String longitude;

    @ColumnInfo(name = "mark_as_purchased", defaultValue = "false")
    public boolean markAsPurchased;
}
