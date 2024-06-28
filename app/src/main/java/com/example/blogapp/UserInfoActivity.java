package com.example.blogapp;

import static android.app.PendingIntent.getActivity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {
    private Bitmap bitmap;
    private SharedPreferences userPref;
    private static final int GALLERY_ADD_PROFILE = 1;
    private TextInputLayout layoutName;
    private TextInputEditText txtName;
    private TextView txtSelectPhoto;
    private Button btnContinue;
    private CircleImageView circleImageView;
    private ActivityResultLauncher<String> galleryLauncher;
    private Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();


    }

    private void initView() {
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        layoutName = findViewById(R.id.txtLayoutUserName);
        txtName = findViewById(R.id.txtUserName);
        txtSelectPhoto = findViewById(R.id.txtSelectPhoto);
        btnContinue = findViewById(R.id.btnContinue);
        circleImageView = findViewById(R.id.imgAvatar);
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String oldName = userPref.getString("name", ""); // Đây là giả sử bạn lưu name cũ với key "oldName" trong SharedPreferences
        txtName.setText(oldName);
        String imgUrl = getIntent().getStringExtra("imgUrl");
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Picasso.get().load(imgUrl).into(circleImageView);
        }

        // Khởi tạo ActivityResultLauncher
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        // Xử lý ảnh được chọn từ Gallery
                        handleSelectedImage(result);
                    }
                });

        // Bắt sự kiện khi nhấn nút chọn ảnh
        txtSelectPhoto.setOnClickListener(v -> {
            galleryLauncher.launch("image/*");
        });

        // Bắt sự kiện khi nhấn nút Tiếp tục
        btnContinue.setOnClickListener(v -> {
            // validate fields
            if (validate()) {
                saveUserInfo();
            }
        });
    }
    private void handleSelectedImage(Uri imgUri) {
        circleImageView.setImageURI(imgUri);

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean validate() {
        if (txtName.getText().toString().isEmpty()) {
            layoutName.setErrorEnabled(true);
            layoutName.setError("Name is Required");
            return false;
        }

        return true;
    }
    private String getUserNameFromResponse(String response) {
        try {
            JSONObject object = new JSONObject(response);
            if (object.getBoolean("success")) {
                JSONObject user = object.getJSONObject("user");
                return user.getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    private void getOldName() {
        // Lấy name cũ từ SharedPreferences
        String oldName = userPref.getString("name", ""); // Đây là giả sử bạn lưu name cũ với key "oldName" trong SharedPreferences
        txtName.setText(oldName);

    }
    private void saveUserInfo() {
        progressDialog.show();
//        if (bitmap == null) {
//            Log.d("sc", "saveUserInfo:  null success");
//
//            // Handle the case where bitmap is null (e.g., user hasn't selected an image)
//            // You may want to show a message to the user or handle this case based on your requirements.
//            return;
//        }
        String imageBase64 = bitmapToString(bitmap);

        String name = txtName.getText().toString().trim();
//        Log.d("sc", imageBase64 );

//        StringRequest request = new StringRequest(Request.Method.PUT, Constant.SAVE_USER_INFO,  new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject object = new JSONObject(response);
//                    JSONObject user = object.getJSONObject("user");
//
//                    if (object.getBoolean("success")) {
////
//                        SharedPreferences.Editor editor = userPref.edit();
//                        editor.putString("image", user.getString("image"));
//                        editor.putString("name", name);
//                        editor.apply();
//                        Log.d(TAG, "Before startActivity");
//                        startActivity(new Intent(UserInfoActivity.this, HomeActivity.class));
//                        finish();
//                        Log.d(TAG, "After startActivity");
////
//                            Toast.makeText(UserInfoActivity.this, "User update success", Toast.LENGTH_SHORT).show();
//
////
////
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                progressDialog.dismiss();
//            }
//        },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        progressDialog.dismiss();
//                        Toast.makeText(UserInfoActivity.this, "User update failed", Toast.LENGTH_SHORT).show();
//                    }
//                }) {
        StringRequest request = new StringRequest(Request.Method.PUT,Constant.SAVE_USER_INFO,response->{

            try {
                JSONObject object = new JSONObject(response);
                JSONObject user = object.getJSONObject("user");
                if (object.getBoolean("success")){

                    SharedPreferences.Editor editor = userPref.edit();
                        editor.putString("image", user.getString("image"));
                        editor.putString("name", name);
                        editor.apply();
                        Log.d(TAG, "Before startActivity");
                        startActivity(new Intent(UserInfoActivity.this, HomeActivity.class));
                        finish();
                        Log.d(TAG, "After startActivity");
//
                            Toast.makeText(UserInfoActivity.this, "User update success", Toast.LENGTH_SHORT).show();
//
////
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();

        },error ->{
            error.printStackTrace();
            progressDialog.dismiss();
        } ){
            //add token to headers

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            //add params

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", name);
//                map.put("photo", bitmapToString(bitmap));
                map.put("image", imageBase64);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(UserInfoActivity.this);
        queue.add(request);
    }


    private String bitmapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);
        }
        return "";
    }
}
