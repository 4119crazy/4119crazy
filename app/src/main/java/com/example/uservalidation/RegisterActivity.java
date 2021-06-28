package com.example.uservalidation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.adpter.data.Country;
import com.adpter.data.CountryPickerFragment;
import com.adpter.data.JsonFileUtil;
import com.adpter.data.Language;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.phone.info.BugReportDeviceInfoBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class RegisterActivity extends FragmentActivity implements View.OnClickListener {
    EditText tel,ver_code;
    ImageView login;
    Boolean style;
    private ImageView ivFlag;
    private TextView tvName;
    private TextView tvCode;
    public Country country;
    public TextView verbutton;
    static String result;
    JSONObject object;
    SharedPreferences preferences;
    ImageView ver_img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        tel = findViewById(R.id.tel);
        verbutton=findViewById(R.id.get_code);
        login = findViewById(R.id.login);
        ivFlag = findViewById(R.id.iv_flag);
        tvName = findViewById(R.id.tv_name);
        tvCode = findViewById(R.id.tv_code);
        ver_code=findViewById(R.id.ver_code);
        ver_img= findViewById(R.id.ver_img);
        tel.setFocusable(false);
        setArrowSize(80);
        setVerSize(70);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        login.setOnClickListener(this);
        verbutton.setOnClickListener(this);
        tel.setOnClickListener(this);
        ivFlag.setOnClickListener(this);
        tvCode.setOnClickListener(this);

        JsonFileUtil.getJson().get_uuid();
        JsonFileUtil.getJson().get_id(getApplicationContext());
        tel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }
                else{
                    style=isPhoneNumberValid(tvCode.getText().toString()+tel.getText().toString(),tvName.getText().toString());
                    if(style){
                        Toast.makeText(getApplicationContext(),"Mobile phone format verification is correct",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"The mobile phone format is not correct",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        try {
            Country.load(this, Language.ENGLISH);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Properties properties = new Properties();
        BugReportDeviceInfoBuilder deviceInfoBuilder = new BugReportDeviceInfoBuilder(this);
        deviceInfoBuilder.setFileName("dm-" + Build.DEVICE + ".txt");
        deviceInfoBuilder.build();
        File mm = deviceInfoBuilder.getFile();
        preferences=getSharedPreferences("uuid",Context.MODE_PRIVATE);
        String uuid=preferences.getString("uuid",null);
        try {
            properties.load(new FileInputStream(mm));
            object = new JSONObject();
            for (Object p : properties.keySet()) {
                properties.get(p);
                object.put(p.toString().replace(".", "_"), properties.get(p));
            }
            object.put("aid",JsonFileUtil.getJson().get_uuid());
            object.put("UUID",uuid);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ss = JsonFileUtil.getJson().postDownloadJson("http://192.168.0.60:8989/report/info", object);
                }
            }).start();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }
    public void setArrowSize(int i2) {
        if (i2 > 0) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.ivFlag.getLayoutParams();
            layoutParams.width = i2;
            layoutParams.height = i2;
            ivFlag.setLayoutParams(layoutParams);
        }
    }
    public void setVerSize(int i2) {
        if (i2 > 0) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.ver_img.getLayoutParams();
            layoutParams.width = i2;
            layoutParams.height = i2;
            ver_img.setLayoutParams(layoutParams);
        }
    }

    public void showResult() {
        style=isPhoneNumberValid(tvCode.getText().toString()+tel.getText().toString(),tvName.getText().toString());
        if (tel.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "The phone number cannot be blank", Toast.LENGTH_LONG).show();
            return;
        }
        else if(ver_code.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "The verification code cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }
        if(ver_code.getText().toString().equals(result)){
            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
            return;
        }
        else{
            Toast.makeText(getApplicationContext(), "Logon failure, error verification code", Toast.LENGTH_LONG).show();
            return;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111 && resultCode == Activity.RESULT_OK) {
            country = Country.fromJson(data.getStringExtra("country"));
            assert country != null;
            if(country.flag != 0) ivFlag.setImageResource(country.flag);
            tvName.setText(country.name);
            tvCode.setText("+" + country.code);
            ivFlag.setImageResource(country.flag);
        }
    }
    @Override
    protected void onDestroy() {
        Country.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static boolean isPhoneNumberValid(String phoneNumber, String countryCode) {

        System.out.println("isPhoneNumberValid: " + phoneNumber + "/" + countryCode);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryCode);
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            System.err.println("isPhoneNumberValid NumberParseException was thrown: " + e.toString());
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login: {

                JSONObject postverfile = JsonFileUtil.getJson().toverParseJsonFile(tvCode.getText().toString(), result, tel.getText().toString());
                String postmsg = JsonFileUtil.getJson().postDownloadJson("http://192.168.0.60:8989/sms/verifyCode", postverfile);
                Toast.makeText(getApplicationContext(), "Your verification code is:" + result, Toast.LENGTH_LONG).show();
                showResult();
                break;
            }

            case R.id.get_code:
                JSONObject jsonfile = JsonFileUtil.getJson().toParseJsonFile(tvName.getText().toString(), tvCode.getText().toString(), tel.getText().toString());
                String sendmsg = JsonFileUtil.getJson().postDownloadJson("http://192.168.0.60:8989/sms/send", jsonfile);
                result = sendmsg;
                break;

            case R.id.tel:
                Toast.makeText(getApplicationContext(), "Please select the country area", Toast.LENGTH_LONG).show();
                break;

            case R.id.iv_flag:

                CountryPickerFragment.newInstance(country -> {
                    if (country.flag != 0) ivFlag.setImageResource(country.flag);
                    tvName.setText(country.name);
                    tvCode.setText("+" + country.code);
                    tel.setFocusableInTouchMode(true);
                    tvCode.setVisibility(View.VISIBLE);
                }).show(getSupportFragmentManager(), "country");
                break;
            case R.id.tv_code:
                CountryPickerFragment.newInstance(country -> {
                    if (country.flag != 0) ivFlag.setImageResource(country.flag);
                    tvName.setText(country.name);
                    tvCode.setText("+" + country.code);
                    tel.setFocusableInTouchMode(true);
                }).show(getSupportFragmentManager(), "country");
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

}
