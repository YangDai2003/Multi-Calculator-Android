package com.yangdai.calc;

import android.app.Dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 30415
 */
public class MyBottomSheetDialog extends BottomSheetDialogFragment {
    ListView listView;
    SharedPreferences history;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.history);
        history = requireActivity().getSharedPreferences("history", Context.MODE_PRIVATE);

        BottomSheetBehavior<FrameLayout> behavior = dialog.getBehavior();
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        behavior.setSkipCollapsed(false);
        behavior.setDraggable(true);

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Set<String> historys = history.getStringSet("historys", new HashSet<>());
        Set<String> temp = new HashSet<>(historys);
        String[] arr = temp.toArray(new String[0]);
        listView = view.findViewById(R.id.list);
        view.findViewById(R.id.delete).setOnClickListener(v -> {
            SharedPreferences.Editor editor = history.edit();
            editor.remove("historys");
            editor.apply();
            dismiss();
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.list_item, arr);
        listView.setAdapter(adapter);
        //为列表中选中的项添加单击响应事件
        listView.setOnItemClickListener((parent, view1, i, l) -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            Bundle result = new Bundle();
            result.putString("select", arr[i].split("=")[1].trim());
            fragmentManager.setFragmentResult("requestKey", result);
            dismiss();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history, container, false);
    }
}
