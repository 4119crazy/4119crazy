package com.adpter.data;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.uservalidation.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<ViewH> {

    private ArrayList<Country> selectedCountries = new ArrayList<>();
    private final LayoutInflater inflater;
    private PickCountryCallback callback = null;
    private final Context context;
    public Adapter(Context ctx) {
        inflater = LayoutInflater.from(ctx);
        context = ctx;
    }

    public void setSelectedCountries(ArrayList<Country> selectedCountries) {
        this.selectedCountries = selectedCountries;
        notifyDataSetChanged();
    }

    public void setCallback(PickCountryCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewH(inflater.inflate(R.layout.item_country, parent, false));
    }

    private int itemHeight = -1;
    public void setItemHeight(float dp) {
        itemHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, context.getResources().getDisplayMetrics());
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewH holder, int position) {
        final Country country = selectedCountries.get(position);
        holder.ivFlag.setImageResource(country.flag);
        holder.tvName.setText(country.name);
        holder.tvCode.setText("+" + country.code);
        if(itemHeight != -1) {
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height = itemHeight;
            holder.itemView.setLayoutParams(params);
        }
        holder.itemView.setOnClickListener(v -> {
            if(callback != null) callback.onPick(country);
        });
    }

    @Override
    public int getItemCount() {
        return selectedCountries.size();
    }

}
