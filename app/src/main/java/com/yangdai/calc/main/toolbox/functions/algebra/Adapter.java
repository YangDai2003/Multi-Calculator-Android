package com.yangdai.calc.main.toolbox.functions.algebra;

import static com.yangdai.calc.utils.Utils.closeKeyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yangdai.calc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 30415
 */
public class Adapter extends RecyclerView.Adapter<Adapter.viewHolder> {
    private final List<Item> list;
    private final TextWatcher textWatcher;
    private final List<viewHolder> viewHolderList;
    private final Context context;

    public Adapter(Context context, List<Item> list, TextWatcher textWatcher) {
        this.list = list;
        this.textWatcher = textWatcher;
        viewHolderList = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.algebra_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.setId("(" + (position + 1) + ")");
        holder.setText(list.get(position).content());
        holder.editText.addTextChangedListener(textWatcher);
        holder.editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard((Activity) context);
                holder.editText.clearFocus();
                return true;
            }
            return false;
        });
        viewHolderList.add(holder);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    public void remove(int position) {
        list.remove(position);
        viewHolderList.remove(position);
        for (int i = 0; i < viewHolderList.size(); i++) {
            viewHolderList.get(i).setId("(" + (i + 1) + ")");
        }
        notifyItemRemoved(position);
    }

    public void add() {
        list.add(new Item(""));
        notifyItemInserted(list.size());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<String> getAllInput() {
        List<String> editTextTextList = new ArrayList<>();
        for (viewHolder holder : viewHolderList) {
            editTextTextList.add(holder.getText());
        }
        return editTextTextList;
    }

    static class viewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        final EditText editText;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_id);
            editText = itemView.findViewById(R.id.content);
        }

        public String getText() {
            return editText.getText().toString();
        }

        public void setText(String content) {
            editText.setText(content);
        }

        public void setId(String id) {
            textView.setText(id);
        }
    }
}
