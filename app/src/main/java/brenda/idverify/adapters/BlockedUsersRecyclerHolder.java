package brenda.idverify.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import brenda.idverify.R;

/**
 * Created by john on 30/07/2017.
 */

public class BlockedUsersRecyclerHolder extends RecyclerView.ViewHolder {

    TextView txtName,txtID,txtDate;
    public BlockedUsersRecyclerHolder(View itemView) {
        super(itemView);

        txtName= (TextView) itemView.findViewById(R.id.txtName);
        txtID= (TextView) itemView.findViewById(R.id.txtID);
        txtDate= (TextView) itemView.findViewById(R.id.txtDate);
    }
}
