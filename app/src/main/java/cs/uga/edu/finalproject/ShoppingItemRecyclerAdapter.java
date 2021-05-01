package cs.uga.edu.finalproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ShoppingItemRecyclerAdapter extends RecyclerView.Adapter<ShoppingItemRecyclerAdapter.ItemHolder>{
    private List<Item> itemList;
    private Context context;

    public ShoppingItemRecyclerAdapter(List<Item> itemList){ this.itemList = itemList; }

    class ItemHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView desc;
        TextView price;
        Button update;
        Button delete;

        public ItemHolder(View itemView){
            super(itemView);
            context = itemView.getContext();

            name = itemView.findViewById(R.id.itemName);
            desc = itemView.findViewById(R.id.itemDetails);
            price = itemView.findViewById(R.id.itemPrice);

            update = itemView.findViewById(R.id.updateItemButton);
            delete = itemView.findViewById(R.id.deleteItemButton);

            update.setOnClickListener(new UpdateButtonClickListener());
            delete.setOnClickListener(new DeleteButtonClickListener());
        }
    }

    private class UpdateButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, UpdateItemActivity.class);
            context.startActivity(intent);
        }
    }

    private class DeleteButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.shopping_item, parent, false );
        return new ItemHolder( view );
    }

    @Override
    public void onBindViewHolder( ItemHolder holder, int position ) {
        Item item = itemList.get( position );

        holder.name.setText( item.getTitle() );
        holder.desc.setText( item.getDesc() );
        double d = item.getPrice();
        holder.price.setText( "$" + String.format("%.2f",d));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
