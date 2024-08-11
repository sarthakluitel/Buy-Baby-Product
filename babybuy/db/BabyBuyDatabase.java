package np.com.yourname.babybuy.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import np.com.yourname.babybuy.db.product.Product;
import np.com.yourname.babybuy.db.product.ProductDao;
import np.com.yourname.babybuy.db.user.User;
import np.com.yourname.babybuy.db.user.UserDao;

@Database(entities = {User.class, Product.class}, version = 1)
public abstract class BabyBuyDatabase extends RoomDatabase {
    public abstract UserDao getUserDao();
    public abstract ProductDao getProductDao();
    private static BabyBuyDatabase INSTANCE;

    public static BabyBuyDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context,
                    BabyBuyDatabase.class,
                    "baby-buy.db"
            ).build();
        }
        return INSTANCE;
    }
}
