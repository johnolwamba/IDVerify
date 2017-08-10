package brenda.idverify.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import brenda.idverify.R;

/**
 * Created by john on 30/07/2017.
 */

public class BlockedUsersRecyclerAdapter extends  RecyclerView.Adapter<BlockedUsersRecyclerHolder> {

    private String[] name;
    private String[] id_number;
    private String[] date;

    Context context;
    LayoutInflater inflater;
    public BlockedUsersRecyclerAdapter(Context context, String[] id_number, String[] name,
                                        String[] date) {
        this.context=context;
        this.id_number = id_number;
        this.date = date;
        this.name = name;

        inflater= LayoutInflater.from(context);
    }
    @Override
    public BlockedUsersRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=inflater.inflate(R.layout.blocked_list_item, parent, false);

        BlockedUsersRecyclerHolder viewHolder=new BlockedUsersRecyclerHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BlockedUsersRecyclerHolder holder, int position) {

        holder.txtName.setText(name[position]);
        holder.txtID.setText(id_number[position]);
        holder.txtDate.setText(date[position]);
    }

    @Override
    public int getItemCount() {
        return name.length;
    }



}
