package cs.uga.edu.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;

    private List<Item> itemList;

    private Button addItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new AddItemButtonClickListener());

        addItem = findViewById(R.id.returnMainButton);
        addItem.setOnClickListener(new ReturnButtonClickListener());

        recyclerView = findViewById( R.id.recyclerView );

        layoutManager = new LinearLayoutManager(this );
        recyclerView.setLayoutManager( layoutManager );

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("shoppingList");

        itemList = new ArrayList<Item>();

        myRef.addListenerForSingleValueEvent( new ValueEventListener() {

            @Override
            public void onDataChange( DataSnapshot snapshot ) {
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    Item item = postSnapshot.getValue(Item.class);
                    itemList.add(item);
                }

                recyclerAdapter = new ShoppingItemRecyclerAdapter( itemList );
                recyclerView.setAdapter( recyclerAdapter );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        } );

    }

    private class AddItemButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            Intent intent = new Intent(v.getContext(), AddItemActivity.class);
            startActivity(intent);
        }
    }

    private class ReturnButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}