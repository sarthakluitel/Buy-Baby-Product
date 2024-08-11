package np.com.yourname.babybuy.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import np.com.yourname.babybuy.R;
import np.com.yourname.babybuy.dashboard.recyclerview.HomeRecyclerAdapter;
import np.com.yourname.babybuy.db.BabyBuyDatabase;
import np.com.yourname.babybuy.db.product.Product;
import np.com.yourname.babybuy.db.product.ProductDao;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PurchasedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PurchasedFragment extends Fragment implements HomeRecyclerAdapter.IHomeRecyclerAdapterListener{

    private RecyclerView recyclerView;

    public static PurchasedFragment newInstance() {
        PurchasedFragment fragment = new PurchasedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_purchased,
                container,
                false
        );
        recyclerView = view.findViewById(R.id.recycler_view_purchased);
        return view;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        loadProducts();
    }

    private void loadProducts() {
        new Thread(() -> getProductsFromDb()).start();
    }

    private void getProductsFromDb() {
        try {
            BabyBuyDatabase babyBuyDatabase = BabyBuyDatabase.getInstance(
                    requireActivity().getApplicationContext());
            ProductDao productDao = babyBuyDatabase.getProductDao();
            List<Product> products = productDao.getPurchasedProducts();
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadProductsInRecyclerView(products);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            requireActivity(),
                            "Please reload page...",
                            Toast.LENGTH_SHORT
                    ).show();

                }
            });
        }
    }


    private void loadProductsInRecyclerView(List<Product> products) {
        HomeRecyclerAdapter recyclerAdapter = new HomeRecyclerAdapter(
                requireActivity(),
                products,
                this
        );
        recyclerView.setAdapter(recyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                requireActivity()
        );
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onItemClicked(Product product) {

    }
}