package cs.uga.edu.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemActivity<TextEdit> extends AppCompatActivity {
    private EditText titleEditText;
    private EditText descEditText;
    //private EditText priceEditText;
    private Button addButton;
    private Button cancelButton;
    private CheckBox purchasedButton;
    private boolean checkedButton;
    private AlertDialog.Builder builder;
    private EditText editableText;
    private Item item_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        builder = new AlertDialog.Builder(this);
        editableText = new EditText(AddItemActivity.this);
        editableText.setHint("Example: 2.99");

        titleEditText = findViewById(R.id.itemTitleEditText);
        descEditText = findViewById(R.id.itemDescEditText);
        //priceEditText = findViewById(R.id.itemPriceEditText);

        addButton = findViewById(R.id.addButton);
        cancelButton = findViewById(R.id.cancelButton);
        purchasedButton = findViewById(R.id.purchasedCheckBox);

        addButton.setOnClickListener( new SaveButtonClickListener());
        cancelButton.setOnClickListener( new CancelButtonClickListener());
        purchasedButton.setOnClickListener(new PurchasedButtonClickListener());
    }

    private class SaveButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String title = titleEditText.getText().toString();
            String desc = descEditText.getText().toString();
            //double price = Double.parseDouble(priceEditText.getText().toString()); No longer need this line, not showing price input.
            final Item item = new Item(title, desc, checkedButton);
            item_ = item;
            // Add new item to list of items in Firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //If the item is purchased, then require the price
            if (item.isPurchased()) {
                builder.setTitle("Purchased Item Selected");
                builder.setView(editableText);
                LinearLayout layoutName = new LinearLayout(AddItemActivity.this);
                layoutName.setOrientation(LinearLayout.VERTICAL);
                layoutName.addView(editableText); // displays the user input bar
                builder.setView(layoutName);
                builder.setMessage("Please input price for the purchased item:")
                        .setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DatabaseReference myRef = database.getReference("recentlyPurchasedList");
                                String userInput = editableText.getText().toString();
                                try {
                                    Double.parseDouble(userInput); //make sure it's a double

                                } catch (NumberFormatException ignore) {
                                    Toast.makeText(getApplicationContext(),"Invalid input!",
                                            Toast.LENGTH_SHORT).show();
                                }

                                //enter the stuff into the database
                                String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                item.setUser(user);
                                item.setPrice(Double.parseDouble(userInput));
                                myRef.push().setValue(item)
                                        .addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                                            titleEditText.setText("");
                                            descEditText.setText("");
                                            //priceEditText.setText("");

                                            Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                                            startActivity(intent);

                                        })
                                        .addOnFailureListener((e) -> {
                                            Toast.makeText(getApplicationContext(), "Failed to add the item" + item.getTitle(),
                                                    Toast.LENGTH_SHORT).show();
                                        });
                                finish();
                                Toast.makeText(getApplicationContext(),"Price has successfully been entered for the item!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.show();
            }
            else {
                //If the item is not purchased, then add it to shoppingList
                DatabaseReference myRef = database.getReference("shoppingList");

                myRef.push().setValue(item)
                        .addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                            titleEditText.setText("");
                            descEditText.setText("");
                            //priceEditText.setText("");

                            Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                            startActivity(intent);

                        })
                        .addOnFailureListener((e) -> {
                            Toast.makeText(getApplicationContext(), "Failed to add the item" + item.getTitle(),
                                    Toast.LENGTH_SHORT).show();
                        });

            }
        }
    }

    /** Cancels the attempt to add new item**/
    private class CancelButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    /**Checks to see if button is checked or not**/
    private class PurchasedButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            boolean checked = ((CheckBox) v).isChecked();
            if(checked)
            {
               checkedButton = true;
            }
            else
            {
                checkedButton = false;
            }
        }
    }
}