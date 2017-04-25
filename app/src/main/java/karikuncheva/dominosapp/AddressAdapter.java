package karikuncheva.dominosapp;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import karikuncheva.dominosapp.model.Address;

/**
 * Created by Mariela Zviskova on 9.4.2017 г..
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private Activity activity;
    private List<Address> addresses;

    public AddressAdapter(Activity activity, List<Address> addresses) {
        this.activity = activity;
        this.addresses = MainActivity.loggedUser.getAddresses();
    }

    @Override
    public AddressAdapter.AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(activity);
        View row = li.inflate(R.layout.single_row_address, parent, false);
        AddressAdapter.AddressViewHolder vh = new AddressAdapter.AddressViewHolder(row);
        return vh;
    }

    @Override
    public void onBindViewHolder(final AddressAdapter.AddressViewHolder vh, final int position) {
        final Address address = addresses.get(position);
        vh.town.setText(address.getTown());
        vh.infoAddress.setText(address.toString());

        vh.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addresses.remove(address);
                notifyDataSetChanged();
                DBManager.getInstance(activity).deleteAddress(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView town;
        TextView infoAddress;
        ImageButton delete;

        AddressViewHolder(View row) {
            super(row);
            town = (TextView) row.findViewById(R.id.town);
            infoAddress = (TextView) row.findViewById(R.id.info_address);
            delete = (ImageButton) row.findViewById(R.id.delete_address);

        }
    }
}
