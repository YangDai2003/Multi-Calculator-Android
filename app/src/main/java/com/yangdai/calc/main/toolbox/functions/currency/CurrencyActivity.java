package com.yangdai.calc.main.toolbox.functions.currency;

import static com.yangdai.calc.utils.Utils.closeKeyboard;
import static com.yangdai.calc.utils.Utils.formatNumberFinance;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.elevation.SurfaceColors;
import com.yangdai.calc.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author 30415
 */
public class CurrencyActivity extends AppCompatActivity {
    private SharedPreferences defaultSp;
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private EditText amountEditText;
    private TextView resultTextView, textView;
    private CurrencyViewModel currencyViewModel;
    private boolean first = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.currency_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.tip) {
            boolean showed = findViewById(R.id.list_view_currency).getVisibility() == View.VISIBLE;
            findViewById(R.id.list_view_currency).setVisibility(showed ? View.GONE : View.VISIBLE);
            if (showed) {
                item.setIcon(R.drawable.tips_off);
            } else {
                item.setIcon(R.drawable.tips_on);
            }
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        setContentView(R.layout.activity_exchange);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        defaultSp = PreferenceManager.getDefaultSharedPreferences(this);

        if (defaultSp.getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        initUi();

        List<String> currenciesList = new ArrayList<>();
        currenciesList.add("N/A");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                currenciesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrencySpinner.setAdapter(adapter);
        fromCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateCurrency();
                if (!first) {
                    SharedPreferences.Editor editor = defaultSp.edit();
                    editor.putInt("from", position);
                    editor.apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        toCurrencySpinner.setAdapter(adapter);
        toCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateCurrency();
                if (!first) {
                    SharedPreferences.Editor editor = defaultSp.edit();
                    editor.putInt("to", position);
                    editor.apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Create the CurrencyViewModel
        currencyViewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);
        currencyViewModel.getExchangeRates().observe(this, exchangeRates -> {
            if (exchangeRates != null && !exchangeRates.isEmpty()) {
                String[] currencies = exchangeRates.keySet().toArray(new String[0]);
                Arrays.sort(currencies);
                adapter.clear();
                adapter.addAll(currencies);
                adapter.notifyDataSetChanged();

                if (first) {
                    first = false;
                    fromCurrencySpinner.setSelection(defaultSp.getInt("from", 0));
                    toCurrencySpinner.setSelection(defaultSp.getInt("to", 0));
                }

                String date;
                // 创建一个Calendar对象，并设置时区为中欧时区
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Central"));
                // 获取当前时间的小时数
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

                if (currentHour >= 15) {
                    // 当前时间大于等于15:00，输出当天日期
                    int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                    int currentMonth = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，需要加1
                    int currentYear = calendar.get(Calendar.YEAR);

                    date = currentYear + "-" + currentMonth + "-" + currentDay;
                } else {
                    // 当前时间小于15:00，输出前一天日期
                    calendar.add(Calendar.DAY_OF_MONTH, -1); // 将日期减一天

                    int previousDay = calendar.get(Calendar.DAY_OF_MONTH);
                    int previousMonth = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，需要加1
                    int previousYear = calendar.get(Calendar.YEAR);

                    date = previousYear + "-" + previousMonth + "-" + previousDay;
                }
                textView.setText(getString(R.string.ecb) + "\n" + getString(R.string.updateDate) + " " + date);
            }
        });

        currencyViewModel.loadExchangeRates();

        ListView listView = findViewById(R.id.list_view_currency);

        List<Currency> currencies = new ArrayList<>();
        currencies.add(new Currency("USD", "美元", "United States Dollar"));
        currencies.add(new Currency("EUR", "欧元", "Euro"));
        currencies.add(new Currency("JPY", "日元", "Japanese Yen"));
        currencies.add(new Currency("BGN", "保加利亚列弗", "Bulgarian Lev"));
        currencies.add(new Currency("CZK", "捷克克朗", "Czech Koruna"));
        currencies.add(new Currency("DKK", "丹麦克朗", "Danish Krone"));
        currencies.add(new Currency("GBP", "英镑", "British Pound"));
        currencies.add(new Currency("HUF", "匈牙利福林", "Hungarian Forint"));
        currencies.add(new Currency("PLN", "波兰兹罗提", "Polish Zloty"));
        currencies.add(new Currency("RON", "罗马尼亚列伊", "Romanian Leu"));
        currencies.add(new Currency("SEK", "瑞典克朗", "Swedish Krona"));
        currencies.add(new Currency("CHF", "瑞士法郎", "Swiss Franc"));
        currencies.add(new Currency("ISK", "冰岛克朗", "Icelandic Króna"));
        currencies.add(new Currency("NOK", "挪威克朗", "Norwegian Krone"));
        currencies.add(new Currency("TRY", "土耳其里拉", "Turkish Lira"));
        currencies.add(new Currency("AUD", "澳大利亚元", "Australian Dollar"));
        currencies.add(new Currency("BRL", "巴西雷亚尔", "Brazilian Real"));
        currencies.add(new Currency("CAD", "加拿大元", "Canadian Dollar"));
        currencies.add(new Currency("CNY", "人民币", "Chinese Yuan"));
        currencies.add(new Currency("HKD", "港元", "Hong Kong Dollar"));
        currencies.add(new Currency("IDR", "印尼卢比", "Indonesian Rupiah"));
        currencies.add(new Currency("ILS", "以色列新谢克尔", "Israeli Shekel"));
        currencies.add(new Currency("INR", "印度卢比", "Indian Rupee"));
        currencies.add(new Currency("KRW", "韩元", "South Korean Won"));
        currencies.add(new Currency("MXN", "墨西哥比索", "Mexican Peso"));
        currencies.add(new Currency("MYR", "马来西亚林吉特", "Malaysian Ringgit"));
        currencies.add(new Currency("NZD", "新西兰元", "New Zealand Dollar"));
        currencies.add(new Currency("PHP", "菲律宾比索", "Philippine Peso"));
        currencies.add(new Currency("SGD", "新加坡元", "Singapore Dollar"));
        currencies.add(new Currency("THB", "泰铢", "Thai Baht"));
        currencies.add(new Currency("ZAR", "南非兰特", "South African Rand"));

        currencies.sort(Comparator.comparing(c -> c.symbol().substring(0, 1)));

        CurrencyAdapter adapter1 = new CurrencyAdapter(this, currencies);
        listView.setAdapter(adapter1);
    }

    private void initUi() {
        resultTextView = findViewById(R.id.result_text_view);
        fromCurrencySpinner = findViewById(R.id.from_currency_spinner);
        toCurrencySpinner = findViewById(R.id.to_currency_spinner);
        amountEditText = findViewById(R.id.amount_edit_text);
        textView = findViewById(R.id.textView2);

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculateCurrency();
            }
        });
        amountEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(this);
                amountEditText.clearFocus();
                return true;
            }
            return false;
        });
    }

    public void calculateCurrency() {
        HashMap<String, Double> exchangeRates = currencyViewModel.getExchangeRates().getValue();
        if (exchangeRates == null || exchangeRates.isEmpty()) {
            // 检查问题
            return;
        }

        String amountStr = amountEditText.getText().toString();
        if (amountStr.isEmpty() || "N/A".equals(amountStr)) {
            resultTextView.setText("");
            return;
        }

        try {
            String fromCurrency = fromCurrencySpinner.getSelectedItem().toString();
            String toCurrency = toCurrencySpinner.getSelectedItem().toString();
            double amount = Double.parseDouble(amountStr);
            Double fromRate = exchangeRates.get(fromCurrency);
            Double toRate = exchangeRates.get(toCurrency);
            if (fromRate != null && toRate != null) {
                double result = (amount / fromRate) * toRate;
                resultTextView.setText(formatNumberFinance(String.valueOf(result)));
            }
        } catch (Exception e) {
            resultTextView.setText("");
        }
    }
}
