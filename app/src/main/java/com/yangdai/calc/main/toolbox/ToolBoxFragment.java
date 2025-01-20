package com.yangdai.calc.main.toolbox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yangdai.calc.main.toolbox.functions.bmi.BMIActivity;
import com.yangdai.calc.main.toolbox.functions.chinese.ChineseNumberConversionActivity;
import com.yangdai.calc.main.toolbox.functions.programmer.ProgrammerActivity;
import com.yangdai.calc.main.toolbox.functions.ruler.RulerActivity;
import com.yangdai.calc.main.toolbox.functions.shopping.ShoppingActivity;
import com.yangdai.calc.main.toolbox.functions.algebra.StatisticsActivity;
import com.yangdai.calc.R;
import com.yangdai.calc.main.toolbox.functions.fraction.FractionActivity;
import com.yangdai.calc.main.toolbox.functions.equation.EquationActivity;
import com.yangdai.calc.main.toolbox.functions.random.RandomNumberActivity;
import com.yangdai.calc.main.toolbox.functions.compass.Compass;
import com.yangdai.calc.main.toolbox.functions.converter.UnitActivity;
import com.yangdai.calc.main.toolbox.functions.currency.CurrencyActivity;
import com.yangdai.calc.main.toolbox.functions.finance.FinanceActivity;
import com.yangdai.calc.main.toolbox.functions.relationship.RelationshipActivity;
import com.yangdai.calc.main.toolbox.functions.time.DateRangeActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 30415
 */
public class ToolBoxFragment extends Fragment {
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    ToolBoxAdapter adapter;
    private List<ToolBoxItem> newData;
    private List<ToolBoxItem> data;
    boolean isGrid;

    public ToolBoxFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = createToolBoxItems();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private List<ToolBoxItem> createToolBoxItems() {
        List<ToolBoxItem> items = new ArrayList<>();
        items.add(new ToolBoxItem(Constants.UNIT_ACTIVITY_ID, getString(R.string.UnitsActivity), getResources().getDrawable(R.drawable.unit_icon, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.DATE_RANGE_ACTIVITY_ID, getString(R.string.dateActivity), getResources().getDrawable(R.drawable.date_range_icon, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.FINANCE_ACTIVITY_ID, getString(R.string.financeActivity), getResources().getDrawable(R.drawable.finance_icon, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.COMPASS_ACTIVITY_ID, getString(R.string.compassActivity), getResources().getDrawable(R.drawable.compass_icon, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.BMI_ACTIVITY_ID, getString(R.string.bmiActivity), getResources().getDrawable(R.drawable.bmi_icon, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.SHOPPING_ACTIVITY_ID, getString(R.string.shoppingActivity), getResources().getDrawable(R.drawable.shopping_icon, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.CURRENCY_ACTIVITY_ID, getString(R.string.exchangeActivity), getResources().getDrawable(R.drawable.currency_exchange_icon, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.CHINESE_NUMBER_CONVERSION_ACTIVITY_ID, getString(R.string.chineseNumberConverter), getResources().getDrawable(R.drawable.chinese_number_icon, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.RELATIONSHIP_ACTIVITY_ID, getString(R.string.relationshipActivity), getResources().getDrawable(R.drawable.relation_icon, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.RANDOM_ACTIVITY_ID, getString(R.string.randomActivity), getResources().getDrawable(R.drawable.random_number_icon, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.FUNCTION_ACTIVITY_ID, getString(R.string.EquationActivity), getResources().getDrawable(R.drawable.functions_icon, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.STATISTICS_ACTIVITY_ID, getString(R.string.statisticActivity), getResources().getDrawable(R.drawable.statistics_icon, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.FRACTION_ACTIVITY_ID, getString(R.string.numberConvert), getResources().getDrawable(R.drawable.fraction, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.PROGRAMMER_ACTIVITY_ID, getString(R.string.programmer), getResources().getDrawable(R.drawable.binary_icon, requireContext().getTheme())));
        items.add(new ToolBoxItem(Constants.RULER_ACTIVITY_ID, getString(R.string.ruler), getResources().getDrawable(R.drawable.ruler_icon, requireContext().getTheme())));
        return items;
    }

    public static ToolBoxFragment newInstance() {
        return new ToolBoxFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_toolbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View notes, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(notes, savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        isGrid = sharedPreferences.getBoolean("GridLayout", true);
        getParentFragmentManager().setFragmentResultListener("ChangeLayout", getViewLifecycleOwner(), (requestKey, bundle) -> {
            isGrid = bundle.getBoolean("GridLayout", true);
            updateRecycleView(isGrid);
        });
        String order = sharedPreferences.getString("order", Constants.ORDER);
        List<String> orderList = new ArrayList<>(Arrays.asList(order.split("/")));
        if (orderList.size() < data.size()) {
            int oLength = orderList.size();
            for (int i = oLength; i < data.size(); i++) {
                orderList.add(String.valueOf(i));
            }
            String orderString = TextUtils.join("/", orderList);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("order", orderString);
            editor.apply();
        } else if (orderList.size() > data.size()) {
            orderList = new ArrayList<>(Arrays.asList(Constants.ORDER.split("/")));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("order", Constants.ORDER);
            editor.apply();
        }

        newData = new ArrayList<>();
        for (String c : orderList) {
            int index = Integer.parseInt(c);
            newData.add(data.get(index));
        }

        recyclerView = requireView().findViewById(R.id.recyclerView);
        updateRecycleView(isGrid);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createItemTouchHelperCallback());
        // 将 ItemTouchHelper 与 RecyclerView 关联
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private ItemTouchHelper.Callback createItemTouchHelperCallback() {
        return new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                // 设置拖动和滑动的方向
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // 处理拖动操作，更新数据集和适配器
                int fromPosition = viewHolder.getBindingAdapterPosition();
                int toPosition = target.getBindingAdapterPosition();
                ToolBoxItem movedToolBoxItem = newData.remove(fromPosition);
                newData.add(toPosition, movedToolBoxItem);
                adapter.notifyItemMoved(fromPosition, toPosition);
                StringBuilder newOrder = new StringBuilder();
                for (int i = 0; i < newData.size(); i++) {
                    newOrder.append(newData.get(i).id()).append("/");
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("order", newOrder.toString());
                editor.apply();
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // 处理滑动操作
            }
        };
    }

    private void updateRecycleView(boolean isGrid) {
        adapter = new ToolBoxAdapter(newData, isGrid, this::startActivityById);
        recyclerView.setLayoutManager(isGrid ? new GridLayoutManager(getContext(), 3) : new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void startActivityById(ToolBoxItem item) {
        Intent intent = null;
        switch (item.id()) {
            case Constants.UNIT_ACTIVITY_ID ->
                    intent = new Intent(getContext(), UnitActivity.class);
            case Constants.DATE_RANGE_ACTIVITY_ID ->
                    intent = new Intent(getContext(), DateRangeActivity.class);
            case Constants.FINANCE_ACTIVITY_ID ->
                    intent = new Intent(getContext(), FinanceActivity.class);
            case Constants.COMPASS_ACTIVITY_ID -> intent = new Intent(getContext(), Compass.class);
            case Constants.BMI_ACTIVITY_ID -> intent = new Intent(getContext(), BMIActivity.class);
            case Constants.SHOPPING_ACTIVITY_ID ->
                    intent = new Intent(getContext(), ShoppingActivity.class);
            case Constants.CURRENCY_ACTIVITY_ID ->
                    intent = new Intent(getContext(), CurrencyActivity.class);
            case Constants.CHINESE_NUMBER_CONVERSION_ACTIVITY_ID ->
                    intent = new Intent(getContext(), ChineseNumberConversionActivity.class);
            case Constants.RELATIONSHIP_ACTIVITY_ID ->
                    intent = new Intent(getContext(), RelationshipActivity.class);
            case Constants.RANDOM_ACTIVITY_ID ->
                    intent = new Intent(getContext(), RandomNumberActivity.class);
            case Constants.FUNCTION_ACTIVITY_ID ->
                    intent = new Intent(getContext(), EquationActivity.class);
            case Constants.STATISTICS_ACTIVITY_ID ->
                    intent = new Intent(getContext(), StatisticsActivity.class);
            case Constants.FRACTION_ACTIVITY_ID ->
                    intent = new Intent(getContext(), FractionActivity.class);
            case Constants.PROGRAMMER_ACTIVITY_ID ->
                    intent = new Intent(getContext(), ProgrammerActivity.class);
            case Constants.RULER_ACTIVITY_ID ->
                    intent = new Intent(getContext(), RulerActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private static class Constants {
        public static final String ORDER = "0/1/2/3/4/5/6/7/8/9/10/11/12/13/14";
        public static final int UNIT_ACTIVITY_ID = 0;
        public static final int DATE_RANGE_ACTIVITY_ID = 1;
        public static final int FINANCE_ACTIVITY_ID = 2;
        public static final int COMPASS_ACTIVITY_ID = 3;
        public static final int BMI_ACTIVITY_ID = 4;
        public static final int SHOPPING_ACTIVITY_ID = 5;
        public static final int CURRENCY_ACTIVITY_ID = 6;
        public static final int CHINESE_NUMBER_CONVERSION_ACTIVITY_ID = 7;
        public static final int RELATIONSHIP_ACTIVITY_ID = 8;
        public static final int RANDOM_ACTIVITY_ID = 9;
        public static final int FUNCTION_ACTIVITY_ID = 10;
        public static final int STATISTICS_ACTIVITY_ID = 11;
        public static final int FRACTION_ACTIVITY_ID = 12;
        public static final int PROGRAMMER_ACTIVITY_ID = 13;
        public static final int RULER_ACTIVITY_ID = 14;
    }
}
