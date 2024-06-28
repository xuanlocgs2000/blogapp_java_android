package com.example.blogapp.Fragments;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.blogapp.UserInfoActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {
    private View view;
    private TextInputLayout layoutEmail,layoutPassword, layoutConfirm, layoutName;

    private TextInputEditText txtName,txtEmail, txtPassword,txtConfirm;
    private TextView txtSignIn;
    private Button btnSignUp;
    private RelativeLayout progressLayout;
    private ProgressBar progressBar;
    private TextView progressText;

    public SignUpFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sign_up,container,false);
        init();
        return  view;
    }

    private void init() {
        layoutName = view.findViewById(R.id.txtLayoutNameSignUp);
        layoutEmail = view.findViewById(R.id.txtLayoutEmailSignUp);
        layoutPassword = view.findViewById(R.id.txtLayoutPasswordSignUp);
        txtName = view.findViewById(R.id.txtNameSignUp);
        txtEmail = view.findViewById(R.id.txtEmailSignUp);
        txtPassword = view.findViewById(R.id.txtPasswordSignUp);
        layoutConfirm = view.findViewById(R.id.txtLayoutPasswordSignUpConfirm);
        txtConfirm = view.findViewById(R.id.txtPasswordSignUpConfirm);
        txtSignIn = view.findViewById(R.id.txtSignIn);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        progressLayout = view.findViewById(R.id.progressLayout);
        progressBar = view.findViewById(R.id.progressBar);
        progressText = view.findViewById(R.id.progressText);
        String email = txtEmail.getText().toString().trim();
        //set data form
        txtName.setText("huy17");
        txtEmail.setText("huy17@gmail.com");
        txtPassword.setText("12345678");
        txtConfirm.setText("12345678");

        txtSignIn.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SignInFragment()).commit();
        });
        btnSignUp.setOnClickListener(v->{
            if(validate()){
                showProgressBar();
                register();
                hideKeyboard();

            }
        });


        // ẩn thông báo khi validate đúng
        //name
        txtName.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!txtName.getText().toString().isEmpty()){
                    layoutName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //email&pass
        txtEmail.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!txtEmail.getText().toString().isEmpty() && email.endsWith("@gmail.com") ){
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
        txtConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtConfirm.getText().toString().equals(txtPassword.getText().toString())){
                    layoutConfirm.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
    private void register() {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.REGISTER, response -> {
            Log.d("RegistrationResponse", response);
            try{
                JSONObject object = new JSONObject(response);

//                if(object.getBoolean("success")){
//                    Log.d("RegistrationSuccess", "Đăng ký thành công");
//                    JSONObject  user = object.getJSONObject("user");
//                    //share preferrence user
//                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = userPref.edit();
//                    editor.putString("token",object.getString("token"));
//                    editor.putString("name",user.getString("name"));
//                    editor.putString("email",user.getString("email"));
//                    editor.putString("image",user.getString("image"));
//                    editor.apply();
//                    Toast.makeText(getActivity(),"Đăng kí thành công",Toast.LENGTH_LONG).show();
//                    hideProgressBar();
//                    startActivity(new Intent((AuthActivity)getContext(), UserInfoActivity.class));
//                    ((AuthActivity) getContext()).finish();
//                }
                if (object.getBoolean("success")){
                    JSONObject user = object.getJSONObject("user");
                    //make shared preference user
                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user",getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("token",object.getString("token"));
                    editor.putString("name",user.getString("name"));
                    editor.putInt("id",user.getInt("id"));
//                    editor.putString("lastname",user.getString("lastname"));
                    editor.putString("image",user.getString("image"));
//                    editor.putString("image","");
                    editor.putBoolean("isLoggedIn",true);
                    editor.apply();
                    //if success
                    startActivity(new Intent(((AuthActivity)getContext()), UserInfoActivity.class));
                    ((AuthActivity) getContext()).finish();
                    Toast.makeText(getContext(), "Register Success", Toast.LENGTH_SHORT).show();
                    hideProgressBar();

                }


            }catch(Exception e){
                e.printStackTrace();

                hideProgressBar();

            }
        },error -> {
            error.printStackTrace();
            hideProgressBar(); //1245
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("email",txtEmail.getText().toString().trim());
                map.put("password",txtPassword.getText().toString());
                map.put("name",txtName.getText().toString());
                map.put("image","https://ibb.co/RH2wfQh");
                map.put("password_confirmation",txtConfirm.getText().toString());
//                map.put("image","");
//
                return map;
            }
        };
        //add request to resquest queue
        RequestQueue queue= Volley.newRequestQueue(getContext());
        queue.add(request);

    }



    private boolean validate() {

        //validate input
        if(txtName.getText().toString().isEmpty()){
            layoutName.setErrorEnabled(true);
            layoutName.setError("Bạn cần nhập tên!");
            return false;
        }
        if(txtEmail.getText().toString().isEmpty()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Bạn cần nhập email!");
            return false;
        }
        String email = txtEmail.getText().toString().trim();

        if (!email.endsWith("@gmail.com")) {
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Email phải là địa chỉ Gmail!");
            return false;
        } else {
            layoutEmail.setErrorEnabled(false);
        }

//// Nếu đến đây, email hợp lệ
//        return true;
        if(txtPassword.getText().toString().length()<8){
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("Mật khẩu có ít nhất 8 kí tự!");
            return false;
        }
        if(!txtConfirm.getText().toString().equals(txtPassword.getText().toString())){
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("Mật khẩu xác nhận chưa chính xác!");
            return false;
        }
        return true;
    }

}
