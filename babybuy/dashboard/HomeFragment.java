package np.com.yourname.babybuy.dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import np.com.yourname.babybuy.R;
import np.com.yourname.babybuy.dashboard.recyclerview.HomeRecyclerAdapter;
import np.com.yourname.babybuy.db.BabyBuyDatabase;
import np.com.yourname.babybuy.db.product.Product;
import np.com.yourname.babybuy.db.product.ProductDao;
import np.com.yourname.babybuy.db.user.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements
        HomeRecyclerAdapter.IHomeRecyclerAdapterListener {
    private TextView tvWelcome, tvNoItems;
    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;
    private User loggedInUser;

    public static HomeFragment newInstance(User user) {
        HomeFragment fragment = new HomeFragment();
        Bundle argument = new Bundle();
        argument.putSerializable("logged_user", user);
        fragment.setArguments(argument);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );
        tvNoItems = view.findViewById(R.id.tv_no_items);
        tvWelcome = view.findViewById(R.id.tv_welcome);
        fabAdd = view.findViewById(R.id.fab_add);
        recyclerView = view.findViewById(R.id.recycler_view_home);
        return view;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        String welcomeMessage;
        User loggedInUser = (User) requireArguments().getSerializable("logged_user");
        if (loggedInUser != null) {
             welcomeMessage = "Hi " + loggedInUser.fullName + ",\n\n" + "Welcome Again !!";
        } else {
            welcomeMessage = "Hi User," + "\n\n" + "Welcome Again !!";
        }
        tvWelcome.setText(welcomeMessage);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAddProductActivity();
            }
        });
        getProducts();
    }
    
    private void loadProductsInRecyclerView(List<Product> products) {
        if (products.isEmpty()) {
            tvNoItems.setVisibility(View.VISIBLE);
        } else {
            tvNoItems.setVisibility(View.GONE);
        }
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

    private void loadAddProductActivity() {
        Intent intent = new Intent(requireActivity(), AddProductActivity.class);
        startActivityForResult(intent, 1001);
    }

    private void loadDetailPageActivity(Product product) {
        Intent intent = new Intent(requireActivity(), DetailPageActivity.class);
        intent.putExtra("product_data", product);
        startActivityForResult(intent, 1002);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            //This is result from the AddProductActivity
            getProducts();
        } else if (requestCode == 1002) {
            //This is result from the DetailPageActivity
            getProducts();
        }
    }

    @Override
    public void onItemClicked(Product product) {
        loadDetailPageActivity(product);
    }

    private void getProducts() {
        new Thread(() -> getProductsFromDb()).start();
    }

    private void getProductsFromDb() {
        try {
            BabyBuyDatabase babyBuyDatabase = BabyBuyDatabase.getInstance(
                    requireActivity().getApplicationContext());
            ProductDao productDao = babyBuyDatabase.getProductDao();
            List<Product> products = productDao.getAllProducts();
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
}