package cs.uga.edu.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalculateActivity extends AppCompatActivity {

    private List<User> userList = new ArrayList<User>();
    private List<Item> itemList = new ArrayList<Item>();

    private static View myView;
    private TextView totalCost;
    private TextView avgCost;
    private TextView totalSpent;
    private TextView userTotalPaid;
    private TextView amountToReceive;
    private TextView amountOwed;
    private Button clearButton;

    private DatabaseReference ref;
    private String eachMemberPayment = "";
    private String eachMemberIsOwed = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        itemList = getIntent().getParcelableArrayListExtra("items"); // getting itemList

        totalCost = findViewById(R.id.totalCost);
        avgCost = findViewById(R.id.avgCost);
        totalSpent = findViewById(R.id.totalSpent);
        userTotalPaid = findViewById(R.id.totalPaid);
        amountToReceive = findViewById(R.id.amountReceived);
        amountOwed = findViewById(R.id.amountOwed);
        clearButton = findViewById(R.id.settleButton);

        clearButton.setOnClickListener(new DeleteButtonClickListener());

        // Set Total Cost
        double total = calculateTotal(itemList);
        totalCost.setText("$" + String.format("%.2f", total));

        // Firebase setup
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        ref = database.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Getting list of users
        myRef.addListenerForSingleValueEvent( new ValueEventListener() {

            @Override
            public void onDataChange( DataSnapshot snapshot ) {
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    User userItem = postSnapshot.getValue(User.class);
                    userList.add(userItem);
                }
                // Set Average Cost
                avgCost.setText("$" + String.format("%.2f", calculateAvg(total,userList.size())));

                // Find the index of the current user
                int index = 0;
                for(int i=0; i< userList.size();i++){
                    if(userList.get(i).getUsername().equals(user.getDisplayName()))
                        index = i;
                }

                // Figure out how much each roommate spent on the purchases
                for(int i=0; i< userList.size();i++){
                    String name = userList.get(i).getUsername();
                    double memberTotal = calculateEachTotal(name);
                    userList.get(i).setPaid(memberTotal); //Set how much this roommate paid for when buying the items
                    if(i != index)
                        eachMemberPayment += name + " spent a total of $" + String.format("%.2f", memberTotal) + ".\n";
                }
                totalSpent.setText(eachMemberPayment);


                // How much the user spent on the purchases
                double userAmountSpent = userList.get(index).getPaid();
                userTotalPaid.setText("$" + String.format("%.2f", userAmountSpent));

                int size = userList.size();
                computeHowMuchIOwe(size, index);

                double amountReceived = userAmountSpent*(size-1)/size;
                amountToReceive.setText("$" + String.format("%.2f", amountReceived));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        } );
    }

    /**
     * Sets every user's (except current user's) amountReturned.
     * This will give the amounts that the current user owes every other roommate.
     */
    private void computeHowMuchIOwe(int users, int currentUser){
        for(int i=0; i<users;i++){
            String name = userList.get(i).getUsername();
            double amount = userList.get(i).getPaid()/users;

            if(i != currentUser) { // because you can't owe yourself money
                userList.get(i).setAmountReturned(amount);
                eachMemberIsOwed += name + " ----------------  $" + String.format("%.2f", amount) + ".\n";
            }
        }
        amountOwed.setText(eachMemberIsOwed);
    }

    /**
     * Calculates the total cost
     */
    private double calculateTotal(List<Item> itemPricesList){
        double total = 0;
        for(int i=0; i< itemPricesList.size(); i++){
            total += itemPricesList.get(i).getPrice();
        }

        return total;
    }

    /**
     * Calculates the average cost
     */
    private double calculateAvg(double total, int size){
        return total/size;
    }

    /**
     * Calculates the total that each roommate spent on purchases
     */
    private double calculateEachTotal(String user){
        double total = 0;
        for(int i=0; i< itemList.size();i++){
            if(itemList.get(i).getUser().equals(user))
                total += itemList.get(i).getPrice();
        }
        return total;
    }

    /**
     * Clears the recentlyPurchasedList database reference
     */
    private class DeleteButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            DialogFragment newFragment = AlertDialogFragment.newInstance();
            showDialogFragment(newFragment);
            myView = v;
            ref.child("recentlyPurchasedList").orderByChild("price")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            for (DataSnapshot data:dataSnapshot.getChildren())
                            {
                                data.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError)
                        {
                            Toast.makeText(getApplicationContext(), "Failed to clear Recently Purchased List", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /** Dialog to notify users that they have settled the cost **/
    public static class AlertDialogFragment extends DialogFragment {
        public static AlertDialogFragment newInstance() {
            return new AlertDialogFragment();
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Congratulations!");
            alertDialog.setMessage("You have just settled the cost. Your roommates will be notified about their transactions.");
            alertDialog.setIcon(android.R.drawable.stat_sys_download_done);
            alertDialog.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(myView.getContext(), MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(myView.getContext(), "Recently Purchased Cleared", Toast.LENGTH_SHORT).show();
                        }
                    });
            return alertDialog.create();
        }
    }

    void showDialogFragment(DialogFragment newFragment) {
        newFragment.show(getSupportFragmentManager(), null);
    }
}