package com.hanyu.hust.testnet.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hanyu.hust.testnet.R;

/**
 * Created by Administrator on 2016/11/15.
 * 分割线控件
 */
public class TitleDivide extends LinearLayout {

	private static final String TAG = "TitleDivide";

	private Context context;

    private TextView tv_title;

    private View view;

    public TitleDivide(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray ta= context.obtainStyledAttributes(attrs, R.styleable.titleDivide);
        float textSize = ta.getDimension(R.styleable.titleDivide_textSize, 30);
        String text = ta.getString(R.styleable.titleDivide_text);

        setOrientation(LinearLayout.HORIZONTAL);
        tv_title = new TextView(context);
        tv_title.setText(text);
        tv_title.setTextSize(textSize);
        LayoutParams paramsLeft = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0);
        tv_title.setPadding(0, 0, 10, 0);        
        addView(tv_title, paramsLeft);
        
        view = new View(context);
        LayoutParams paramsRight = new LayoutParams(0, 1, 1);
        paramsRight.gravity = Gravity.CENTER_VERTICAL;
        view.setBackgroundResource(R.mipmap.line);
        
        addView(view, paramsRight);
        ta.recycle();
    }
}
