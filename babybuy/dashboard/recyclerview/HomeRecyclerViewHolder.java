package np.com.yourname.babybuy.dashboard.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import np.com.yourname.babybuy.R;

public class HomeRecyclerViewHolder
        extends RecyclerView.ViewHolder {
    private ImageView ivProductImage;
    private TextView tvProductTitle;
    private TextView tvProductDescription;
    private TextView tvProductPrice;
    private ImageView ivPurchased;
    private ConstraintLayout clProductRootLayout;

    public HomeRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        ivProductImage = itemView.findViewById(R.id.iv_item_image);
        tvProductTitle = itemView.findViewById(R.id.tv_item_title);
        tvProductDescription = itemView.findViewById(R.id.tv_item_description);
        tvProductPrice = itemView.findViewById(R.id.tv_item_price);
        ivPurchased = itemView.findViewById(R.id.iv_purchased);
        clProductRootLayout = itemView.findViewById(R.id.cl_item_root);
    }

    public ImageView getIvProductImage() {
        return ivProductImage;
    }

    public TextView getTvProductTitle() {
        return tvProductTitle;
    }

    public TextView getTvProductDescription() {
        return tvProductDescription;
    }

    public TextView getTvProductPrice() {
        return tvProductPrice;
    }

    public ImageView getIvPurchased() {
        return ivPurchased;
    }

    public ConstraintLayout getClProductRootLayout() {
        return clProductRootLayout;
    }
}
