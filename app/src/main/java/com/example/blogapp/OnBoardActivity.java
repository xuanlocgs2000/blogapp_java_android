package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.blogapp.Adapters.ViewPagersAdapter;

public class OnBoardActivity extends AppCompatActivity {
    ViewPager viewPager;
    Button btnLeft, btnRight;
    ViewPagersAdapter adapter;
    LinearLayout dotsLayout;
    TextView[] dots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        initView();


    }

    private void initView() {
        viewPager = findViewById(R.id.view_pager);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        dotsLayout = findViewById(R.id.dotsLayout);
        adapter  = new ViewPagersAdapter(this);
        addDots(0);;
        viewPager.addOnPageChangeListener(listener);
        viewPager.setAdapter(adapter);

        btnRight.setOnClickListener(v->{
            //if btn text = next ->next page
            if (btnRight.getText().toString().equals("Next")){
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);

            }
            else{
                startActivity(new Intent(OnBoardActivity.this,AuthActivity.class));
                finish();
            }
        });
        btnLeft.setOnClickListener(v->{
            //if click btn skip ->go page
            viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
        });
    }
    private void addDots(int position){
        dotsLayout.removeAllViews();
        dots= new TextView[3];
        for (int i =0;i<dots.length;i++){
            dots[i]= new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226",Html.FROM_HTML_MODE_LEGACY));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorLightGrey,getApplication().getTheme()));
            dotsLayout.addView(dots[i]);

        }
        if(dots.length>0){
            dots[position].setTextColor(getResources().getColor(R.color.colorGrey,getApplication().getTheme()));
        }
    }
    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            //thay skip = finish if show full
            //hide skip btn
            if(position<2){
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(true);
                //Right
                btnRight.setText("Next");
            }
            else{
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Finish");

            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}