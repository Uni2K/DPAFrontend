package com.project.app.CustomViews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.project.app.R;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;
    private View tri;

    public CustomMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.marker_text);
        tri=findViewById(R.id.marker_tri);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText(e.getY()+"" );
    }

    public void setText(String text){
        tvContent.setText(text);
    }
    public  void setTextColor(int color){
        tvContent.setTextColor(color);
    }
    public  void setBackgroundColor(int color){
        ColorStateList c= ColorStateList.valueOf(color);
        tvContent.setBackgroundTintList(c);
        tri.setBackgroundTintList(c);

    }
}