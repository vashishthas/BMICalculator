package com.example.bmicalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.text.MessageFormat;

public class MainActivity extends AppCompatActivity {

    private static final String MESSAGE_ID="message_prefs";
    private EditText weight;
    private EditText height;
    private Button calculateButton;
    private Button heightButton,weightButton;
    private TextView res,idealWeight;
    private MaterialButton maleButton;
    private MaterialButton femaleButton;
    private TextView message;

    private float idw;
    private String editHeight;
    private String editWeight;
    private float ht;
    private boolean heightFlag=true;
    private boolean weightFlag=true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES)
        {
            setTheme(R.style.DarkTheme);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else
        {
            setTheme(R.style.Theme_BMICalculator);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences getSharedPrefs=getSharedPreferences(MESSAGE_ID,MODE_PRIVATE);
        String value=getSharedPrefs.getString("Mode","nothing");
        if(value.equals("Dark"))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            setTheme(R.style.DarkTheme);
        }


        maleButton=findViewById(R.id.male_button);
        femaleButton=findViewById(R.id.female_button);


        weight=findViewById(R.id.weight);
        height=findViewById(R.id.height);
        calculateButton=findViewById(R.id.calculate);

        res=findViewById(R.id.bmi);

        message=findViewById(R.id.message_text);


        idealWeight=findViewById(R.id.ideal_weight);

        heightButton=findViewById(R.id.height_button);
        weightButton=findViewById(R.id.weight_button);

        maleButton.setChecked(true);

        heightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(heightFlag)
                {
                    heightButton.setText(R.string.inches_text);
                    heightFlag=false;
                }
                else
                {
                    heightButton.setText(R.string.cm_text);
                    heightFlag=true;
                }
            }
        });
        weightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(weightFlag)
                {
                    weightButton.setText(R.string.lbs_text);
                    weightFlag=false;              //wt is false when in lbs
                }
                else
                {
                    weightButton.setText(R.string.kg_text);
                    weightFlag=true;
                }
            }
        });
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                editHeight=height.getText().toString();
                editWeight=weight.getText().toString();
                if(editHeight.equals(""))
                {
                    height.setError("Enter Height");
                    height.requestFocus();
                    return;
                }
                if(editWeight.equals(""))
                {
                    weight.setError("Enter Weight");
                    weight.requestFocus();
                    return;
                }
                calculation();
            }
        });
    }

    private void calculation() {
        ht = Float.parseFloat(editHeight);
        if (!heightFlag)
            ht *= 0.0254;  //inches to meters
        else
            ht /= 100; //converting cm to meters
        float wt = Float.parseFloat(editWeight);
        if (!weightFlag)
            wt *= 0.453592; //lbs to kg
        float result = wt / (ht * ht);
        if (result < 16) {
            //severe
            message.setText(R.string.msg1);
            message.setTextColor(getResources().getColor(R.color.red));
        } else if (result < 18.5) {
            //under wt
            message.setText(R.string.msg2);
            message.setTextColor(getResources().getColor(R.color.orange));
        } else if (result < 25) {
            //ideal
            message.setText(R.string.msg3);
            message.setTextColor(getResources().getColor(R.color.green));
        } else if (result < 30) {
            //over
            message.setText(R.string.msg4);
            message.setTextColor(getResources().getColor(R.color.orange));
        } else {
            //Obese
            message.setText(R.string.msg5);
            message.setTextColor(getResources().getColor(R.color.red));
        }
        String ans;
        ans = String.format("%.2f", result);
        res.setText(ans);
//                    idw= (float) (50+((0.91*ht*100)-152.4)); ALTERNATE FORMULA
        ht*=100;
        idealWeightCalculation();
    }

    private void idealWeightCalculation() {
        if(maleButton.isChecked())
        {
            idw= (float) ((ht-100)-(ht-150)/4);
        }
        else
            if(femaleButton.isChecked())
            {
                idw = (float) ((ht - 100) - (ht - 150) / 2);
            }
        if(idw<5)
        {
            idealWeight.setText(R.string.idealWeightError);
        }
        else {
            String idealW;
            if(weightButton.getText().toString().equals("lbs"))
            {
                idw*=2.20462;
                idealW = String.format("%s lbs", String.format("%.1f", idw));
            }
            else {
                idealW = String.format("%s Kg", String.format("%.1f", idw));
            }
            idealWeight.setText(MessageFormat.format("Ideal Weight: {0}", idealW));
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.reset:
                resetAll();
                break;
            case R.id.chart:

                break;
            case R.id.darkMode:
                if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    setTheme(R.style.Theme_BMICalculator);
                    sharedPrefs("Light");
                }
                else
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    setTheme(R.style.DarkTheme);
                    sharedPrefs("Dark");
                }
                break;
        }
        return true;
//        return super.onOptionsItemSelected(item);
    }

    private void sharedPrefs(String msg) {
        SharedPreferences sharedPreferences=getSharedPreferences(MESSAGE_ID,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("Mode",msg);

        editor.apply();
    }

    private void resetAll() {
        height.setText("");
        weight.setText("");
        res.setText("");
        idealWeight.setText("");
        message.setText("");
    }
}