package com.research.deustotech.smartxa;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.*;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.appyvet.materialrangebar.RangeBar;
import com.rubengees.introduction.IntroductionBuilder;


public class SettingsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {


    private static final String ARG_TEXT = "arg_text";
    private static final String ARG_COLOR = "arg_color";

    private String mText;
    private int mColor;

    private View mContent;
    private TextView mTextView;

    View view;
    RangeBar rangeBar;
    TextView patientText;
    Button changeButton;
    SharedPreferences sp;
    Spinner repSpinner;
    LottieAnimationView checkView;

    public static android.support.v4.app.Fragment newInstance(String text, int color) {
        android.support.v4.app.Fragment frag = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putInt(ARG_COLOR, color);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        changeButton = (Button) view.findViewById(R.id.changeButton);
        changeButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // retrieve text and color from bundle or savedInstanceState
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            mText = args.getString(ARG_TEXT);
            mColor = args.getInt(ARG_COLOR);
        } else {
            mText = savedInstanceState.getString(ARG_TEXT);
            mColor = savedInstanceState.getInt(ARG_COLOR);
        }

        // initialize views
        mContent = view.findViewById(R.id.fragment_content);
        mTextView = (TextView) view.findViewById(R.id.text);

        // set text and background color
        mTextView.setText(mText);
        mContent.setBackgroundColor(mColor);


        repSpinner = (Spinner) view.findViewById(R.id.repSpinner);
        rangeBar = (RangeBar) view.findViewById(R.id.rangebar);
        patientText = (TextView) view.findViewById(R.id.patientText);

        sp = this.getActivity().getSharedPreferences( "Smartxa_Preferences", Context.MODE_PRIVATE);

        rangeBar.setRangePinsByValue(0,sp.getInt("max_range", 100));
        repSpinner.setSelection(sp.getInt("repSpinnerId", 0));

        checkView = (LottieAnimationView)view.findViewById(R.id.animation_view);

        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

                System.out.println(rangeBar.getRightIndex());

                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("max_range",rangeBar.getRightIndex());
                editor.apply();
                editor.commit();


            }

        });



        // TODO: Remove after testing
        //SharedPreferences.Editor editor = sp.edit();
        //editor.putString("patient_name","Antonio Gonzalez");
        //editor.apply();
        //editor.commit();

        patientText.setText(sp.getString("patient_name", "Name"));



    }


    @Override
    public void onPause()
    {
        super.onPause();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_TEXT, mText);
        outState.putInt(ARG_COLOR, mColor);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onStop()
    {
        super.onStop();
        System.out.println("settings page stopped and hidden. " + sp.getString("repetitions", "5"));
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("repetitions", repSpinner.getSelectedItem().toString());
        editor.putInt("repSpinnerId", repSpinner.getSelectedItemPosition());
        editor.apply();
        editor.commit();
    }


    @Override
    public void onClick(View v)
    {
        System.out.println("Changing the patient");

        //Toast.makeText(this.getActivity().getApplicationContext(),"Patient changed", Toast.LENGTH_LONG);

        if(!patientText.getText().toString().equals(sp.getString("patient_name","Name")))
        {
            System.out.println("Changing the patient");
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("patient_name",patientText.getText().toString());
            editor.apply();
            editor.commit();

            System.out.println(sp.getString("patient_name", "Name"));
            //Toast.makeText(this.getActivity().getApplicationContext(),"Patient changed", Toast.LENGTH_LONG);

            checkView.playAnimation();
        }

    }

}
