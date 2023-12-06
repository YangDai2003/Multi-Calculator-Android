package com.yangdai.calc.main.calculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yangdai.calc.R;
import com.yangdai.calc.utils.Utils;

import java.util.List;

public class HistoryAdapter extends ArrayAdapter<String> {
    private final HistoryItemClick itemClick;

    public HistoryAdapter(@NonNull Context context, @NonNull List<String> objects, HistoryItemClick itemClick) {
        super(context, 0, objects);
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        String item = getItem(position);
        if (item != null) {
            TextView input = convertView.findViewById(R.id.input_tv);
            TextView output = convertView.findViewById(R.id.output_tv);

            String[] parts = item.split("=");
            if (parts.length == 2) {
                String leftSide = parts[0].trim();
                String rightSide = parts[1].trim();

                input.setText(leftSide);
                CalculatorUtils.highlightSpecialSymbols(input);
                output.setText(Utils.formatNumber(rightSide));

                View.OnClickListener onClickListener = v -> itemClick.onClick(rightSide);

                convertView.setOnClickListener(onClickListener);
                input.setOnClickListener(v -> itemClick.onClick(leftSide));
                output.setOnClickListener(onClickListener);
            }

        }

        return convertView;
    }
}
