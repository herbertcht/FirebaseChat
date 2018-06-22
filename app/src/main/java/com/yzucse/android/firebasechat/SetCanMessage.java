package com.yzucse.android.firebasechat;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetCanMessage extends Fragment {
    private SharedPreferences mSharedPreferences;
    private GlobalData globalData;
    private User muser;
    private EditText editText_can;
    private ToggleButton toggleButton;
    public SetCanMessage() {
        // Required empty public constructor
    }

    public void setGlobalData(GlobalData globalData) {
        this.globalData = new GlobalData(globalData);
    }

    public GlobalData getGlobalData() {
        return this.globalData;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_set_canmessage, container, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editText_can=view.findViewById(R.id.et_canmessage);
        toggleButton=view.findViewById(R.id.tb_canmessage);
        StaticValue.setViewVisibility(getActivity().findViewById(R.id.fragmentlayout), View.VISIBLE);
        editText_can.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(StaticValue.FRIENDLY_MSG_LENGTH, StaticValue.DEFAULT_MSG_LENGTH_LIMIT))});
        CheckCan();
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                globalData.getmUsersDBR().child(globalData.getmUser()
                        .getUserID()).child(StaticValue.OPEN_CAN).setValue(isChecked);
            }
        });
        editText_can.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus==false) {
                    globalData.getmUsersDBR().child(globalData.getmUser()
                            .getUserID()).child(StaticValue.CAN_MESSAGE)
                            .setValue(editText_can.getText().toString());
                    globalData.getmUsersDBR().child(globalData.getmUser()
                            .getUserID()).child(StaticValue.OPEN_CAN).setValue(toggleButton.isChecked());
                }
            }
        });
        editText_can.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_BACK)) {
                   editText_can.clearFocus();
                   return true;
                }
                return false;
            }
        });

        return view;
    }

    public void CheckCan(){
        String canmessage=globalData.getmUser().getCanMessage();
        boolean open=globalData.getmUser().isOpenCan();
        if(canmessage==null)
        {
            toggleButton.setChecked(false);
            globalData.getmUsersDBR().child(globalData.getmUser()
                    .getUserID()).child(StaticValue.OPEN_CAN).setValue(false);
        }else{
            editText_can.setText(canmessage);
            toggleButton.setChecked(open);
        }
    }

}
