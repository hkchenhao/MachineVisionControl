package com.hanyu.hust.testnet.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.ScrollBar;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.ui.view.EToast;

import static java.lang.String.format;

/**
 * Created by Administrator on 2017/4/21.
 * 帮助界面
 */
public class Help extends Activity {

    private String pdfName = "帮助文档";

    private Button bt_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*取消屏幕休眠，add by wuxin 170714*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_help);

        // 绑定控件
        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);

        // 滚动条
        ScrollBar scrollBar = (ScrollBar) findViewById(R.id.scrollBar);
        pdfView.setScrollBar(scrollBar);

        // 从Assert文件夹下help.pdf文件加载数据
        pdfView.fromAsset("help.pdf").defaultPage(1).showMinimap(false)
                .enableSwipe(true).onLoad(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {
                EToast.showToast(Help.this, "预加载完成");
            }
        }).onPageChange(new OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount) {
                setTitle(format("%s %s / %s", pdfName, page, pageCount));
            }
        }).load();

        bt_exit = (Button) findViewById(R.id.bt_help_exit);
        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.top_out);
            }
        });
    }
}

