package np.com.yourname.babybuy.dashboard.recyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import np.com.yourname.babybuy.R;
import np.com.yourname.babybuy.db.product.Product;
import np.com.yourname.babybuy.utility.BitmapScalar;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerViewHolder> {

    // Context
    // List<Product>
    private Context context;
    private List<Product> productList;
    private IHomeRecyclerAdapterListener listener;

    public HomeRecyclerAdapter(
            Context context,
            List<Product> productList,
            IHomeRecyclerAdapterListener listener
    ) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeRecyclerViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        LayoutInflater layoutInflater = LayoutInflater
                .from(context);
        View view = layoutInflater.inflate(
                R.layout.item_home_recycler_view,
                parent,
                false
        );
        return new HomeRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull HomeRecyclerViewHolder holder,
            int position
    ) {
        Product product = productList.get(position);
        holder.getTvProductTitle().setText(product.title);
        holder.getTvProductDescription().setText(product.description);
        holder.getTvProductPrice().setText("£ " + product.price);
        if (product.markAsPurchased) {
            holder.getIvPurchased().setVisibility(View.VISIBLE);
        } else {
            holder.getIvPurchased().setVisibility(View.GONE);
        }
        loadImageWithGlide(holder.getIvProductImage(), product.image);
        holder.getClProductRootLayout().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClicked(product);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void loadImageWithGlide(ImageView imageView, String uriPath) {
        Glide.with(context)
                .load(Uri.parse(uriPath))
                .placeholder(R.drawable.ic_gallery)
                .into(imageView);
    }

    private Bitmap getBitmapForImageView(View view, String imageUriPath) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(),
                    Uri.parse(imageUriPath)
            );
            bitmap = BitmapScalar.stretchToFill(
                    bitmap,
                    100,
                    100
            );
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_gallery);
        }
        return bitmap;
    }

    public interface IHomeRecyclerAdapterListener {
        void onItemClicked(Product product);
    }
}
