package com.yangdai.calc.main.calculator;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yangdai.calc.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 30415
 */
public class HistoryListFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences historySp;
    private MyScrollListView listView;
    private TextView textView;


    public HistoryListFragment() {
    }

    public static HistoryListFragment newInstance() {
        return new HistoryListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_list, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historySp = requireActivity().getSharedPreferences("history", MODE_PRIVATE);
        if (historySp != null) {
            historySp.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.historyList);
        textView = view.findViewById(R.id.historyHint);
        setupListView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (historySp != null) {
            historySp.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        if ("newHistory".equals(s)) {
            setupListView();
        }
    }

    private void setupListView() {
        if (listView == null) {
            return;
        }
        String historys = historySp.getString("newHistory", "");
        List<String> savedStringList = new ArrayList<>(Arrays.asList(historys.split("//")));
        savedStringList.removeIf(String::isEmpty);
        if (savedStringList.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
        HistoryAdapter adapter = new HistoryAdapter(requireContext(), savedStringList, str -> {
            if (!str.isEmpty()) {
                Bundle result = new Bundle();
                result.putString("select", str.trim());
                getParentFragmentManager().setFragmentResult("requestKey", result);
                ViewPager2 viewPager = requireParentFragment().requireView().findViewById(R.id.view_pager);
                viewPager.setCurrentItem(1);
            }
        });
        listView.setAdapter(adapter);
    }
}