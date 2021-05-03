package cs.uga.edu.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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

    TextView totalCost;
    TextView avgCost;
    TextView totalSpent;
    TextView totalPaid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        itemList = getIntent().getParcelableArrayListExtra("items");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        myRef.addListenerForSingleValueEvent( new ValueEventListener() {

            @Override
            public void onDataChange( DataSnapshot snapshot ) {
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    User user = postSnapshot.getValue(User.class);
                    userList.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        } );

        totalCost = findViewById(R.id.totalCost);
        avgCost = findViewById(R.id.avgCost);
        totalSpent = findViewById(R.id.totalSpent);
        totalPaid = findViewById(R.id.totalPaid);

        double total = calculateTotal(itemList);
        totalCost.setText("$" + String.format("%.2f", total));
        avgCost.setText("$" + String.format("%.2f", calculateAvg(total,userList.size())));

        String eachMemberPayment = "";

        for(int i=0; i< userList.size();i++){
            String name = userList.get(i).getUsername();
            double memberTotal = calculateEachTotal(name);
            userList.get(i).setPaid(memberTotal);
            eachMemberPayment += name + " spent a total of $" + String.format("%.2f", memberTotal) + "/n";
        }
        totalSpent.setText(eachMemberPayment);

        //totalPaid.setText()

    }

    private double calculateTotal(List<Item> itemPricesList){
        double total = 0;
        for(int i=0; i< itemPricesList.size(); i++){
            total += itemPricesList.get(i).getPrice();
        }

        return total;
    }

    private double calculateAvg(double total, int size){
        return total/size;
    }

    private double calculateEachTotal(String user){
        double total = 0;
        for(int i=0; i< itemList.size();i++){
            if(itemList.get(i).getUser().equals(user))
                total += itemList.get(i).getPrice();
        }
        return total;
    }
}