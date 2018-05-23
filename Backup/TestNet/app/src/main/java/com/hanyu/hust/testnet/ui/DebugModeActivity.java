package com.hanyu.hust.testnet.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.task.BaseTask;
import com.hanyu.hust.testnet.task.VedioTask;
import com.hanyu.hust.testnet.utils.HandlerNetMsg;
import com.hanyu.hust.testnet.utils.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 调试模式下界面
 */
public class DebugModeActivity extends Activity {

    private static final String TAG = "DebugModeActivity";

    /**
     * 异步事件接受器，在主线程中处理
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetUtils.MSG_NET_GET_VIDEO:
                    Bitmap bitmap = HandlerNetMsg.handlerVedio(msg);
                    iv_fullImage1.setImageBitmap(bitmap);
                    LogUtil.d(TAG, "MSG_NET_GET_VIDEO");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * ButterKnife注解框架，相当于iv_fullImage1 = findViewById（XXX）
     */
    @Bind(R.id.iv_debug_mode1)
    ImageView iv_fullImage1;

    @Bind(R.id.iv_debug_mode2)
    ImageView iv_fullImage2;

    @Bind(R.id.bt_debug_mode)
    Button bt_debug_mode;

    private BaseTask mSendThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*取消屏幕休眠，add by wuxin 170714*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_debug_mode);

        ButterKnife.bind(this);
        NetUtils.setIp();

        /**
         * 3号相机创建视频流任务
         */
        mSendThread = new VedioTask("3", mHandler, 200);
        mSendThread.setThreadName("GetVedio3").start();
    }

    /**
     * 取消视频任务
     * @param view
     */
    @OnClick({R.id.bt_debug_mode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_debug_mode: {
                mSendThread.cancle();
                finishWithAnim();
            }
            break;
            default:
                break;
        }
    }

    /**
     * 退出动画
     */
    private void finishWithAnim()
    {
        finish();
        overridePendingTransition(0, R.anim.top_out);
    }

}