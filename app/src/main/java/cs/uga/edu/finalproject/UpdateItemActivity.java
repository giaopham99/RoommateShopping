package cs.uga.edu.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UpdateItemActivity extends AppCompatActivity {
    public static final String ITEM_OBJ = "";
    private Button updateButton;
    private Button cancelButton;
    private EditText titleEditText;
    private EditText descEditText;
    private EditText priceEditText;
    private CheckBox purchasedButton;
    private boolean previouslyChecked;
    private AlertDialog.Builder builder;
    private EditText editableText2;
    private boolean checkedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);
        Intent intent = getIntent();
        Item item = intent.getParcelableExtra("Item");
        builder = new AlertDialog.Builder(UpdateItemActivity.this);

        editableText2 = new EditText(UpdateItemActivity.this);
        editableText2.setHint("Example: 2.99");
        titleEditText = findViewById(R.id.itemTitleEditText);
        descEditText = findViewById(R.id.itemDescEditText);
        priceEditText = findViewById(R.id.itemPriceEditText);
        purchasedButton = findViewById(R.id.purchasedCheckBox2);

        /**Fills the text boxes with initial content**/
        titleEditText.setText(item.getTitle());
        descEditText.setText(item.getDesc());
        double d = item.getPrice();
        previouslyChecked = item.isPurchased();
        if(previouslyChecked == true) {
            priceEditText.setText("$" + String.format("%.2f", d));
        }//only show price if it's been checked
        else
        {
            priceEditText.setHint("Not entered");
        }

        purchasedButton.setChecked(item.isPurchased());

        /**Attaches the button listeners**/
        updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new UpdateButtonListener());
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new CancelButtonListener());
        purchasedButton.setOnClickListener(new PurchasedButtonClickListener());
    }

    /**
     * Update Button Listener
     **/
    private class UpdateButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            /**Gets current values item**/
            String title = titleEditText.getText().toString();
            String desc = descEditText.getText().toString();
            double price = 0;
            if(priceEditText.getText().toString().length() != 0) {
                price = Double.parseDouble(priceEditText.getText().toString().substring(1));
            }
            //If it's empty, set price to 0. Else add a value.

            final Item item = new Item(title, desc, price, checkedButton);

            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //Update all the stuff in the thing, send to different places depending on if it's checked or not
            if (checkedButton == true) {
                builder.setTitle("Purchased Item Selected");
                builder.setView(editableText2);
                LinearLayout layoutName = new LinearLayout(UpdateItemActivity.this);
                layoutName.setOrientation(LinearLayout.VERTICAL);
                layoutName.addView(editableText2); // displays the user input bar
                builder.setView(layoutName);
                builder.setMessage("Please input price for the purchased item:")
                        .setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DatabaseReference myRef = database.getReference("recentlyPurchasedList");
                                String userInput = editableText2.getText().toString();
                                try {
                                    Double.parseDouble(userInput); //make sure it's a double

                                } catch (NumberFormatException ignore) {
                                    Toast.makeText(getApplicationContext(), "Invalid input!",
                                            Toast.LENGTH_SHORT).show();
                                }
                                builder.show();

                                //enter the stuff into the database
                                myRef.orderByChild("title").equalTo(item.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        DatabaseReference myRef = database.getReference("recentlyPurchasedList");
                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                                            String id = data.getKey();
                                            myRef.child(id).child("title").setValue(title);
                                            myRef.child(id).child("desc").setValue(desc);
                                            myRef.child(id).child("price").setValue(Double.parseDouble(userInput));
                                            myRef.child(id).child("isPurchased").setValue(true);
                                            myRef.child(id).child("user").setValue(item.getUser());
                                        }
                                        //after success return to menu
                                        Toast.makeText(getApplicationContext(),item.getTitle() + " has successfully been updated in RecentlyPurchasedList!",
                                                Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(UpdateItemActivity.this, ShoppingListActivity.class);
                                        startActivity(intent);
                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                                //if the button has been checked, then remove it from the shoppingList at the end
                                if (previouslyChecked == false) {
                                    myRef = database.getReference("shoppingList");
                                    Query deleteQuery = myRef.orderByChild("title").equalTo(item.getTitle());
                                    deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot sh) {
                                            for (DataSnapshot deleteQuery : sh.getChildren()) {
                                                deleteQuery.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("Recycler", "onCancelled", error.toException());

                                        }
                                    });
                                }
                            }
                        });
            }
            else {
                DatabaseReference myRef = database.getReference("shoppingList");
                myRef.orderByChild("title").equalTo(item.getTitle()).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DatabaseReference myRef = database.getReference("shoppingList");
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String id = data.getKey();
                            myRef.child(id).child("title").setValue(title);
                            myRef.child(id).child("desc").setValue(desc);
                            myRef.child(id).child("price").setValue(0); //reset the price because it should be 0 effectively
                            myRef.child(id).child("isPurchased").setValue(false); //set to false always because it's not checked
                        }
                        //after success return to menu
                        Toast.makeText(getApplicationContext(),item.getTitle() + " has successfully been updated in ShoppingList!",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UpdateItemActivity.this, ShoppingListActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //if the button has been checked, then remove it from the recentlyPurchased List at the end
                if (previouslyChecked == false) {
                    myRef = database.getReference("recentlyPurchasedList");
                    Query deleteQuery = myRef.orderByChild("title").equalTo(item.getTitle());
                    deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot sh) {
                            for (DataSnapshot deleteQuery : sh.getChildren()) {
                                deleteQuery.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Recycler", "onCancelled", error.toException());
                        }
                    });
                }
            }
        }
    }



    /**
     * Cancel Button Listener
     **/
    private class CancelButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(UpdateItemActivity.this, ShoppingListActivity.class);
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
