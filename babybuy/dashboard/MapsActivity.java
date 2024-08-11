package np.com.yourname.babybuy.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import np.com.yourname.babybuy.R;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerDragListener {

    public static final int MAPS_ACTIVITY_SUCCESS_RESULT_CODE = 3014;
    public static final int MAPS_ACTIVITY_FAILURE_RESULT_CODE = 3015;
    public static final String EXTRA_PRODUCT_LOCATION = "maps_product_location";
    public static final String EXTRA_MAPS_MESSAGE = "maps_exception_message";

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ProductLocation productLocation;
    private Marker marker;
    private ImageButton imageButtonBack;
    private MaterialButton mbSaveLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        imageButtonBack = findViewById(R.id.ib_back);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPageExitAlertDialog();
            }
        });

        mbSaveLocation = findViewById(R.id.mb_save_location);
        mbSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProductLocation();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMyLocationClickListener(this);
        this.googleMap.setOnMyLocationButtonClickListener(this);
        this.googleMap.setOnMarkerDragListener(this);
        enableMyLocation();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        if (marker != null) {
            marker.remove();
        }
        locateMarkerToCurrentLocation(location);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        this.marker = marker;
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        this.marker = marker;
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
        this.marker = marker;
    }

    @Override
    public void onBackPressed() {
        showPageExitAlertDialog();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (allPermissionForLocationGranted()) {
                googleMap.setMyLocationEnabled(true);
            } else {
                setFailureResultBackToCallingComponent("Please grant the location permissions...");
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (allPermissionForLocationGranted()) {
            fusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.QUALITY_HIGH_ACCURACY,
                    new CancellationToken() {
                        @NonNull
                        @Override
                        public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                            return null;
                        }

                        @Override
                        public boolean isCancellationRequested() {
                            return false;
                        }
                    }
            ).addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    locateMarkerToCurrentLocation(location);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    locateMarkerToDefaultLocation();
                }
            });
            googleMap.setMyLocationEnabled(true);
            return;
        }

        requestPermissions(
                getPermissionsRequiredForLocation().toArray(new String[0]),
                101
        );
    }

    private boolean allPermissionForLocationGranted() {
        boolean granted = false;
        for (String permission :
                getPermissionsRequiredForLocation()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED) {
                granted = true;
            }
        }
        return granted;
    }

    private List<String> getPermissionsRequiredForLocation() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        return permissions;
    }

    private void locateMarkerToCurrentLocation(Location currentLocation) {
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        marker = googleMap.addMarker(new MarkerOptions()
                .position(currentLatLng)
                .draggable(true)
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
    }

    private void locateMarkerToDefaultLocation() {
        LatLng kathmandu = new LatLng(27.7172, 85.3240);
        marker = googleMap.addMarker(new MarkerOptions()
                .position(kathmandu)
                .title("Marker in Kathmandu")
                .draggable(true)
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kathmandu, 15));
    }

    private void updateProductLocation() {
        if (marker == null) {
            Toast.makeText(
                    MapsActivity.this,
                    "Please mark the product location...",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        productLocation = new ProductLocation();
        productLocation.latitude = String.valueOf(marker.getPosition().latitude);
        productLocation.longitude = String.valueOf(marker.getPosition().longitude);
        setSuccessResultBackToCallingComponent(productLocation);
    }

    private void showPageExitAlertDialog() {
        AlertDialog exitAlertDialog = new AlertDialog.Builder(MapsActivity.this)
                .setTitle("Exit")
                .setMessage("Are you sure want to exit? All your progress will be lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        productLocation = new ProductLocation();
                        productLocation.latitude = String.valueOf("27.7172");
                        productLocation.longitude = String.valueOf("85.3240");
                        setSuccessResultBackToCallingComponent(productLocation);
//                        setFailureResultBackToCallingComponent("Setting Location Cancelled...");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        exitAlertDialog.show();
    }

    private void setSuccessResultBackToCallingComponent(ProductLocation productLocation) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PRODUCT_LOCATION, productLocation);
        setResult(MAPS_ACTIVITY_SUCCESS_RESULT_CODE, intent);
        finish();
    }

    private void setFailureResultBackToCallingComponent(String exceptionMessage) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_MAPS_MESSAGE, exceptionMessage);
        setResult(MAPS_ACTIVITY_FAILURE_RESULT_CODE, intent);
        finish();
    }
}