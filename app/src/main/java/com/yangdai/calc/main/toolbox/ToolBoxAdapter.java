package com.yangdai.calc.main.toolbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.yangdai.calc.R;
import com.yangdai.calc.main.ItemClick;

import java.util.List;

/**
 * @author 30415
 */
public class ToolBoxAdapter extends RecyclerView.Adapter<ToolBoxAdapter.ViewHolder> {
    private final List<ToolBoxItem> list;
    final ItemClick itemClick;
    private final boolean isGrid;

    public ToolBoxAdapter(List<ToolBoxItem> list, boolean isGrid, ItemClick itemClick) {
        this.list = list;
        this.itemClick = itemClick;
        this.isGrid = isGrid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isGrid){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_toolbox, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_toolbox, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(v -> {
            int position1 = viewHolder.getBindingAdapterPosition();
            itemClick.onClick(list.get(position1));
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setText(list.get(position).title());
        holder.imageView.setImageDrawable(list.get(position).drawable());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        final ShapeableImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
