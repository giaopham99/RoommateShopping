package cs.uga.edu.finalproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ShoppingItemRecyclerAdapter extends RecyclerView.Adapter<ShoppingItemRecyclerAdapter.ItemHolder>{
    private List<Item> itemList;
    private Context context;
    private Item item;
    private boolean isShoppingList;
    public ShoppingItemRecyclerAdapter(List<Item> itemList, boolean isShoppingList ){
        this.itemList = itemList;
        this.isShoppingList = isShoppingList;
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView desc;
        TextView price;
        Button update;
        Button delete;
        CheckBox purchased;
        View view;
        int position;

        public ItemHolder(View itemView){
            super(itemView);
            context = itemView.getContext();
            name = itemView.findViewById(R.id.itemName);
            desc = itemView.findViewById(R.id.itemDetails);
            price = itemView.findViewById(R.id.itemPrice);

            purchased = itemView.findViewById(R.id.checkBox);
            purchased.setEnabled(false); //Doesn't let users change it here, only in update activity

            update = itemView.findViewById(R.id.updateItemButton);
            delete = itemView.findViewById(R.id.deleteItemButton);
        }
    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item, parent, false);
            return new ItemHolder(view);
    }

        @Override
        public void onBindViewHolder (ItemHolder holder,int position ){
            Item item = itemList.get(position);
            holder.name.setText(item.getTitle());
            holder.desc.setText(item.getDesc());
            holder.purchased.setChecked(item.isPurchased());
            double d = item.getPrice();
            if(item.isPurchased() == true) {
                holder.price.setText("$" + String.format("%.2f", d));
            }
            else
            {
                holder.price.setVisibility(View.INVISIBLE);
            }
            //If the price isn't set, then hide it

            holder.update.setOnClickListener(view -> {
                Intent intent = new Intent(context, UpdateItemActivity.class);
                intent.putExtra("Item", item);
                context.startActivity(intent);
            });

            holder.delete.setOnClickListener(view ->
            {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("shoppingList");

                //if it isn't for shopping list, then it's for recently purchased. switch tables.
                if(isShoppingList == false) {
                    myRef = database.getReference("recentlyPurchasedList");
                }

                Query deleteQuery = myRef.orderByChild("title").equalTo(item.getTitle());

                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot sh) {
                        for (DataSnapshot deleteQuery: sh.getChildren()) {
                            deleteQuery.getRef().removeValue();
                            Toast.makeText(context, "Deleted " + item.getTitle(), Toast.LENGTH_SHORT).show();
                            itemList.remove(item);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Recycler", "onCancelled", error.toException());

                    }
                });
            });

        }

        @Override
        public int getItemCount () {
            return itemList.size();
        }


}