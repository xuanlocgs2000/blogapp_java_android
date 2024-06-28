package com.example.blogapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.blogapp.R;

public class ViewPagersAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;
    public ViewPagersAdapter(Context context) {
        this.context = context;
    }
    private int images[]={
            R.drawable.obimg1,
            R.drawable.obimg2,
            R.drawable.obimg3
    };
    private String titles[]={
      "Chia sẻ",
      "Kiến thức",
      "Trải nghiệm"
    };
    private String desc[]={
            "Chia sẻ những điều vui vẻ ",
            "Kiến thức bạn cần",
            "Trải nghiệm cá nhân"
    };
    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.view_pager,container,false);
        //init View
        ImageView imageView = v.findViewById(R.id.imgViewPager);
        TextView txtTitle  = v.findViewById(R.id.txtTitleViewPager);
        TextView txtDesc = v.findViewById(R.id.txtDescViewPager);
        imageView.setImageResource(images[position]);
        txtTitle.setText(titles[position]);
        txtDesc.setText(desc[position]);
        container.addView(v);
        return v;


    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
