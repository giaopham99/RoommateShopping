package cs.uga.edu.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemActivity<TextEdit> extends AppCompatActivity {

    private EditText titleEditText;
    private EditText descEditText;
    private EditText priceEditText;

    private Button addButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        titleEditText = findViewById(R.id.itemTitleEditText);
        descEditText = findViewById(R.id.itemDescEditText);
        priceEditText = findViewById(R.id.itemPriceEditText);

        addButton = findViewById(R.id.addButton);
        cancelButton = findViewById(R.id.cancelButton);

        addButton.setOnClickListener( new SaveButtonClickListener());
        cancelButton.setOnClickListener( new CancelButtonClickListener());
    }

    private class SaveButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String title = titleEditText.getText().toString();
            String desc = descEditText.getText().toString();
            double price = Double.parseDouble(priceEditText.getText().toString());

            final Item item = new Item(title, desc, price);

            // Add new item to list of items in Firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("shoppingList");

            myRef.push().setValue(item)
                    .addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                        Toast.makeText(getApplicationContext(), "A new item has been added for " + item.getTitle(),
                                Toast.LENGTH_SHORT).show();

                        titleEditText.setText("");
                        descEditText.setText("");
                        priceEditText.setText("");

                        Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                        startActivity(intent);

                    })
                    .addOnFailureListener((e) -> {
                        Toast.makeText(getApplicationContext(), "Failed to add the item" + item.getTitle(),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private class CancelButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}