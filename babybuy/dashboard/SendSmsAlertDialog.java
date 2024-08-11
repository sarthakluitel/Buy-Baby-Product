package np.com.yourname.babybuy.dashboard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import np.com.yourname.babybuy.db.product.Product;

/**
 * Created  on 03/01/2023.
 */
public class SendSmsAlertDialog extends AlertDialog {
    private TextInputEditText textInputEditTextMobileNumber;
    private ImageButton imageButtonPickContact;
    private Product product;
    private View recentClickedView;

    public SendSmsAlertDialog(@NonNull Context context, Product product) {
        super(context);
        this.product = product;
    }

    private void checkSmsPermissionsToProceed(Context context) {
        if (areSmsPermissionsGranted(context)) {
            showContactPicker(context);
        }

        ((Activity) context).requestPermissions(
                smsPermissionsList().toArray(new String[0]),
                111
        );
    }

    private boolean areSmsPermissionsGranted(Context context) {
        boolean areAllPermissionGranted = false;
        for (String permission : smsPermissionsList()) {
            if (ContextCompat.checkSelfPermission(context, permission)
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
        smsPermissions.add(Manifest.permission.READ_SMS);
        smsPermissions.add(Manifest.permission.SEND_SMS);
        return smsPermissions;
    }

    private void showContactPicker(Context context) {
        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        ((Activity) context).startActivityForResult(pickContact, 5001);
    }

    private void validatePhoneNumberToProceed(Context context) {
        String mobileNum = textInputEditTextMobileNumber.getText().toString().trim();
        if (mobileNum.isEmpty()) {
            Toast.makeText(context, "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (areSmsPermissionsGranted(context)) {
            SmsManager smsManager = SmsManager.getDefault();
            String message = "Item: " + product.title + "\n"
                    + "Price: " + product.price + "\n"
                    + "Description: " + product.description.substring(50);
            smsManager.sendTextMessage(
                    mobileNum,
                    null,
                    message,
                    null,
                    null
            );
        } else {
            ((Activity) context).requestPermissions(
                    smsPermissionsList().toArray(new String[0]),
                    111
            );
        }
    }

    public void onSmsPermissionsGranted() {

    }
}
