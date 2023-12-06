package com.yangdai.calc.main.toolbox.functions.algebra;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;


import com.yangdai.calc.R;
import com.yangdai.calc.main.toolbox.functions.BaseFunctionActivity;
import com.yangdai.calc.utils.TouchAnimation;
import com.yangdai.calc.utils.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 30415
 */
public class StatisticsActivity extends BaseFunctionActivity {

    private Adapter adapter;
    private TextView tvGcd, tvLcm, tvAvg0, tvAvg1, tvAvg2, tvAvg3, tvSum, tvDiff0, tvDiff1;
    private List<String> inputStringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        tvGcd = findViewById(R.id.gcd);
        tvLcm = findViewById(R.id.lcm);
        tvAvg0 = findViewById(R.id.avg0);
        tvAvg1 = findViewById(R.id.avg1);
        tvAvg2 = findViewById(R.id.avg2);
        tvAvg3 = findViewById(R.id.avg3);
        tvDiff0 = findViewById(R.id.diff0);
        tvDiff1 = findViewById(R.id.diff1);
        tvSum = findViewById(R.id.sum);
        findViewById(R.id.bt_add).setOnClickListener(v -> adapter.add());
        TouchAnimation touchAnimation = new TouchAnimation(findViewById(R.id.bt_add));
        findViewById(R.id.bt_add).setOnTouchListener(touchAnimation);

        List<Item> dataList = new ArrayList<>();
        dataList.add(new Item(""));
        dataList.add(new Item(""));

        adapter = new Adapter(this, dataList, textWatcher);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void setRootView() {
        setContentView(R.layout.activity_statistic);
    }

    private final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getBindingAdapterPosition() == 0 || viewHolder.getBindingAdapterPosition() == 1) {
                return 0;
            }
            int swiped = ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
            //第一个参数拖动，第二个删除侧滑
            return makeMovementFlags(0, swiped);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getBindingAdapterPosition();
            adapter.remove(position);
            inputStringList = adapter.getAllInput();
            calculate(inputStringList);
        }
    });

    final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            inputStringList = adapter.getAllInput();
            calculate(inputStringList);
        }
    };

    private void calculate(List<String> inputStringList) {
        List<BigInteger> integerList = inputStringList.stream()
                .filter(str -> !str.isEmpty())
                .map(BigInteger::new)
                .collect(Collectors.toList());
        calculateGcd(integerList);
        calculateLcm(integerList);
        calculateAvg0(integerList);
        calculateAvg1(integerList);
        calculateAvg2(integerList);
        calculateAvg3(integerList);
        calculateVariance(integerList);
        calculateStandardDeviation(integerList);
        calculateSum(integerList);
    }

    @SuppressLint("SetTextI18n")
    private void calculateGcd(List<BigInteger> integerList) {
        try {
            tvGcd.setText(Utils.formatNumber(Utils.gcdMultiple(integerList).toString()));
        } catch (Exception e) {
            tvGcd.setText("");
        }
    }

    @SuppressLint("SetTextI18n")
    private void calculateLcm(List<BigInteger> integerList) {
        try {
            tvLcm.setText(Utils.formatNumber(Utils.lcmMultiple(integerList).toString()));
        } catch (Exception e) {
            tvLcm.setText("");
        }
    }

    @SuppressLint("SetTextI18n")
    private void calculateAvg0(List<BigInteger> integerList) {
        BigInteger sum = integerList.stream()
                .reduce(BigInteger.ZERO, BigInteger::add);
        try {
            BigDecimal avg = new BigDecimal(sum).divide(BigDecimal.valueOf(integerList.size()), 10, BigDecimal.ROUND_HALF_UP);
            tvAvg0.setText(Utils.formatNumber(avg.toBigDecimal().toPlainString()));
        } catch (Exception e) {
            tvAvg0.setText("");
        }
    }

    @SuppressLint("SetTextI18n")
    private void calculateAvg1(List<BigInteger> integerList) {
        if (integerList.isEmpty()) {
            tvAvg1.setText("");
            return;
        }
        double product = integerList.stream()
                .mapToDouble(BigInteger::doubleValue)
                .reduce(1, (a, b) -> a * b);
        try {
            double avg = Math.pow(product, 1.0 / integerList.size());
            tvAvg1.setText(Utils.formatNumber(Double.toString(avg)));
        } catch (Exception e) {
            tvAvg1.setText("");
        }
    }

    @SuppressLint("SetTextI18n")
    private void calculateAvg2(List<BigInteger> integerList) {
        double sumReciprocals = integerList.stream()
                .mapToDouble(value -> 1.0 / value.doubleValue())
                .sum();
        try {
            double avg = integerList.size() / sumReciprocals;
            tvAvg2.setText(Utils.formatNumber(Double.toString(avg)));
        } catch (Exception e) {
            tvAvg2.setText("");
        }
    }

    @SuppressLint("SetTextI18n")
    private void calculateAvg3(List<BigInteger> integerList) {
        double sumSquares = integerList.stream()
                .mapToDouble(value -> Math.pow(value.doubleValue(), 2))
                .sum();
        try {
            double avg = Math.sqrt(sumSquares / integerList.size());
            tvAvg3.setText(Utils.formatNumber(Double.toString(avg)));
        } catch (Exception e) {
            tvAvg3.setText("");
        }
    }

    private double calculateVariance(List<BigInteger> integerList) {
        double sum = 0;
        double sumSquares = 0;
        int size = integerList.size();

        for (BigInteger value : integerList) {
            double doubleValue = value.doubleValue();
            sum += doubleValue;
            sumSquares += Math.pow(doubleValue, 2);
        }

        try {
            double mean = sum / size;
            double variance = (sumSquares / size) - Math.pow(mean, 2);
            tvDiff0.setText(Utils.formatNumber(Double.toString(variance)));
            return variance;
        } catch (Exception e) {
            tvDiff0.setText("");
            return 0;
        }
    }

    private void calculateStandardDeviation(List<BigInteger> integerList) {
        double variance = calculateVariance(integerList);
        double standardDeviation = Math.sqrt(variance);
        tvDiff1.setText(Utils.formatNumber(Double.toString(standardDeviation)));
    }


    @SuppressLint("SetTextI18n")
    private void calculateSum(List<BigInteger> integerList) {
        if (integerList.isEmpty()) {
            tvSum.setText("");
            return;
        }
        BigInteger sum = integerList.stream()
                .reduce(BigInteger.ZERO, BigInteger::add);
        try {
            tvSum.setText(Utils.formatNumber(sum.toString()));
        } catch (Exception e) {
            tvSum.setText("");
        }
    }
}