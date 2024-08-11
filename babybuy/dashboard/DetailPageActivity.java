package np.com.yourname.babybuy.dashboard;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import np.com.yourname.babybuy.R;
import np.com.yourname.babybuy.db.BabyBuyDatabase;
import np.com.yourname.babybuy.db.product.Product;
import np.com.yourname.babybuy.db.product.ProductDao;
import np.com.yourname.babybuy.utility.BitmapScalar;

public class DetailPageActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Product product;
    private ImageButton ibBack;
    private ImageView ivDetailImage;
    private TextView tvProductTitle, tvProductPrice, tvProductDescription;
    private ImageButton ibEdit, ibDelete, ibSendSms;
    private MaterialCheckBox cbMarkAsPurchased;
    private GoogleMap googleMap;
    private AlertDialog sendSmsAlertDialog;
    private TextInputEditText tietSendSmsMobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        //Receive the data from intent
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product_data");
        //use this product data to set on the detail page
        initViews();
        assignViews();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (product == null) {
            return;
        }
        this.googleMap = googleMap;
        updateMapLocation(product, googleMap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == AddProductActivity.RESULT_CODE_SUCCESS_ADD_PRODUCT_ACTIVITY) {
                Product updatedProduct = (Product) data.getSerializableExtra("updated_product_data");
                if (updatedProduct != null) {
                    product = updatedProduct;
                    assignViews();
                    updateMapLocation(product, googleMap);
                }
            }
        } else if (requestCode == 5001) {
            //Result for Contacts App Activity
            if (data != null) {
                fetchContactNumberFromData(data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            boolean areAllPermissionGranted = false;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    areAllPermissionGranted = true;
                } else {
                    areAllPermissionGranted = false;
                    break;
                }
            }
            if (areAllPermissionGranted) {
                showSendSmsAlertDialog();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "You need to allow SMS permission to send SMS",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    private void initViews() {
        ibBack = findViewById(R.id.ib_back);
        ivDetailImage = findViewById(R.id.iv_product_image);
        tvProductTitle = findViewById(R.id.tv_product_title);
        tvProductDescription = findViewById(R.id.tv_product_description);
        tvProductPrice = findViewById(R.id.tv_product_price);
        cbMarkAsPurchased = findViewById(R.id.cb_mark_as_purchased);
        ibEdit = findViewById(R.id.ib_edit);
        ibDelete = findViewById(R.id.ib_delete);
        ibSendSms = findViewById(R.id.ib_share);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cbMarkAsPurchased.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                handleCheckChangedForMarkAsPurchased(isChecked);
            }
        });

        ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddProductActivityToUpdate();
            }
        });

        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertUserBeforeDeleting();
            }
        });

        ibSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForSmsPermissions();
            }
        });
    }

    private void handleCheckChangedForMarkAsPurchased(boolean isChecked) {
        if (isChecked) {
            updateProductWithMarkAsPurchasedTrue();
        } else {
            updateProductWithMarkAsPurchasedFalse();
        }
    }

    private void updateProductWithMarkAsPurchasedTrue() {
        product.markAsPurchased = true;
        updateProductDataInDb(product);
    }

    private void updateProductWithMarkAsPurchasedFalse() {
        product.markAsPurchased = false;
        updateProductDataInDb(product);
    }

    private void updateProductDataInDb(Product product) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BabyBuyDatabase babyBuyDatabase = BabyBuyDatabase
                            .getInstance(getApplicationContext());
                    ProductDao productDao = babyBuyDatabase.getProductDao();
                    productDao.updateProduct(product);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }

    private void startAddProductActivityToUpdate() {
        Intent intent = new Intent(DetailPageActivity.this, AddProductActivity.class);
        intent.putExtra("product_data", product);
        startActivityForResult(intent, 1001);
    }

    private void assignViews() {
        ivDetailImage.post(new Runnable() {
            @Override
            public void run() {
                ivDetailImage.setImageBitmap(getBitmapForImageView(ivDetailImage, product.image));
            }
        });
        tvProductTitle.setText(product.title);
        tvProductPrice.setText("£ " + product.price);
        tvProductDescription.setText(product.description);
        cbMarkAsPurchased.setChecked(product.markAsPurchased);
    }

    private Bitmap getBitmapForImageView(View view, String imageUriPath) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    getContentResolver(),
                    Uri.parse(imageUriPath)
            );
            bitmap = BitmapScalar.stretchToFill(
                    bitmap,
                    view.getWidth(),
                    view.getHeight()
            );
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_gallery);
        }
        return bitmap;
    }

    private void updateMapLocation(Product product, GoogleMap googleMap) {
        LatLng latLng = new LatLng(
                Double.parseDouble(product.latitude),
                Double.parseDouble(product.longitude)
        );
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Product Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void alertUserBeforeDeleting() {
        AlertDialog alertDialog = new AlertDialog.Builder(DetailPageActivity.this)
                .setTitle("Delete Product")
                .setMessage("Are you sure want to delete this product data?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        deleteProductFromDataBase();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void deleteProductFromDataBase() {
        new Thread(() -> {
            try {
                BabyBuyDatabase babyBuyDatabase = BabyBuyDatabase
                        .getInstance(getApplicationContext());
                ProductDao productDao = babyBuyDatabase.getProductDao();
                productDao.deleteProduct(product);
                runOnUiThread(this::finishActivity);
            } catch (Exception exception) {
                exception.printStackTrace();
                runOnUiThread(() -> Toast.makeText(
                        DetailPageActivity.this,
                        "Error while deleting product...",
                        Toast.LENGTH_SHORT
                ).show());
            }
        }).start();
    }

    private void checkForSmsPermissions() {
        if (areSmsPermissionsGranted()) {
            showSendSmsAlertDialog();
        } else {
            requestPermissions(
                    smsPermissionsList().toArray(new String[0]),
                    111
            );
        }
    }

    private boolean areSmsPermissionsGranted() {
        boolean areAllPermissionGranted = false;
        for (String permission : smsPermissionsList()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED) {
                areAllPermissionGranted = true;
            } else {
                areAllPermissionGranted = false;
                break;
            }
        }
        return areAllPermissionGranted;
    }

    private List<String> smsPermissionsList() {
        List<String> smsPermissions = new ArrayList<>();
        smsPermissions.add(Manifest.permission.READ_CONTACTS);
        smsPermissions.add(Manifest.permission.SEND_SMS);
        return smsPermissions;
    }

    private void showSendSmsAlertDialog() {
        sendSmsAlertDialog = new AlertDialog.Builder(DetailPageActivity.this).create();
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View alertDialogView = layoutInflater.inflate(R.layout.layout_send_sms, null);
        sendSmsAlertDialog.setView(alertDialogView);
        sendSmsAlertDialog.setTitle("Send SMS");
        sendSmsAlertDialog.setCancelable(true);

        tietSendSmsMobileNumber = alertDialogView.findViewById(R.id.tiet_enter_mobile);
        ImageButton ibSelectContact = alertDialogView.findViewById(R.id.ib_select_contact);
        MaterialButton mbSendSmsCancel = alertDialogView.findViewById(R.id.mb_cancel);
        MaterialButton mbSendSms = alertDialogView.findViewById(R.id.mb_send_sms);

        ibSelectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startContactsAppActivityToPickContact();
            }
        });

        mbSendSmsCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSmsAlertDialog.dismiss();
            }
        });

        mbSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateMobileNumber();
            }
        });


        sendSmsAlertDialog.show();
    }

    private void startContactsAppActivityToPickContact() {
        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContact, 5001);
    }

    private void fetchContactNumberFromData(Intent data) {
        Uri contactUri = data.getData();

        // Specify which fields you want
        // your query to return values for
        String[] queryFields = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        // Perform your query - the contactUri
        // is like a "where" clause here
        try (Cursor cursor = this.getContentResolver()
                .query(contactUri, null, null, null, null)) {
            // Double-check that you
            // actually got results
            if (cursor.getCount() == 0) return;

            // Pull out the first column of
            // the first row of data
            // that is your contact's name
            cursor.moveToFirst();

            int contactNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String contactNumber = cursor.getString(contactNumberIndex);
            tietSendSmsMobileNumber.setText(contactNumber);

        }
    }

    private void validateMobileNumber() {
        String mobileNumber = tietSendSmsMobileNumber.getText().toString().trim();
        if (mobileNumber.isEmpty()) {
            tietSendSmsMobileNumber.setError("Please enter a mobile number");
            return;
        }
        String message = prepareSms();
        sendSmsAlertDialog.dismiss();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending SMS to " + mobileNumber);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendSmsToMobileNumber(mobileNumber, message);
                progressDialog.dismiss();
            }
        }, 1500);
    }

    private String prepareSms() {
        String message = "Item: " + product.title + "\n"
                + "Price: " + product.price + "\n"
                + "Description: " + product.description;

        if (message.length() > 100) {
            message = message.substring(0, 100);
        }
        return message;
    }

    private void sendSmsToMobileNumber(String mobileNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(
                    mobileNumber,
                    null,
                    message,
                    null,
                    null
            );
            Toast.makeText(
                    getApplicationContext(),
                    "SMS sent successfully",
                    Toast.LENGTH_LONG
            ).show();
        } catch (Exception exception) {
            exception.printStackTrace();
            Toast.makeText(
                    getApplicationContext(),
                    "Failed to send SMS. Please try again...",
                    Toast.LENGTH_LONG
            ).show();
        }
//        Intent intent = new Intent(Intent.ACTION_SENDTO);
//        intent.setData(Uri.parse("smsto:" + mobileNumber));
//        intent.putExtra("sms_body", message);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, 5002);
//        }
    }



    private void finishActivity() {
        setResult(RESULT_OK);
        finish();
    }
}