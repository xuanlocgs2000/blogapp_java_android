package com.example.blogapp.Fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.Adapters.PostAdapter;
import com.example.blogapp.Constant;
import com.example.blogapp.HomeActivity;
import com.example.blogapp.Model.Post;
import com.example.blogapp.Model.User;
import com.example.blogapp.R;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static  ArrayList<Post> arrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private  PostAdapter postAdapter;
    private MaterialToolbar toolbar;
    private SharedPreferences userPref;
    public HomeFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.layout_home, container, false);
            init();
            return  view;

    }

    private void init() {
//        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        userPref = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recycleHome);
        recyclerView.setHasFixedSize(true);//toi uu
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout = view.findViewById(R.id.swipeHome);
        toolbar = view.findViewById(R.id.toolbarHome);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);
        getPosts();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();
            }
        });

    }
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
    private void getPosts() {
        arrayList = new ArrayList<>();
        swipeRefreshLayout.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.POSTS,response -> {
            try{
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("posts"));
                    for(int i = 0; i < array.length(); i++){
                        JSONObject postObject = array.getJSONObject(i);
                        JSONObject userObject = postObject.getJSONObject("user");
                        User user = new User();
                        user.setId(userObject.getInt("id"));
                        user.setUserName(userObject.getString("name"));
                        user.setPhoto(userObject.getString("image"));
                        Log.d(TAG,"link anh: "+userObject.getString("image"));
                        Post post = new Post();
                        post.setId(postObject.getInt("id"));
                        post.setUser(user);
                        post.setLikes(postObject.getInt("likes_count"));
                        post.setComments(postObject.getInt("comments_count"));
                        post.setDesc(postObject.getString("body"));
                        post.setPhoto(postObject.getString("image"));
                        post.setTime( formatApiTime(postObject.getString("created_at")));

                        arrayList.add(post);



                    }
                    postAdapter  = new PostAdapter(getContext(),arrayList);
                    recyclerView.setAdapter(postAdapter);

                }
//                swipeRefreshLayout.setRefreshing(false);

            }
            catch(Exception e){
                e.printStackTrace();

            }
            swipeRefreshLayout.setRefreshing(false);

        } ,error -> {
            error.printStackTrace();
            swipeRefreshLayout.setRefreshing(false);

        }
        ){

@Override
public Map<String, String> getHeaders() throws AuthFailureError {
    String token = userPref.getString("token", "");
    HashMap<String, String> map = new HashMap<>();
    map.put("Authorization", "Bearer " + token);
    return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}