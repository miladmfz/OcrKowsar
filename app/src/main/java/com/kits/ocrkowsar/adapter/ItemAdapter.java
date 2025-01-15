package com.kits.ocrkowsar.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.activity.OcrFactorListActivity;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final Context mContext;

    private List<String> items;
    private List<String> selectedItems = new ArrayList<>(); // List baraye zakhire item haye entekhab shode

    public ItemAdapter(Context context, List<String> items) {

        this.items = items;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.kits.ocrkowsar.R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = items.get(position);
        holder.textView.setText(item);
        holder.checkBox.setOnCheckedChangeListener(null); // Prevent re-binding issues

        holder.checkBox.setChecked(selectedItems.contains(item));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.add(item);
            } else {
                selectedItems.remove(item);
            }

            OcrFactorListActivity activity = (OcrFactorListActivity) mContext;
            activity.CheckStackList();

        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<String> getSelectedItems() {
        return selectedItems;
    }
    public void Clear_selectedItems() {
        selectedItems.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            textView = itemView.findViewById(R.id.textview);
        }
    }
}
