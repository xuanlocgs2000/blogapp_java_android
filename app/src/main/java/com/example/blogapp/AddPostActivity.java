package com.example.blogapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.Fragments.HomeFragment;
import com.example.blogapp.Model.Post;
import com.example.blogapp.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private Button btnPost;
    private ImageView imagePost;
    private TextView txtChangeImage;
    private EditText txtDesc;
    private Bitmap bitmap = null;
    private static final int GALLERY_CHANGE_POST =3;
    private ProgressBar progressBar;
    private TextView progressText;
    private RelativeLayout progressLayout;
    private Dialog progressDialog;


    private SharedPreferences userPref;
    ActivityResultLauncher<Intent> activityResultLauncher= registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    int result = o.getResultCode();
                    Intent data = o.getData();
                    if (result==RESULT_OK){
                        Uri imgUri = data.getData();
                        imagePost.setImageURI(imgUri);
                        try{
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getIntent().getData());

                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        init();
    }

    private void init() {
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        progressLayout = findViewById(R.id.progressLayout);
        progressBar = findViewById(R.id.progressBar);
        progressText =findViewById(R.id.progressText);
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        btnPost = findViewById(R.id.btnAddPost);
        imagePost = findViewById(R.id.imgAddPost);
        txtChangeImage = findViewById(R.id.changeImg);
        txtDesc = findViewById(R.id.txtDescAddPost);
        imagePost.setImageURI(getIntent().getData());
        try{
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getIntent().getData());

        }catch (IOException e){
            e.printStackTrace();
        }
        btnPost.setOnClickListener(v->{
            if (!txtDesc.getText().toString().isEmpty()){
                post();
            }
            else{
                Toast.makeText(this,"Emty desc", Toast.LENGTH_LONG).show();
            }
        });
        txtChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImagePost();
            }
        });
    }

    private void post() {
//        showProgressBar();
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.POSTS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONObject postObject = object.getJSONObject("post");
                    JSONObject userObject = postObject.getJSONObject("user");

                    User user = new User();
                    user.setId(userObject.getInt("id"));
                    user.setUserName(userObject.getString("name"));
                    user.setPhoto(userObject.getString("image"));

                    Post post = new Post();
                    post.setUser(user);
                    post.setId(postObject.getInt("id"));
                    post.setDesc(postObject.getString("body"));
                    post.setPhoto(postObject.getString("image"));
                    post.setComments(0);
                    post.setLikes(0);
                    post.setTime(postObject.getString("created_at"));
//
                    HomeFragment.arrayList.add(0,post);
                    HomeFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();

                    Toast.makeText(this,"Đăng bài thành công", Toast.LENGTH_SHORT).show();


                    finish();
//
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            progressDialog.dismiss();


        }, error->{
//add token header
            error.printStackTrace();
            progressDialog.dismiss();



        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;

            //add token header

            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("body",txtDesc.getText().toString().trim());
        map.put("image",bitmapToString(bitmap));
//                map.put("user_id", String.valueOf(userPref.getInt("id", 0)));
        return map;
    }
};
        RequestQueue queue = Volley.newRequestQueue(AddPostActivity.this);
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
    //himh anh
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
    public void cancelPost(){
        finish();
    }
    public void changeImagePost(){
        Intent i  = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,GALLERY_CHANGE_POST);
        activityResultLauncher.launch(i);


    }
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==GALLERY_CHANGE_POST&&resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            imagePost.setImageURI(imgUri);
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getIntent().getData());
                //imge

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}