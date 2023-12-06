package com.yangdai.calc.main.toolbox.functions.currency;

import static com.yangdai.calc.utils.Utils.closeKeyboard;
import static com.yangdai.calc.utils.Utils.formatNumberFinance;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.imageview.ShapeableImageView;
import com.yangdai.calc.R;
import com.yangdai.calc.main.toolbox.functions.BaseFunctionActivity;
import com.yangdai.calc.utils.TouchAnimation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @author 30415
 */
public class CurrencyActivity extends BaseFunctionActivity {
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private EditText amountEditText;
    private TextView resultTextView, textView, nameFrom, nameTo;
    private CurrencyViewModel currencyViewModel;
    private ShapeableImageView imageViewFrom, imageViewTo;
    private boolean first = true, showEnglishName;
    private static final List<Currency> CURRENCIES = new ArrayList<>(Arrays.asList(
            new Currency(R.drawable.australia, "AUD", "澳大利亚元", "Australian Dollar"),
            new Currency(R.drawable.bulgaria, "BGN", "保加利亚列弗", "Bulgarian Lev"),
            new Currency(R.drawable.brazil, "BRL", "巴西雷亚尔", "Brazilian Real"),
            new Currency(R.drawable.canada, "CAD", "加拿大元", "Canadian Dollar"),
            new Currency(R.drawable.switzerland, "CHF", "瑞士法郎", "Swiss Franc"),
            new Currency(R.drawable.china, "CNY", "人民币", "Chinese Yuan"),
            new Currency(R.drawable.czechia, "CZK", "捷克克朗", "Czech Koruna"),
            new Currency(R.drawable.denmark, "DKK", "丹麦克朗", "Danish Krone"),
            new Currency(R.drawable.eu, "EUR", "欧元", "Euro"),
            new Currency(R.drawable.uk, "GBP", "英镑", "British Pound"),
            new Currency(R.drawable.hongkongchina, "HKD", "港元", "Hong Kong Dollar"),
            new Currency(R.drawable.hungary, "HUF", "匈牙利福林", "Hungarian Forint"),
            new Currency(R.drawable.indonesia, "IDR", "印尼卢比", "Indonesian Rupiah"),
            new Currency(R.drawable.israel, "ILS", "以色列新谢克尔", "Israeli Shekel"),
            new Currency(R.drawable.india, "INR", "印度卢比", "Indian Rupee"),
            new Currency(R.drawable.iceland, "ISK", "冰岛克朗", "Icelandic Króna"),
            new Currency(R.drawable.japan, "JPY", "日元", "Japanese Yen"),
            new Currency(R.drawable.south_korea, "KRW", "韩元", "South Korean Won"),
            new Currency(R.drawable.mexico, "MXN", "墨西哥比索", "Mexican Peso"),
            new Currency(R.drawable.malaysia, "MYR", "马来西亚林吉特", "Malaysian Ringgit"),
            new Currency(R.drawable.norway, "NOK", "挪威克朗", "Norwegian Krone"),
            new Currency(R.drawable.new_zealand, "NZD", "新西兰元", "New Zealand Dollar"),
            new Currency(R.drawable.philippines, "PHP", "菲律宾比索", "Philippine Peso"),
            new Currency(R.drawable.poland, "PLN", "波兰兹罗提", "Polish Zloty"),
            new Currency(R.drawable.romania, "RON", "罗马尼亚列伊", "Romanian Leu"),
            new Currency(R.drawable.sweden, "SEK", "瑞典克朗", "Swedish Krona"),
            new Currency(R.drawable.singapore, "SGD", "新加坡元", "Singapore Dollar"),
            new Currency(R.drawable.thailand, "THB", "泰铢", "Thai Baht"),
            new Currency(R.drawable.turkey, "TRY", "土耳其里拉", "Turkish Lira"),
            new Currency(R.drawable.us, "USD", "美元", "United States Dollar"),
            new Currency(R.drawable.south_africa, "ZAR", "南非兰特", "South African Rand")
    ));

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.currency_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.tip) {
            boolean showed = findViewById(R.id.list_view_currency).getVisibility() == View.VISIBLE;
            findViewById(R.id.list_view_currency).setVisibility(showed ? View.GONE : View.VISIBLE);
            if (showed) {
                item.setIcon(R.drawable.tips_off);
            } else {
                item.setIcon(R.drawable.tips_on);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUi();

        // 获取当前应用语言
        Configuration config = getResources().getConfiguration();
        Locale currentLocale = config.getLocales().get(0);
        showEnglishName = !currentLocale.getLanguage().equals("zh");

        List<String> currenciesList = new ArrayList<>();
        currenciesList.add("N/A");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                currenciesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrencySpinner.setAdapter(adapter);
        fromCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCurrency = parent.getItemAtPosition(position).toString();
                CURRENCIES.forEach(currency -> {
                    if (selectedCurrency.equals(currency.symbol())) {
                        imageViewFrom.setImageResource(currency.id());
                        nameFrom.setText(showEnglishName ? currency.englishName() : currency.chineseName());
                    }
                });
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
                String selectedCurrency = parent.getItemAtPosition(position).toString();
                CURRENCIES.forEach(currency -> {
                    if (selectedCurrency.equals(currency.symbol())) {
                        imageViewTo.setImageResource(currency.id());
                        nameTo.setText(showEnglishName ? currency.englishName() : currency.chineseName());
                    }
                });
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
                    int from = defaultSp.getInt("from", 0);
                    int to = defaultSp.getInt("to", 0);
                    fromCurrencySpinner.setSelection(from);
                    toCurrencySpinner.setSelection(to);
                    imageViewFrom.setImageResource(CURRENCIES.get(from).id());
                    imageViewTo.setImageResource(CURRENCIES.get(to).id());
                    nameFrom.setText(showEnglishName ? CURRENCIES.get(from).englishName() : CURRENCIES.get(from).chineseName());
                    nameTo.setText(showEnglishName ? CURRENCIES.get(to).englishName() : CURRENCIES.get(to).chineseName());
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
        CurrencyAdapter adapter1 = new CurrencyAdapter(this, CURRENCIES, showEnglishName);
        listView.setAdapter(adapter1);
    }

    @Override
    protected void setRootView() {
        setContentView(R.layout.activity_exchange);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUi() {
        resultTextView = findViewById(R.id.result_text_view);
        fromCurrencySpinner = findViewById(R.id.from_currency_spinner);
        toCurrencySpinner = findViewById(R.id.to_currency_spinner);
        amountEditText = findViewById(R.id.amount_edit_text);
        textView = findViewById(R.id.textView2);
        imageViewFrom = findViewById(R.id.flag_from);
        imageViewTo = findViewById(R.id.flag_to);
        nameFrom = findViewById(R.id.name_from);
        nameTo = findViewById(R.id.name_to);
        ImageView imageView = findViewById(R.id.switchCurrency);
        TouchAnimation touchAnimation = new TouchAnimation(imageView);
        imageView.setOnTouchListener(touchAnimation);
        imageView.setOnClickListener(v -> {
            // 获取当前fromSpinner和toSpinner的选中位置
            int fromPosition = fromCurrencySpinner.getSelectedItemPosition();
            int toPosition = toCurrencySpinner.getSelectedItemPosition();

            // 交换fromSpinner和toSpinner的选中位置
            fromCurrencySpinner.setSelection(toPosition);
            toCurrencySpinner.setSelection(fromPosition);
        });
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
