package np.com.yourname.babybuy.utility;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import np.com.yourname.babybuy.R;


public class sort_by extends AppCompatActivity {

    private ListView listView;
    private Button btnSortAsc, btnSortDesc;
    private List<String> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sort_by);
        listView = findViewById(R.id.listView);
        btnSortAsc = findViewById(R.id.btnSortAsc);
        btnSortDesc = findViewById(R.id.btnSortDesc);

        itemList = new ArrayList<>();
        // Add sample money values to the list
        itemList.add("$100");
        itemList.add("$50");
        itemList.add("$200");
        itemList.add("$75");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

        btnSortAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sort the list in ascending order
                Collections.sort(itemList);
                adapter.notifyDataSetChanged();
            }
        });

        btnSortDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sort the list in descending order
                Collections.sort(itemList, Collections.reverseOrder());
                adapter.notifyDataSetChanged();
            }
        });
    }
}

