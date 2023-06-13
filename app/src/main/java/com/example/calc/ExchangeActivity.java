package com.example.calc;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.elevation.SurfaceColors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ExchangeActivity extends AppCompatActivity {
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private EditText amountEditText;
    private TextView resultTextView;

    private ExchangeViewModel exchangeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_exchange);

        fromCurrencySpinner = findViewById(R.id.from_currency_spinner);
        toCurrencySpinner = findViewById(R.id.to_currency_spinner);
        amountEditText = findViewById(R.id.amount_edit_text);
        resultTextView = findViewById(R.id.result_text_view);

        List<String> currenciesList = new ArrayList<>();
        Collections.addAll(currenciesList, getResources().getStringArray(R.array.currencies_array));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                currenciesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrencySpinner.setAdapter(adapter);
        toCurrencySpinner.setAdapter(adapter);

        // Create the ExchangeViewModel
        exchangeViewModel = new ViewModelProvider(this).get(ExchangeViewModel.class);
        exchangeViewModel.getExchangeRates().observe(this, exchangeRates -> {
            if (exchangeRates != null) {
                calculateCurrency(resultTextView);
                String[] currencies = exchangeRates.keySet().toArray(new String[0]);
                adapter.clear();
                adapter.addAll(currencies);
                adapter.notifyDataSetChanged();
            }
        });

        exchangeViewModel.loadExchangeRates();

        fromCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateCurrency(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        toCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateCurrency(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @SuppressLint("DefaultLocale")
    public void calculateCurrency(View view) {
        HashMap<String, Double> exchangeRates = exchangeViewModel.getExchangeRates().getValue();
        if (exchangeRates == null) {
            return; // Exchange rates not loaded yet, return early
        }

        try {
            String fromCurrency = fromCurrencySpinner.getSelectedItem().toString();
            String toCurrency = toCurrencySpinner.getSelectedItem().toString();
            double amount;
            amount = Double.parseDouble(amountEditText.getText().toString());
            double fromRate = exchangeRates.get(fromCurrency);
            double toRate = exchangeRates.get(toCurrency);

            double result = (amount / fromRate) * toRate;
            resultTextView.setText(String.format("%.2f", result));

        } catch (Exception ignored) {

        }
    }
}
