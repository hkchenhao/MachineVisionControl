package com.hanyu.hust.testnet.ui.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.adapter.ImageAdapter;
import com.hanyu.hust.testnet.entity.ButtonJson;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.ui.FileExplorer;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.utils.AppDirectory;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 纽扣选择界面
 */
public class ButtonSearch extends Activity {

//    private static final String TAG = "ButtonSeach";

    private static final int DATE_DIALOG_ID1 = 1;

    private static final int DATE_DIALOG_ID2 = 2;

//    private static final String[] mMaterial = {"树脂", "果实", "贝壳", "金属", "玉石"};

    private static final String[] mSize = {"<10", "10-20", "20-30", "30-40"};

//    private static final String[] mShapes = {"圆形", "心形", "三角形", "方形", "五角形", "梅花形", "异形"};

//    private static final String[] mColorType = {"纯色", "拼色", "花纹", "字符", "图案"};

//    private static final String[] mColor = {"白色", "红色", "橙色", "黄色", "绿色", "青色", "蓝色", "紫色", "黑色"};

    private String[] keys = {"materialF", "sizeF", "shapeF", "patternF", "colorF"};

    private String[] vals = {"树脂", "0", "圆形", "纯色", "白色"};

    /**
     * 时间
     */
    private int mYear1, mYear2;

    private int mMonth1, mMonth2;

    private int mDay1, mDay2;

    final Calendar c = Calendar.getInstance();

    @Bind(R.id.bt_fg_exit)
    Button bt_return;

    @Bind(R.id.bt_top)
    Button bt_top;

    @Bind(R.id.bt_bottom)
    Button bt_bottom;

    @Bind(R.id.bt_search)
    Button bt_search;

    @Bind(R.id.bt_fg_confirm)
    Button bt_confirm;

    @Bind(R.id.bt_edit)
    Button bt_edit;

    @Bind(R.id.sp_material_search)
    Spinner sp_material;

    @Bind(R.id.sp_size_search)
    Spinner sp_size;

    @Bind(R.id.sp_shape_search)
    Spinner sp_shape;

    @Bind(R.id.sp_color_type)
    Spinner sp_color_type;

    @Bind(R.id.sp_color_search)
    Spinner sp_color;

    @Bind(R.id.iv_front)
    ImageView iv_photo1;

    @Bind(R.id.tv_button_name)
    TextView tv_name;

    @Bind(R.id.tv_button_info)
    TextView tv_info;

    @Bind(R.id.tv_button_task)
    TextView tv_task;

    @Bind(R.id.start_date)
    EditText et_start_date;

    @Bind(R.id.end_date)
    EditText et_end_date;

    @Bind(R.id.cb_general)
    CheckBox cb_general;

    @Bind(R.id.cb_time)
    CheckBox cb_time;

    /**
     * GridView控件，显示图像
     */
    private GridView gvMain;

    private List<String> btnSettingFileList = new ArrayList<String>();

    //Json
    private JSONObject mJson = null;
    // 搜索框
    private SearchView searchView;

    // 适配器

    private ImageAdapter mAdapter;

    private ProgressDialog dialog;

    private String btnCfgFileName;

    private StringBuilder time1, time2;


    /**
     * 异步事件处理器
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DATE_DIALOG_ID1:
                    showDialog(DATE_DIALOG_ID1);
                    break;
                case DATE_DIALOG_ID2:
                    showDialog(DATE_DIALOG_ID2);
                    break;
            }
        }

        ;
    };

    /**
     * 日期控件的事件
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear1 = year;
            mMonth1 = monthOfYear;
            mDay1 = dayOfMonth;
            updateDateDisplay(1);
        }
    };
    private DatePickerDialog.OnDateSetListener mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear2 = year;
            mMonth2 = monthOfYear;
            mDay2 = dayOfMonth;
            updateDateDisplay(2);
        }
    };

    /**
     * 更新日期显示
     */
    private void updateDateDisplay(int id) {
        if (id == DATE_DIALOG_ID1) {
            time1 = new StringBuilder().append(mYear1)
                    .append("-")
                    .append((mMonth1 + 1) < 10 ? "0" + (mMonth1 + 1)
                            : (mMonth1 + 1)).append("-")
                    .append((mDay1 < 10) ? "0" + mDay1 : mDay1);
            et_start_date.setText(time1);
        }
        if (id == DATE_DIALOG_ID2) {
            time2 = new StringBuilder()
                    .append(mYear2)
                    .append("-")
                    .append((mMonth2 + 1) < 10 ? "0" + (mMonth2 + 1)
                            : (mMonth2 + 1)).append("-")
                    .append((mDay2 < 10) ? "0" + mDay2 : mDay2);
            et_end_date.setText(time2);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID1:
                return new DatePickerDialog(this, mDateSetListener1, mYear1,
                        mMonth1, mDay1);
            case DATE_DIALOG_ID2:
                return new DatePickerDialog(this, mDateSetListener2, mYear2,
                        mMonth2, mDay2);
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DATE_DIALOG_ID1:
                ((DatePickerDialog) dialog).updateDate(mYear1, mMonth1, mDay1);
                break;
            case DATE_DIALOG_ID2:
                ((DatePickerDialog) dialog).updateDate(mYear2, mMonth2, mDay2);
                break;
        }
    }

    /**
     * 回调框架
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setDateTime();
        initWiget();
    }

    /**
     * 初始化控件
     */
    private void initWiget() {
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    try {
                        CheckID(query);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    vals[0] = Constants.btnMaterialTypeCnList[sp_material.getSelectedItemPosition()];
                    vals[1] = sp_size.getSelectedItemPosition() + "";
                    vals[2] = Constants.btnShapeTypeCnList[sp_shape.getSelectedItemPosition()];
                    vals[3] = Constants.btnPatternTypeCnList[sp_color_type.getSelectedItemPosition()];
                    vals[4] = Constants.btnColorTypeCnList[sp_color.getSelectedItemPosition()];

                    queryButton(keys, vals);
                    mAdapter.notifyDataSetChanged();
                }
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        et_start_date.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mHandler.sendEmptyMessage(DATE_DIALOG_ID1);
                }
            }
        });
        et_end_date.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mHandler.sendEmptyMessage(DATE_DIALOG_ID2);
                }
            }
        });

        sp_shape.setAdapter(generateAdapter(Constants.btnShapeTypeCnList));
        sp_size.setAdapter(generateAdapter(mSize));
        sp_color_type.setAdapter(generateAdapter(Constants.btnPatternTypeCnList));
        sp_color.setAdapter(generateAdapter(Constants.btnColorTypeCnList));
        sp_material.setAdapter(generateAdapter(Constants.btnMaterialTypeCnList));

        gvMain = (GridView) findViewById(R.id.gv_main);
        try {
            initListView();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         * 生成适配器
         */
        mAdapter = new ImageAdapter(ButtonSearch.this, gvMain, btnSettingFileList);
        gvMain.setAdapter(mAdapter);
        gvMain.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                btnCfgFileName = btnSettingFileList.get(position);
                SettingProfile profile = new SettingProfile(btnCfgFileName);
                String time;
                try {
                    time = profile.getJsonStr().getString("time");
                } catch (JSONException e) {
                    e.printStackTrace();
                    time = "";
                }
                Bitmap bitmap = profile.getBitmap();
                iv_photo1.setImageBitmap(bitmap);
                tv_name.setText("  钮扣编号: " + btnCfgFileName + "\n  时间: " + time);

                mJson = profile.getJsonStr();

                parseJson(mJson);
            }
        });

        /**
         * 长按点击事件
         */
        gvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final String Id = btnSettingFileList.get(position);
                DialogWindow dialogDelete = new DialogWindow.Builder(ButtonSearch.this)
                        .setTitle("确定删除" + Id + "?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                String path = AppDirectory.getLeaningDirectory() + Id;
                                FileExplorer.deleteDirectory(new File(path));
                                btnSettingFileList.remove(position);
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
     * 生成适配器
     *
     * @param paras
     * @return
     */
    private ArrayAdapter<String> generateAdapter(String[] paras) {
        return new ArrayAdapter<String>(this,
                R.layout.item_spinner, paras);
    }

    /**
     * 设置日期
     */
    private void setDateTime() {
        mYear1 = c.get(Calendar.YEAR);
        mMonth1 = c.get(Calendar.MONTH);
        mDay1 = c.get(Calendar.DAY_OF_MONTH);
        mYear2 = c.get(Calendar.YEAR);
        mMonth2 = c.get(Calendar.MONTH);
        mDay2 = c.get(Calendar.DAY_OF_MONTH);
        updateDateDisplay(1);
        updateDateDisplay(2);
    }

    /**
     * ID 查询
     *
     * @param id
     * @throws JSONException
     */
    void CheckID(String id) throws JSONException {
        File parentFile = new File(AppDirectory.getLeaningDirectory());
        if (!parentFile.exists())
            parentFile.mkdirs();
        btnSettingFileList.clear();

        File[] files = parentFile.listFiles();

        for (int i = 0; i < files.length; i++) {
            String info = files[i].getName();
            if (info.contains(id)) {
                btnSettingFileList.add(info);
            }
        }
    }

    /**
     * 事件段查询
     */
    void CheckDate() {
        File parentFile = new File(AppDirectory.getLeaningDirectory());
        if (!parentFile.exists())
            parentFile.mkdirs();
        btnSettingFileList.clear();

        File[] files = parentFile.listFiles();

        for (int i = 0; i < files.length; i++) {
            SettingProfile buttonProfile = new SettingProfile(files[i]);
            try {
                if (buttonProfile.checkDate(time1.toString(), time2.toString())) {
                    btnSettingFileList.add(files[i].getName());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 条件查询
     *
     * @param keys   查询项目
     * @param values 查询值
     */
    void queryButton(String[] keys, String[] values) {
        File parentFile = new File(AppDirectory.getLeaningDirectory());
        if (!parentFile.exists())
            parentFile.mkdirs();
        File[] files = parentFile.listFiles();
        btnSettingFileList.clear();

        LogUtil.d("ButtonSearch", "filesize = " + files.length);

        for (int i = 0; i < files.length; i++) {
            SettingProfile buttonProfile = new SettingProfile(files[i]);
            try {
                if (buttonProfile.checkCondition(keys, values)) {
                    Log.d("ButtonSearch", "enter1");
                    btnSettingFileList.add(files[i].getName());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据Json字符串生成纽扣配置文件对象ButtonJson
     *
     * @param json
     */
    protected void parseJson(JSONObject json) {
        JsonParser jParser = new JsonParser();
        Gson gson = new Gson();

        ButtonJson buttonJson = gson.fromJson(jParser.parse(json.toString()).getAsJsonObject().toString(), ButtonJson.class);

        String info = null, task = null;
        String material =   Constants.btnMaterialEn2Cn(buttonJson.infoFront.materialF);
        String size =       buttonJson.infoFront.sizeF;
        String shape =      Constants.btnShapeEn2Cn(buttonJson.infoFront.shapeF);
        String holeNum =    buttonJson.infoFront.holeNumF;
        String light =      Constants.btnLightEn2Cn(buttonJson.infoFront.lightF);
        String pattern =    Constants.btnPatternEn2Cn(buttonJson.infoFront.patternF);
        String color =      Constants.btnColorEn2Cn(buttonJson.infoFront.colorF);

        info = "  材料: " + material + "\n" + "  尺寸: " + size + "\n" + "  外形: " + shape + "\n" + "  线孔数: " + holeNum + "\n"
                + "  透明性: " + light + "\n" + "  颜色: " + pattern + "/" + color + "\n";
        tv_info.setText(info);

        if (buttonJson.taskSize != null) {
            double outDia = buttonJson.taskSize.outDia;
            double outDiaDevDown = buttonJson.taskSize.outDiaDevDown;
            double outDiaDevUp = buttonJson.taskSize.outDiaDevUp;

            double holeDia = buttonJson.taskSize.holeDia;
            double holeDiaDevDown = buttonJson.taskSize.holeDiaDevDown;
            double holeDiaDevUp = buttonJson.taskSize.holeDiaDevUp;

            double holeDist = buttonJson.taskSize.holeDist;
            double holeDistDevDown = buttonJson.taskSize.holeDistDevDown;
            double holeDistDevUp = buttonJson.taskSize.holeDistDevUp;

            task = "  外径: " + outDia + "[" + outDiaDevDown + "," + outDiaDevUp + "]\n"
                    + "  线径: " + holeDia + "[" + holeDiaDevDown + "," + holeDiaDevUp + "]\n"
                    + "  线孔距: " + holeDist + "[" + holeDistDevDown + "," + holeDistDevUp + "]\n";
            tv_task.setText(task);
        } else {
            tv_task.setText("外径：\n" + "线径：\n" + "线孔距\n");
        }
    }

    /**
     * 从SD卡目录中加载数据到ListView
     *
     * @throws JSONException
     */
    private void initListView() throws JSONException {
        File parentFile = new File(AppDirectory.getLeaningDirectory());
        if (!parentFile.exists())
            parentFile.mkdirs();
        File[] files = parentFile.listFiles();
        if (files != null)
            for (int i = 0; i < files.length; i++) {
                btnSettingFileList.add(files[i].getName());
            }
    }

    /**
     * 回调框架
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SettingProfile profile = new SettingProfile(btnCfgFileName);
        String time;
        try {
            time = profile.getJsonStr().getString("time");
        } catch (JSONException e) {
            e.printStackTrace();
            time = "";
        }
        Bitmap bitmap = profile.getBitmap();
        iv_photo1.setImageBitmap(bitmap);
        tv_name.setText("  钮扣编号: " + btnCfgFileName + "\n  时间: " + time);

        JSONObject obj = profile.getJsonStr();
        parseJson(obj);
    }

    /**
     * 按钮监听事件
     *
     * @param view
     */
    @OnClick({R.id.bt_fg_exit, R.id.bt_top, R.id.bt_bottom,
            R.id.bt_search, R.id.bt_fg_confirm, R.id.bt_edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_fg_exit: {
                onReturn();
                break;
            }
            case R.id.bt_top: {
                int position = gvMain.getFirstVisiblePosition() - 2;
                if (position < 0) {
                    position = 0;
                }
                gvMain.smoothScrollToPosition(position);
                break;
            }
            case R.id.bt_bottom: {
                int position = gvMain.getLastVisiblePosition() + 2;
                if (position >= gvMain.getCount()) {
                    position = gvMain.getCount() - 1;
                }
                gvMain.smoothScrollToPosition(position);
                break;
            }
            case R.id.bt_search: {
                searchButton();
                break;
            }
            case R.id.bt_fg_confirm: {
                onConfirm();
                break;
            }

            case R.id.bt_edit: {
                onEdit();
                break;
            }
        }
    }

    /**
     * 编辑菜单事件
     */
    private void onEdit() {
        Intent intent = new Intent(ButtonSearch.this, MachineLearning.class);
        intent.putExtra(Constants.btnCfgRetStr, btnCfgFileName);
        startActivityForResult(intent, 1);
    }

    /**
     * 纽扣查询时间
     */
    private void searchButton() {
        final SearchView view = searchView;
        String query = view.getQuery().toString();

        if (!TextUtils.isEmpty(query)) {
            long tic = System.currentTimeMillis();
            try {
                CheckID(query);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            long toc = System.currentTimeMillis();
            mAdapter.notifyDataSetChanged();
            return;
        }

        if (cb_time.isChecked()) {
            dialog = ProgressBox.show(ButtonSearch.this, "正在查询,请稍候..."); // 进程弹窗
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long tic = System.currentTimeMillis();
                    CheckDate();
                    long toc = System.currentTimeMillis();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                }
            }).start();
            return;
        }

        if (cb_general.isChecked()) {
            vals[0] = Constants.btnMaterialTypeCnList[sp_material.getSelectedItemPosition()];
            vals[1] = sp_size.getSelectedItemPosition() + "";
            vals[2] = Constants.btnShapeTypeCnList[sp_shape.getSelectedItemPosition()];
            vals[3] = Constants.btnPatternTypeCnList[sp_color_type.getSelectedItemPosition()];
            vals[4] = Constants.btnColorTypeCnList[sp_color.getSelectedItemPosition()];
            dialog = ProgressBox.show(ButtonSearch.this, "正在查询,请稍候..."); // 进程弹窗
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long tic = System.currentTimeMillis();
                    queryButton(keys, vals);
                    long toc = System.currentTimeMillis();

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                }
            }).start();
            return;
        }
    }

    /**
     * 确定按钮事件
     */
    private void onConfirm() {
        if (btnCfgFileName == null) {
            EToast.showToast(ButtonSearch.this, "请先选择钮扣吧！");
        } else {
            /*TODO：判断json字段中是否含有尺寸信息,无尺寸信息无法发送该配置文件*/
            SettingProfile profile = new SettingProfile(AppDirectory.getLeaningDirectory() + btnCfgFileName, btnCfgFileName);
            JSONObject jObj = profile.getJsonStr();
            JSONObject taskSizeJObj = null;

            try {
                taskSizeJObj = jObj.getJSONObject(ButtonKeys.TASK_SIZE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (taskSizeJObj != null) {
                DialogWindow dialog_exit = new DialogWindow.Builder(ButtonSearch.this)
                        .setTitle("确定加载钮扣文件" + btnCfgFileName + "?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent();
                                intent.putExtra(Constants.btnCfgRetStr, btnCfgFileName);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                AppContext.btnCfgJson = jObj;
                AppContext.btnCfgProfile = profile;
                dialog_exit.showWrapContent();
            } else {
                EToast.showToast(ButtonSearch.this, "请完成尺寸测量信息的配置");
            }
        }
    }

    /**
     * 返回按钮事件
     */
    private void onReturn() {
        finish();
    }

}