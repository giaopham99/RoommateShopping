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
    private List<String> userList = new ArrayList<String>();
    private List<Item> itemList;

    TextView totalCost;
    TextView avgCost;
    TextView totalSpent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        itemList = getIntent().getParcelableArrayListExtra("items");

        for(int i=0; i<itemList.size();i++)
        {
            if(!userList.contains(itemList.get(i).getUser()))
                userList.add(itemList.get(i).getUser());
        }

        totalCost = findViewById(R.id.totalCost);
        avgCost = findViewById(R.id.avgCost);
        totalSpent = findViewById(R.id.totalSpent);

        double total = calculateTotal(itemList);
        totalCost.setText("$" + String.format("%.2f", total));
        avgCost.setText("$" + String.format("%.2f", calculateAvg(total,userList.size())));

        String eachMemberPayment = "";


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