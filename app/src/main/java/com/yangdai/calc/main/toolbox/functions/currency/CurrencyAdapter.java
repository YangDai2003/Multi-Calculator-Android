package com.yangdai.calc.main.toolbox.functions.currency;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.imageview.ShapeableImageView;
import com.yangdai.calc.R;

import java.util.List;

/**
 * @author 30415
 */
public class CurrencyAdapter extends ArrayAdapter<Currency> {
    private final boolean showEnglishName;

    public CurrencyAdapter(Context context, List<Currency> currencies, boolean showEnglishName) {
        super(context, 0, currencies);
        this.showEnglishName = showEnglishName;
    }

    @NonNull
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.currency_layout, parent, false);
        }

        Currency currency = getItem(position);
        if (currency != null) {
            ShapeableImageView shapeableImageView = convertView.findViewById(R.id.flag);
            shapeableImageView.setImageResource(currency.id());
            TextView textView = convertView.findViewById(R.id.symbol);
            textView.setText(currency.symbol());
            TextView textView1 = convertView.findViewById(R.id.name);
            if (!showEnglishName) {
                textView1.setText(currency.chineseName());
            } else {
                textView1.setText(currency.englishName());
            }
        }

        return convertView;
    }
}
