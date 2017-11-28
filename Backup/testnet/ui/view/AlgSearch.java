package com.hanyu.hust.testnet.ui.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.adapter.ImageAdapter;
import com.hanyu.hust.testnet.entity.AlgCfgJson;
import com.hanyu.hust.testnet.entity.AlgCfgJsonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.ui.FileExplorer;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.utils.AlgSettingUtils;
import com.hanyu.hust.testnet.utils.AppDirectory;
import com.hanyu.hust.testnet.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xinshangqiu on 2017/7/15.
 */

public class AlgSearch extends Activity {

    @Bind(R.id.btn_alg_act_exit)
    Button btn_alg_exit;

    @Bind(R.id.btn_alg_act_edit)
    Button btn_alg_edit;

    @Bind(R.id.btn_alg_act_confirm)
    Button btn_alg_act_confirm;

    @Bind(R.id.iv_sel_alg)
    ImageView iv_sel_alg;

    @Bind(R.id.tv_sel_alg_name)
    TextView tv_sel_alg_name;

    @Bind(R.id.tv_sel_alg_info)
    TextView tv_sel_alg_info;

    /*算法设置文件的名字*/
    private String algCfgFileName;

    /*栅格视窗*/
    private GridView gvMain;

    /*文件列表*/
    private List<String> algCfgFileList = new ArrayList<String>();
    private ImageAdapter mAdapter;


    /**
     * 初始化创建类
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alg_search);
        ButterKnife.bind(this);

        initWidget();
    }

    /**
     * 回调框架
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SettingProfile profile = new SettingProfile(AppDirectory.getAlgDirectory() + algCfgFileName, algCfgFileName);
        String time;
        try {
            time = profile.getJsonStr().getString(AlgCfgJsonKeys.time);
        } catch (JSONException e) {
            e.printStackTrace();
            time = "";
        }

        /*** 在左上角进行信息输出* */
        Bitmap bitmap = profile.getBitmap();
        iv_sel_alg.setImageBitmap(bitmap);
        tv_sel_alg_name.setText("配置文件：" + algCfgFileName + ".ini\n 时间：" + time);

                /*解析json文件，显示在左侧*/
        JSONObject jObj = profile.getJsonStr();
        parseJson(jObj);
    }

    /**
     * 初始化窗口
     * */
    private void initWidget() {
        gvMain = (GridView) findViewById(R.id.gv_alg_sel);
        try {
            initListView();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         * 生成适配器
         * */
        mAdapter = new ImageAdapter(AlgSearch.this, gvMain, algCfgFileList);
        gvMain.setAdapter(mAdapter);
        gvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                algCfgFileName = algCfgFileList.get(position);
                SettingProfile profile = new SettingProfile(AppDirectory.getAlgDirectory() + algCfgFileName, algCfgFileName);
                String time;
                try {
                    time = profile.getJsonStr().getString(AlgCfgJsonKeys.time);
                } catch (JSONException e) {
                    e.printStackTrace();
                    time = "";
                }

                /*** 加载左上角显示的图片* */
                Bitmap bitmap = profile.getBitmap();
                iv_sel_alg.setImageBitmap(bitmap);
                tv_sel_alg_name.setText("配置文件：" + algCfgFileName + ".ini\n 时间：" + time);

                /*解析json文件，显示在左侧*/
                JSONObject jObj = profile.getJsonStr();
                parseJson(jObj);
            }
        });


        /**
         * 长按点击事件
         * */
        gvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final String Id = algCfgFileList.get(position);
                DialogWindow dialogDelete = new DialogWindow.Builder(AlgSearch.this)
                        .setTitle("确定删除" + Id + "?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String path = AppDirectory.getAlgDirectory() + Id;
                                FileExplorer.deleteDirectory(new File(path));
                                algCfgFileList.remove(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialogDelete.showWrapContent();
                return false;
            }
        });
    }

    /**
     * algSettingJson文件解析
     */
    protected void parseJson(JSONObject jObj) {
        JSONObject atomAlgJson;
        String info = null;
        String cameraNum = null;
        String[] arg = new String[AlgCfgJsonKeys.argc];

        try {
            cameraNum = jObj.getString(AlgCfgJsonKeys.cameraNum);
            atomAlgJson = jObj.getJSONObject(AlgCfgJsonKeys.atomAlg);
            for(int i=0;i<AlgCfgJsonKeys.argc;i++){
                arg[i] = AlgSettingUtils.algNameEn2Cn(atomAlgJson.getString(AlgCfgJsonKeys.arg[i]));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        info = "相机：" + cameraNum + "\n";
        for(int i=0;i<AlgCfgJsonKeys.argc;i++){
            info = info +"算法"+(i+1)+":"+arg[i]+"\n";
        }

        tv_sel_alg_info.setText(info);

    }

    /**
     * 从SD卡加载数据到ListView
     */
    private void initListView() throws JSONException {
        File parentFile = new File(AppDirectory.getAlgDirectory());
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        File[] files = parentFile.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                algCfgFileList.add(files[i].getName());
            }
        }
    }

    /**
     * 按键监听
     */
    @OnClick({R.id.btn_alg_act_confirm, R.id.btn_alg_act_edit, R.id.btn_alg_act_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_alg_act_confirm: {
                onConfirm();
                break;
            }
            case R.id.btn_alg_act_edit: {
                onEdit();
                break;
            }
            case R.id.btn_alg_act_exit: {
                onExit();
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * 确定键相关操作
     */
    private void onConfirm() {
        if (algCfgFileName == null) {
            EToast.showToast(AlgSearch.this, "请先选择算法配置文件");
        } else {
            DialogWindow dialog_exit = new DialogWindow.Builder(AlgSearch.this)
                    .setTitle("确定加载算法配置文件" + algCfgFileName + "?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent();
                            intent.putExtra(Constants.algCfgRetStr, algCfgFileName);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
            dialog_exit.showWrapContent();
        }
    }

    /**
     * 编辑键相关操作
     */
    private void onEdit() {
        Intent intent = new Intent(AlgSearch.this, MachineLearning.class);
        intent.putExtra(Constants.algCfgRetStr, algCfgFileName);
        startActivityForResult(intent, 1);
    }

    /**
     * 退出键相关操作
     */
    private void onExit() {
        finish();
    }

}
