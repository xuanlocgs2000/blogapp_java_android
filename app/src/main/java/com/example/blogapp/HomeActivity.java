package com.example.blogapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.blogapp.Fragments.AccountFragment;
import com.example.blogapp.Fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncher= registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    int result = o.getResultCode();
                    Intent data = o.getData();
                    if (result==RESULT_OK){
            Uri imgUri = data.getData();
            Intent i = new Intent(HomeActivity.this, AddPostActivity.class);
            i.setData(imgUri);
            startActivity(i);
        }
                }
            }
    );
    private FragmentManager fragmentManager;
    private FloatingActionButton flb;
    private BottomNavigationView navigationView;
    private static  final  int GALLERY_ADD_POST=2;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//        fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.frameHomeContainer, new HomeFragment()).commit();
//        initView();
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameHomeContainer,new HomeFragment(),HomeFragment.class.getSimpleName()).commit();
        initView();
    }

    private void initView() {
        navigationView = findViewById(R.id.bottom_nav);
        flb = (FloatingActionButton) findViewById(R.id.fltButton);
        flb.setOnClickListener(v->{
            Intent i  = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,GALLERY_ADD_POST);
            activityResultLauncher.launch(i);
        });

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.item_home) {
                    Fragment account = fragmentManager.findFragmentByTag(AccountFragment.class.getSimpleName());
                    if (account != null) {
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(AccountFragment.class.getSimpleName())).commit();
                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName())).commit();
                    }
                } else if (itemId == R.id.item_account) {
                    Fragment account = fragmentManager.findFragmentByTag(AccountFragment.class.getSimpleName());
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName())).commit();
                    if (account != null) {
                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(AccountFragment.class.getSimpleName())).commit();
                    } else {
                        fragmentManager.beginTransaction().add(R.id.frameHomeContainer, new AccountFragment(), AccountFragment.class.getSimpleName()).commit();
                    }
                }

                return true;
            }
        });


    }
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==GALLERY_ADD_POST&&resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            Intent i = new Intent(HomeActivity.this, AddPostActivity.class);
            i.setData(imgUri);
            startActivity(i);
        }
    }
}