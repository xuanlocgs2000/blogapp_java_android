package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.Adapters.CommentsAdapter;
import com.example.blogapp.Fragments.HomeFragment;
import com.example.blogapp.Model.Post;
import com.example.blogapp.Model.User;
import com.example.blogapp.Model.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Comment> list;
    private CommentsAdapter adapter;
    private int postId = 0;
    public  static  int postPosition = 0;
    private SharedPreferences preferences;
    private EditText txtAddComment;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        init();
    }

//    private void init() {
//        dialog = new ProgressDialog(this);
//        dialog.setCancelable(false);
//        postPosition = getIntent().getIntExtra("postPosition",-1);
//        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
//        recyclerView = findViewById(R.id.recyclerComments);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        txtAddComment = findViewById(R.id.txtAddComment);
//        postId = getIntent().getIntExtra("postId",0);
//        getComments();
//    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        postPosition = getIntent().getIntExtra("postPosition", -1);
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recyclerComments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        txtAddComment = findViewById(R.id.txtAddComment);
        postId = getIntent().getIntExtra("postId", 0);
        list =  list = new ArrayList<>(); // Khởi tạo danh sách rỗng
        adapter = new CommentsAdapter(CommentActivity.this, list); // Khởi tạo adapter
        recyclerView.setAdapter(adapter); // Gán adapter cho RecyclerView
        getComments(); // Lấy danh sách comment
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

String url = Constant.COMMENTS+postId;
    public static String formatApiTime(String apiTime) {
        try {
            // Đối tượng SimpleDateFormat để parse thời gian từ chuỗi
            SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());

            // Đối tượng SimpleDateFormat để format lại thời gian
            SimpleDateFormat targetDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

            // Parse thời gian từ chuỗi
            Date date = apiDateFormat.parse(apiTime);

            // Format lại thời gian và trả về
            return targetDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            // Xử lý nếu có lỗi parse
            return apiTime; // Trả về nguyên bản nếu không thể parse
        }
    }
    private void getComments() {
        dialog.setMessage("Loading comments");
        dialog.show();

        String url = Constant.COMMENTS + postId;
//        {{server}}/api/posts/comments/2
        StringRequest request = new StringRequest(Request.Method.GET, url, res -> {
            try {
                JSONObject response = new JSONObject(res);
                JSONArray commentsArray = response.getJSONArray("comments");

                list.clear(); // Xóa các phần tử cũ trong danh sách

                for (int i = 0; i < commentsArray.length(); i++) {
                    JSONObject commentObject = commentsArray.getJSONObject(i);
                    JSONObject userObject = commentObject.getJSONObject("user");

                    User user = new User();
                    user.setId(userObject.getInt("id"));
                    user.setUserName(userObject.getString("name"));
                    user.setPhoto(userObject.isNull("image") ? null : userObject.getString("image"));

                    Comment comment = new Comment();
                    comment.setId(commentObject.getInt("id"));
                    comment.setComment(commentObject.getString("comment"));
                    comment.setDate(formatApiTime(commentObject.getString("created_at")));
                    comment.setUser(user);

                    list.add(comment); // Thêm comment vào danh sách
                }

                adapter.notifyDataSetChanged(); // Thông báo cho adapter cập nhật dữ liệu mới

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(CommentActivity.this, "Failed to load comments.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }, err -> {
            err.printStackTrace();
            dialog.dismiss();
            Toast.makeText(CommentActivity.this, "Error occurred while fetching comments.", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }




    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CommentActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addComment(View view) {
        String commentText = txtAddComment.getText().toString().trim(); // Trim để loại bỏ khoảng trắng thừa
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.setMessage("Adding comment");
        dialog.show();

        String url = Constant.COMMENTS + postId;
        StringRequest request = new StringRequest(Request.Method.POST, url, res -> {
            try {
                JSONObject object = new JSONObject(res);
                if (object.getBoolean("success")) {
                    txtAddComment.setText("");
                    Toast.makeText(this, "Comment bài viết  thành công", Toast.LENGTH_SHORT).show();
                    JSONObject commentJson = object.getJSONObject("comments");
                    JSONObject userJson = commentJson.getJSONObject("user");

                    User user = new User();
                    user.setId(userJson.getInt("id"));
                    user.setUserName(userJson.getString("name"));
                    user.setPhoto(userJson.isNull("image") ? null : userJson.getString("image"));

                    Comment comment = new Comment();
                    comment.setId(commentJson.getInt("id"));
                    comment.setComment(commentJson.getString("comment"));
                    comment.setDate(formatApiTime(commentJson.getString("created_at")));
                    comment.setUser(user);

                    // Thêm comment mới vào danh sách và cập nhật adapter
                    list.add(comment);
                    adapter.notifyDataSetChanged();

                    // Cập nhật số lượng comment trong Post
                    Post post = HomeFragment.arrayList.get(postPosition);
                    post.setComments(post.getComments() + 1);
                    HomeFragment.arrayList.set(postPosition, post);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();

                    // Xóa nội dung ô nhập comment sau khi thêm thành công
                    txtAddComment.setText("");

                } else {
                    Toast.makeText(this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }, err -> {
            err.printStackTrace();
            dialog.dismiss();
            Toast.makeText(this, "Error occurred while adding comment", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("comment", commentText);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


}
