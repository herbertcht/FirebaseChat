package com.yzucse.android.firebasechat;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    private GlobalData globalData;
    private Button button_setcan;
    private ProgressBar mProgressBar;
    private SetCanMessage setCanMessage;

    public SettingFragment() {
        // Required empty public constructor
    }

    public GlobalData getGlobalData() {
        return globalData;
    }

    public void setGlobalData(GlobalData globalData) {
        this.globalData = new GlobalData(globalData);
    }

    public void changefragment(Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentlayout, fragment)
               // .addToBackStack(null)
                .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);
        button_setcan = view.findViewById(R.id.bt_setting_can);
        mProgressBar = view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        button_setcan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCanMessage = new SetCanMessage();
                setCanMessage.setGlobalData(globalData);
                StaticValue.setViewVisibility(getActivity().findViewById(R.id.mainLayout),View.INVISIBLE);
                StaticValue.setViewVisibility(getActivity().findViewById(R.id.fragmentlayout), View.VISIBLE);
                changefragment(setCanMessage);
            }
        });
        return view;
    }

}
