package com.example.blogapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.AuthActivity;
import com.example.blogapp.Constant;
import com.example.blogapp.HomeActivity;
import com.example.blogapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignInFragment extends Fragment {
    private View view;

    private TextInputLayout layoutEmail,layoutPassword;
    private TextInputEditText txtEmail, txtPassword;
    private TextView txtSignUp;
    private Button btnSignIn,btnTest;
    private RelativeLayout progressLayout;
    private ProgressBar progressBar;
    private TextView progressText;
    public SignInFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sign_in,container,false);
        init();
        return  view;
    }

    private void init() {
        layoutEmail = view.findViewById(R.id.txtLayoutEmailSignIn);
        layoutPassword = view.findViewById(R.id.txtLayoutPasswordSignIn);
        txtEmail = view.findViewById(R.id.txtEmailSignIn);
        txtPassword = view.findViewById(R.id.txtPasswordSignIn);
        txtSignUp = view.findViewById(R.id.txtSignUp);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        progressLayout = view.findViewById(R.id.progressLayout);
        progressBar = view.findViewById(R.id.progressBar);
        progressText = view.findViewById(R.id.progressText);
//        btnTest =view.findViewById(R.id.btnTest);
        txtSignUp.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SignUpFragment()).commit();
        });
        btnSignIn.setOnClickListener(v->{
            if(validate()){
                showProgressBar();
                login();
                hideKeyboard();

            }
        });
        txtEmail.setText("huy17@gmail.com");
        txtPassword.setText("12345678");
//        btnTest.setOnClickListener(v->{
//
//                Toast.makeText(getContext(),"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
//
//
//
//        });
        // ẩn thông báo khi validate đúng
        txtEmail.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!txtEmail.getText().toString().isEmpty()){
                    layoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtPassword.getText().toString().length()>7){
                    layoutPassword.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validate() {
        //validate input
        if(txtEmail.getText().toString().isEmpty()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Bạn cần nhập email!");
            return false;
        }
        if(txtPassword.getText().toString().length()<6){
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("Mật khẩu có ít nhất 8 kí tự!");
            return false;
        }
        return true;
    }
    private void showProgressBar() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        progressText.setVisibility(View.GONE);
    }
    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void login(){
        StringRequest request = new StringRequest(Request.Method.POST, Constant.LOGIN, response -> {
            try{
                JSONObject object = new JSONObject(response);

                   if(object.getBoolean("success")){
                       JSONObject  user = object.getJSONObject("user");
                       //share preferrence user
                       SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                       SharedPreferences.Editor editor = userPref.edit();
                       editor.putString("token",object.getString("token"));
                       editor.putString("name",user.getString("name"));
                       editor.putInt("id",user.getInt("id"));
                       editor.putString("image",user.getString("image"));
                       editor.putBoolean("isLoggedIn",true);
                       editor.apply();
                       Toast.makeText(getContext(),"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                       hideProgressBar();
                       startActivity(new Intent((AuthActivity)getContext(), HomeActivity.class));
                       ((AuthActivity) getContext()).finish();

                   }



            }catch(Exception e){
                hideProgressBar();



            }
        },error -> {
            error.printStackTrace();
            Toast.makeText(getContext(),"Tài khoản và mật khẩu chưa chính xác",Toast.LENGTH_SHORT).show();

            hideProgressBar(); //

        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("email",txtEmail.getText().toString().trim());
                map.put("password",txtPassword.getText().toString());
                return map;
            }
        };
        //add request to resquest queue
        RequestQueue queue= Volley.newRequestQueue(getContext());
        queue.add(request);

    }
}
