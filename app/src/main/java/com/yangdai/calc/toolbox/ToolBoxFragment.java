package com.yangdai.calc.toolbox;

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

import com.yangdai.calc.BMIActivity;
import com.yangdai.calc.ChineseNumberConversionActivity;
import com.yangdai.calc.shopping.ShoppingActivity;
import com.yangdai.calc.algebra.StatisticsActivity;
import com.yangdai.calc.R;
import com.yangdai.calc.fraction.FractionActivity;
import com.yangdai.calc.function.FunctionActivity;
import com.yangdai.calc.random.RandomNumberActivity;
import com.yangdai.calc.compass.Compass;
import com.yangdai.calc.converter.UnitActivity;
import com.yangdai.calc.currency.CurrencyActivity;
import com.yangdai.calc.finance.FinanceActivity;
import com.yangdai.calc.relationship.RelationshipActivity;
import com.yangdai.calc.time.DateRangeActivity;

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
    private static final String ORDER = "0/1/2/3/4/5/6/7/8/9/10/11/12";
    boolean isGrid;
    private static final int UNIT_ACTIVITY_ID = 0;
    private static final int DATE_RANGE_ACTIVITY_ID = 1;
    private static final int FINANCE_ACTIVITY_ID = 2;
    private static final int COMPASS_ACTIVITY_ID = 3;
    private static final int BMI_ACTIVITY_ID = 4;
    private static final int SHOPPING_ACTIVITY_ID = 5;
    private static final int CURRENCY_ACTIVITY_ID = 6;
    private static final int CHINESE_NUMBER_CONVERSION_ACTIVITY_ID = 7;
    private static final int RELATIONSHIP_ACTIVITY_ID = 8;
    private static final int RANDOM_ACTIVITY_ID = 9;
    private static final int FUNCTION_ACTIVITY_ID = 10;
    private static final int STATISTICS_ACTIVITY_ID = 11;
    private static final int FRACTION_ACTIVITY_ID = 12;

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
        items.add(new ToolBoxItem(UNIT_ACTIVITY_ID, getString(R.string.ChangeActivity), getResources().getDrawable(R.drawable.unit_icon, getContext().getTheme())));
        items.add(new ToolBoxItem(DATE_RANGE_ACTIVITY_ID, getString(R.string.dateActivity), getResources().getDrawable(R.drawable.date_range_icon, getContext().getTheme())));
        items.add(new ToolBoxItem(FINANCE_ACTIVITY_ID, getString(R.string.financeActivity), getResources().getDrawable(R.drawable.finance_icon, getContext().getTheme())));
        items.add(new ToolBoxItem(COMPASS_ACTIVITY_ID, getString(R.string.compassActivity), getResources().getDrawable(R.drawable.compass_icon, getContext().getTheme())));
        items.add(new ToolBoxItem(BMI_ACTIVITY_ID, getString(R.string.bmiActivity), getResources().getDrawable(R.drawable.bmi_icon, getContext().getTheme())));
        items.add(new ToolBoxItem(SHOPPING_ACTIVITY_ID, getString(R.string.shoppingActivity), getResources().getDrawable(R.drawable.discount_icon, getContext().getTheme())));
        items.add(new ToolBoxItem(CURRENCY_ACTIVITY_ID, getString(R.string.exchangeActivity), getResources().getDrawable(R.drawable.currency_exchange_icon, getContext().getTheme())));
        items.add(new ToolBoxItem(CHINESE_NUMBER_CONVERSION_ACTIVITY_ID, getString(R.string.chineseNumberConverter), getResources().getDrawable(R.drawable.chinese_number_icon, getContext().getTheme())));
        items.add(new ToolBoxItem(RELATIONSHIP_ACTIVITY_ID, getString(R.string.relationshipActivity), getResources().getDrawable(R.drawable.relation_icon, getContext().getTheme())));
        items.add(new ToolBoxItem(RANDOM_ACTIVITY_ID, getString(R.string.randomActivity), getResources().getDrawable(R.drawable.random_number_icon, getContext().getTheme())));
        items.add(new ToolBoxItem(FUNCTION_ACTIVITY_ID, getString(R.string.functionActivity), getResources().getDrawable(R.drawable.functions_icon, getContext().getTheme())));
        items.add(new ToolBoxItem(STATISTICS_ACTIVITY_ID, getString(R.string.statisticActivity), getResources().getDrawable(R.drawable.statistics_icon, getContext().getTheme())));
        items.add(new ToolBoxItem(FRACTION_ACTIVITY_ID, getString(R.string.numberConvert), getResources().getDrawable(R.drawable.fraction, getContext().getTheme())));
        return items;
    }

    public static ToolBoxFragment newInstance() {
        return new ToolBoxFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
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
        String order = sharedPreferences.getString("order", ORDER);
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
            orderList = new ArrayList<>(Arrays.asList(ORDER.split("/")));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("order", ORDER);
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
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
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
        adapter = new ToolBoxAdapter(newData, isGrid, (item) -> {
            int itemId = item.id();
            switch (itemId) {
                case UNIT_ACTIVITY_ID ->
                        startActivity(new Intent(getContext(), UnitActivity.class));
                case DATE_RANGE_ACTIVITY_ID ->
                        startActivity(new Intent(getContext(), DateRangeActivity.class));
                case FINANCE_ACTIVITY_ID ->
                        startActivity(new Intent(getContext(), FinanceActivity.class));
                case COMPASS_ACTIVITY_ID -> startActivity(new Intent(getContext(), Compass.class));
                case BMI_ACTIVITY_ID -> startActivity(new Intent(getContext(), BMIActivity.class));
                case SHOPPING_ACTIVITY_ID ->
                        startActivity(new Intent(getContext(), ShoppingActivity.class));
                case CURRENCY_ACTIVITY_ID ->
                        startActivity(new Intent(getContext(), CurrencyActivity.class));
                case CHINESE_NUMBER_CONVERSION_ACTIVITY_ID ->
                        startActivity(new Intent(getContext(), ChineseNumberConversionActivity.class));
                case RELATIONSHIP_ACTIVITY_ID ->
                        startActivity(new Intent(getContext(), RelationshipActivity.class));
                case RANDOM_ACTIVITY_ID ->
                        startActivity(new Intent(getContext(), RandomNumberActivity.class));
                case FUNCTION_ACTIVITY_ID ->
                        startActivity(new Intent(getContext(), FunctionActivity.class));
                case STATISTICS_ACTIVITY_ID ->
                        startActivity(new Intent(getContext(), StatisticsActivity.class));
                case FRACTION_ACTIVITY_ID ->
                        startActivity(new Intent(getContext(), FractionActivity.class));
                default -> {
                }
            }
        });
        recyclerView.setLayoutManager(isGrid ? new GridLayoutManager(getContext(), 3) : new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
