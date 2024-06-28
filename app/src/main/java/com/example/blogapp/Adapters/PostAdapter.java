package com.example.blogapp.Adapters;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.CommentActivity;
import com.example.blogapp.Constant;
import com.example.blogapp.EditPostActivity;
import com.example.blogapp.HomeActivity;
import com.example.blogapp.Model.Post;
import com.example.blogapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    private Context context;
    private ArrayList<Post> list;
    private ArrayList<Post> listAll;
    private SharedPreferences preferences;

    public PostAdapter(Context context, ArrayList<Post> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post, parent, false);
        return new PostHolder(view);//khoi tao view holder moi
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        Post post = list.get(position);
        Log.d(TAG, "Adapter" + Constant.URL + "storage/profiles/" + post.getPhoto());
        Log.d(TAG, "response obj: " + post.isSelfLike());

        Picasso.get().load(post.getUser().getPhoto()).into(holder.imgProfile);
        Picasso.get().load(post.getPhoto()).into(holder.imgPost);
        holder.txtName.setText(post.getUser().getUserName());
        holder.txtComments.setText("Xem tất cả " + post.getComments() + " bình luận");
        holder.txtLikes.setText(post.getLikes() + " likes");
        holder.txtTime.setText(post.getTime());
        holder.txtDesc.setText(post.getDesc());
        holder.btnLike.setImageResource(
                post.isSelfLike() ? R.drawable.ic_favorite_24 : R.drawable.ic_fav_outline
        );
        // like click
        holder.btnLike.setOnClickListener(v->{
            holder.btnLike.setImageResource(
                    post.isSelfLike()?R.drawable.ic_fav_outline:R.drawable.ic_favorite_24
            );
            String url = Constant.LIKE_POST +  post.getId();
//            Toast.makeText(context, "URL: " + url, Toast.LENGTH_SHORT).show();
            StringRequest request = new StringRequest(Request.Method.POST,url,response -> {

                Post mPost = list.get(position);

                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("success")){
                        mPost.setSelfLike(!post.isSelfLike());
                        mPost.setLikes(mPost.isSelfLike()?post.getLikes()+1:post.getLikes()-1);
                        list.set(position,mPost);
                        notifyItemChanged(position);
                        notifyDataSetChanged();
                    }
                    else {
                        holder.btnLike.setImageResource(
                                post.isSelfLike()?R.drawable.ic_favorite_24:R.drawable.ic_fav_outline
                        );
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            },err->{
                err.printStackTrace();
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String token = preferences.getString("token", "");
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Authorization", "Bearer " + token);
                    return map;
                }
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("post_id",post.getId()+"");
                    return  map;

                }
            };
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);
        });

        if (post.getUser().getId() == preferences.getInt("id", 0)) {
            holder.btnPostOptions.setVisibility(View.VISIBLE);
        } else {
            holder.btnPostOptions.setVisibility(View.GONE);
        }

        holder.btnPostOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.btnPostOptions);
            popupMenu.inflate(R.menu.menu_post_option);
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.item_edit) {
                    // Xử lý khi chọn Edit
                    Intent i = new Intent(((HomeActivity) context), EditPostActivity.class);
                    i.putExtra("postId", post.getId());
                    i.putExtra("position", position);
                    i.putExtra("text", post.getDesc());
                    context.startActivity(i);
                    return true; // Trả về true để thể hiện đã xử lý sự kiện
                } else if (itemId == R.id.item_delete) {
                    deletePost(post.getId(), position);
                    // Xử lý khi chọn Delete
                    return true; // Trả về true để thể hiện đã xử lý sự kiện
                }
                return false;
            });
            popupMenu.show();
        });

        holder.txtComments.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context), CommentActivity.class);
            i.putExtra("postId",post.getId());
            i.putExtra("postPosition",position);
            context.startActivity(i);
        });

        holder.btnComment.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context), CommentActivity.class);
            i.putExtra("postId",post.getId());
            i.putExtra("postPosition",position);
            context.startActivity(i);
        });
    }

    private void deletePost(int postId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận");
        builder.setMessage("Xoá bài viết này?");
        builder.setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
            String url = Constant.DELETE_POST + postId;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest request = new StringRequest(Request.Method.DELETE, url, response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("success")) {
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            listAll.clear();
                            listAll.addAll(list);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    error.printStackTrace();
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String token = preferences.getString("token", "");
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Authorization", "Bearer " + token);
                        return map;
                    }

                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", postId + "");
                        return map;
                    }
                };

                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        });
        builder.setNegativeButton("Huỷ", (dialog, which) -> {
            // Do nothing
        });
        builder.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtTime, txtDesc, txtLikes, txtComments;
        private CircleImageView imgProfile;
        private ImageView imgPost;
        ImageButton btnPostOptions, btnLike, btnComment;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtPostNameUser);
            txtTime = itemView.findViewById(R.id.txtPostTime);
            txtDesc = itemView.findViewById(R.id.txtPostDesc);
            txtLikes = itemView.findViewById(R.id.txtPostLike);
            txtComments = itemView.findViewById(R.id.txtPostComment);
            imgProfile = itemView.findViewById(R.id.imgPostProfile);
            imgPost = itemView.findViewById(R.id.imgPost);
            btnPostOptions = itemView.findViewById(R.id.btnOptionPost);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnPostOptions.setVisibility(View.GONE);
        }
    }
}
