package com.hanyu.hust.testnet.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.ui.BasePannelActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 面板基类，用于文件管理和统计分析界面，记住是Layout，是个布局容器
 */
public class BasePannelLayout extends LinearLayout {

    private Context context;

    @Bind(R.id.bt_control_pannel_top)
    ImageButton bt_top;

    @Bind(R.id.bt_control_pannel_bottom)
    ImageButton bt_bottom;

    @Bind(R.id.bt_control_pannel_right)
    ImageButton bt_right;

    @Bind(R.id.bt_control_pannel_left)
    ImageButton bt_left;

    @Bind(R.id.bt_control_pannel_confirm)
    Button bt_confirm;

    @Bind(R.id.bt_control_pannel_cancel)
    Button bt_cancel;

    public BasePannelLayout(Context context) {
        super(context, null);
    }

    public BasePannelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // 在构造函数中将Xml中定义的布局解析出来。
        LayoutInflater.from(context).inflate(R.layout.control_pannel, this,
                true);
        ButterKnife.bind(this);
        init_widget();
    }

    private void init_widget() {

    }

    /**
     * 绑定监听器
     * @param v
     */
    @OnClick({R.id.bt_control_pannel_left, R.id.bt_control_pannel_right,
            R.id.bt_control_pannel_top, R.id.bt_control_pannel_bottom,
            R.id.bt_control_pannel_confirm, R.id.bt_control_pannel_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_control_pannel_left:
            case R.id.bt_control_pannel_right:
            case R.id.bt_control_pannel_top:
            case R.id.bt_control_pannel_bottom:
                ((BasePannelActivity) context).onDirectionClicked(v.getId());
                break;
            case R.id.bt_control_pannel_confirm:
            case R.id.bt_control_pannel_cancel:
                ((BasePannelActivity) context).onButtonClicked(v.getId());
                break;
        }
    }
}
