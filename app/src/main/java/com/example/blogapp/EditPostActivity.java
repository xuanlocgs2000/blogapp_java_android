package com.example.blogapp;

//import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.Table.map;

import static com.example.blogapp.Constant.UPDATE_POST;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.Fragments.HomeFragment;
import com.example.blogapp.Model.Post;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {
    private int position = 0 , id=0;
    private EditText txtDesc;
    private Button btnSave;
    private SharedPreferences userPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        initView();
    }

    private void initView() {
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        Log.d("EditPostActivity", "Response: " + userPref);

        txtDesc = findViewById(R.id.txtDescEditPost);
        btnSave = findViewById(R.id.btnEditPost);
        position = getIntent().getIntExtra("position",0);
        id = getIntent().getIntExtra("postId",0);
        txtDesc.setText(getIntent().getStringExtra("text"));
        btnSave.setOnClickListener(v->{
            if(!txtDesc.getText().toString().isEmpty()){
                savePost();
            }
        });

    }

    private void savePost() {
String url = UPDATE_POST+id;
        StringRequest request = new StringRequest(Request.Method.PUT, url, response -> {
            Log.d("EditPostActivity", "Response: " + response);

            try{
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    Post post = HomeFragment.arrayList.get(position);
                    post.setDesc(txtDesc.getText().toString());

                    HomeFragment.arrayList.set(position,post);
                    HomeFragment.recyclerView.getAdapter().notifyItemChanged(position);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    finish();
                }

            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }, error -> {}){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
//                map.put("id",id+"");
                map.put("body", txtDesc.getText().toString());
                return  map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(EditPostActivity.this);
        queue.add(request);

    }

    public void cancelEdit(View view){
        finish();

    }
}