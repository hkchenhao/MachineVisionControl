package com.hanyu.hust.testnet.ui.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.adapter.ImageAdapter;
import com.hanyu.hust.testnet.entity.ButtonJson;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.ui.FileExplorer;
import com.hanyu.hust.testnet.ui.HomeAcitivity;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.ui.fragment.FragmentCanLightSrcDrv;
import com.hanyu.hust.testnet.ui.fragment.FragmentCanMotorCtrlCard;
import com.hanyu.hust.testnet.utils.AppDirectory;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.NavigatorUtils;

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
 * 钮扣选择界面
 */
public class ButtonSearch extends Activity {

    private static final String TAG = "ButtonSeach";

    private static final int DATE_DIALOG_ID1 = 1;

    private static final int DATE_DIALOG_ID2 = 2;

    private String[] keys = {"materialF", "sizeF", "shapeF", "patternF", "colorF"};

    private String[] vals = {
            Constants.btnMaterialTypeEnList[0],
            "0",
            Constants.btnShapeTypeEnList[0],
            Constants.btnPatternTypeEnList[0],
            Constants.btnColorTypeEnList[0]
    };

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

    @Bind(R.id.tv_button_ctrl)
    TextView tv_ctrl;

    @Bind(R.id.start_date)
    EditText et_start_date;

    @Bind(R.id.end_date)
    EditText et_end_date;

    @Bind(R.id.cb_general)
    CheckBox cb_general;

    @Bind(R.id.cb_time)
    CheckBox cb_time;


    private String[] materialTypeEnList;
    private String[] materialTypeCnList;
    private String[] sizeTypeCnList;
    private String[] shapeTypeEnList;
    private String[] shapeTypeCnList;
    private String[] colorEnList;
    private String[] colorCnList;
    private String[] patternTypeEnList;
    private String[] patternTypeCnList;

    /**
     * GridView控件，显示图像
     */
    private GridView gvMain;

    private List<String> btnSettingFileList = new ArrayList<String>();

    // 搜索框
    private SearchView searchView;

    // 适配器

    private ImageAdapter mAdapter;

    private ProgressDialog dialog;

    private StringBuilder time1, time2;

    private SettingProfile btnCfgJsonProfile = null;        //配置文件的路径名
    private JSONObject btnCfgJson = null;                   //配置文件的json
    private ButtonJson btnCfgJsonDetail = null;             //配置文件的json内容细节
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

        initConditionList();
        setDateTime();
        initWiget();
        if (AppContext.btnCfgProfile != null) {
            updateLeftView(AppContext.btnCfgProfile.getID());
        }
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
                    vals[0] = materialTypeEnList[sp_material.getSelectedItemPosition()];
                    vals[1] = Integer.toString(sp_size.getSelectedItemPosition());
                    vals[2] = shapeTypeEnList[sp_shape.getSelectedItemPosition()];
                    vals[3] = patternTypeEnList[sp_color_type.getSelectedItemPosition()];
                    vals[4] = colorEnList[sp_color.getSelectedItemPosition()];

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


        sp_shape.setAdapter(generateAdapter(shapeTypeCnList));
        sp_size.setAdapter(generateAdapter(sizeTypeCnList));
        sp_color_type.setAdapter(generateAdapter(patternTypeCnList));
        sp_color.setAdapter(generateAdapter(colorCnList));
        sp_material.setAdapter(generateAdapter(materialTypeCnList));

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
                updateLeftView(btnSettingFileList.get(position));
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
     * 初始化删选条件列表
     */
    private void initConditionList() {
        /**材质*/
        materialTypeEnList = new String[Constants.btnMaterialTypeEnList.length + 1];
        materialTypeEnList[0] = "none";
        for (int i = 0; i < Constants.btnMaterialTypeEnList.length; i++) {
            materialTypeEnList[i + 1] = Constants.btnMaterialTypeEnList[i];
        }
        materialTypeCnList = new String[Constants.btnMaterialTypeCnList.length + 1];
        materialTypeCnList[0] = "不限";
        for (int i = 0; i < Constants.btnMaterialTypeCnList.length; i++) {
            materialTypeCnList[i + 1] = Constants.btnMaterialTypeCnList[i];
        }

        sizeTypeCnList = new String[5];
        sizeTypeCnList[0] = "不限";
        sizeTypeCnList[1] = "<10";
        sizeTypeCnList[2] = "10~20";
        sizeTypeCnList[3] = "20~30";
        sizeTypeCnList[4] = "30~40";

        /**形状*/
        shapeTypeEnList = new String[Constants.btnShapeTypeEnList.length + 1];
        shapeTypeEnList[0] = "none";
        for (int i = 0; i < Constants.btnShapeTypeEnList.length; i++) {
            shapeTypeEnList[i + 1] = Constants.btnShapeTypeEnList[i];
        }
        shapeTypeCnList = new String[Constants.btnShapeTypeCnList.length + 1];
        shapeTypeCnList[0] = "不限";
        for (int i = 0; i < Constants.btnShapeTypeCnList.length; i++) {
            shapeTypeCnList[i + 1] = Constants.btnShapeTypeCnList[i];
        }

        /**花色*/
        shapeTypeEnList = new String[Constants.btnShapeTypeEnList.length + 1];
        shapeTypeEnList[0] = "none";
        for (int i = 0; i < Constants.btnShapeTypeEnList.length; i++) {
            shapeTypeEnList[i + 1] = Constants.btnShapeTypeEnList[i];
        }
        shapeTypeCnList = new String[Constants.btnShapeTypeCnList.length + 1];
        shapeTypeCnList[0] = "不限";
        for (int i = 0; i < Constants.btnShapeTypeCnList.length; i++) {
            shapeTypeCnList[i + 1] = Constants.btnShapeTypeCnList[i];
        }

        /**主色*/
        patternTypeEnList = new String[Constants.btnPatternTypeEnList.length + 1];
        patternTypeEnList[0] = "none";
        for (int i = 0; i < Constants.btnPatternTypeEnList.length; i++) {
            patternTypeEnList[i + 1] = Constants.btnPatternTypeEnList[i];
        }
        patternTypeCnList = new String[Constants.btnPatternTypeCnList.length + 1];
        patternTypeCnList[0] = "不限";
        for (int i = 0; i < Constants.btnPatternTypeCnList.length; i++) {
            patternTypeCnList[i + 1] = Constants.btnPatternTypeCnList[i];
        }

        colorEnList = new String[Constants.btnColorTypeEnList.length + 1];
        colorEnList[0] = "none";
        for (int i = 0; i < Constants.btnColorTypeEnList.length; i++) {
            colorEnList[i + 1] = Constants.btnColorTypeEnList[i];
        }
        colorCnList = new String[Constants.btnColorTypeCnList.length + 1];
        colorCnList[0] = "不限";
        for (int i = 0; i < Constants.btnColorTypeCnList.length; i++) {
            colorCnList[i + 1] = Constants.btnColorTypeCnList[i];
        }
    }

    /**
     * 根据配置文件名，更新左侧视图
     *
     * @param file_name
     */
    private void updateLeftView(String file_name) {
        btnCfgJsonProfile = new SettingProfile(file_name);
        btnCfgJson = btnCfgJsonProfile.getJsonStr();
        btnCfgJsonDetail = ButtonJson.getButtonJson(btnCfgJsonProfile.getJsonStr());
        if (btnCfgJsonDetail != null) {
            iv_photo1.setImageBitmap(btnCfgJsonProfile.getBitmap());
            tv_name.setText("钮扣编号:" + btnCfgJsonDetail.name + "\n时间:" + btnCfgJsonDetail.time);
            tv_info.setText(btnCfgJsonDetail.infoFrontToCnString());
            tv_task.setText(btnCfgJsonDetail.briefSizeInfoToString());
            tv_ctrl.setText(btnCfgJsonDetail.sysCtrlParamToString());
        }
    }

    /**
     * 如果btnCfgJsonProfile不为空，则利用btnCfgJsonProfile对界面进行更新
     */
    private void updateLeftView() {
        if (btnCfgJsonProfile != null) {
            btnCfgJson = btnCfgJsonProfile.getJsonStr();
            btnCfgJsonDetail = ButtonJson.getButtonJson(btnCfgJsonProfile.getJsonStr());
            if (btnCfgJsonDetail != null) {
                iv_photo1.setImageBitmap(btnCfgJsonProfile.getBitmap());
                tv_name.setText("钮扣编号:" + btnCfgJsonDetail.name + "\n时间:" + btnCfgJsonDetail.time);
                tv_info.setText(btnCfgJsonDetail.infoFrontToCnString());
                tv_task.setText(btnCfgJsonDetail.briefSizeInfoToString());
                tv_ctrl.setText(btnCfgJsonDetail.sysCtrlParamToString());
            }
        }
    }

    /**
     * 生成适配器
     *
     * @param paras
     * @return
     */
    private ArrayAdapter<String> generateAdapter(String[] paras) {
        return new ArrayAdapter<>(this,
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

        for (int i = 0; i < files.length; i++) {
            SettingProfile buttonProfile = new SettingProfile(files[i]);
            try {
                if (buttonProfile.checkCondition(keys, values)) {
                    btnSettingFileList.add(files[i].getName());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        updateLeftView();
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
        if (btnCfgJsonProfile != null) {
            View view1 = getLayoutInflater().inflate(R.layout.dialog_password, null);
            final EditText password_input = (EditText) view1.findViewById(R.id.password_editText);
            password_input.setText(Constants.DefaultPassword);
            DialogWindow dialog = new DialogWindow.Builder(ButtonSearch.this)
                    .setView(view1)
                    .setTitle("是否确定进入钮扣配置文件编辑页面？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (password_input.getText().toString().equals(Constants.Password)){
                                Intent intent = new Intent(ButtonSearch.this, MachineLearning.class);
                                intent.putExtra(Constants.btnCfgRetStr, btnCfgJsonProfile.getID());
                                startActivityForResult(intent, 1);
                            }else{
                                password_input.setText("");
                                EToast.showToast(ButtonSearch.this, "口令错误，请重新输入");
                            }
                        }
                    })
                    .setNegativeButton("取消", null).create();
            dialog.show();
        } else {
            EToast.showToast(this, "请选择钮扣配置文件！！！");
        }
    }

    /**
     * 钮扣查询时间
     */
    private void searchButton() {
        final SearchView view = searchView;
        String query = view.getQuery().toString();

        if (!TextUtils.isEmpty(query)) {
            try {
                CheckID(query);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mAdapter.notifyDataSetChanged();
            return;
        }

        if (cb_time.isChecked()) {
            dialog = ProgressBox.show(ButtonSearch.this, "正在查询,请稍候..."); // 进程弹窗
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CheckDate();
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
            vals[0] = materialTypeEnList[sp_material.getSelectedItemPosition()];
            vals[1] = Integer.toString(sp_size.getSelectedItemPosition());
            vals[2] = shapeTypeEnList[sp_shape.getSelectedItemPosition()];
            vals[3] = patternTypeEnList[sp_color_type.getSelectedItemPosition()];
            vals[4] = colorEnList[sp_color.getSelectedItemPosition()];
            dialog = ProgressBox.show(ButtonSearch.this, "正在查询,请稍候..."); // 进程弹窗
            new Thread(new Runnable() {
                @Override
                public void run() {
                    queryButton(keys, vals);
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
        if (btnCfgJsonProfile == null) {
            EToast.showToast(ButtonSearch.this, "请先选择钮扣吧！");
        } else {
            /**检查是否包含尺寸信息*/
            if (btnCfgJson.has(ButtonKeys.TASK_SIZE)) {
                DialogWindow dialog_exit = new DialogWindow.Builder(ButtonSearch.this)
                        .setTitle("确定加载钮扣文件" + btnCfgJsonProfile.getID() + "?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                boolean jsonChanged = false;

                                /**检查控制参数字段*/
                                if (!btnCfgJson.has(ButtonKeys.SYSTEMPARAMETER)) {
                                    try {
                                        JSONObject ctrlItem = new JSONObject();
                                        for (int i = 0; i < ButtonKeys.litgtSy.length; i++) {
                                            ctrlItem.put(ButtonKeys.litgtSy[i], 0);
                                        }
                                        for (int i = 0; i < ButtonKeys.enginSpeedSy.length; i++) {
                                            ctrlItem.put(ButtonKeys.enginSpeedSy[i], 0);
                                        }
                                        for (int i = 0; i < ButtonKeys.blowSy.length; i++) {
                                            ctrlItem.put(ButtonKeys.blowSy[i], 0);
                                        }

                                        ctrlItem.put(ButtonKeys.litgtSy[1], FragmentCanLightSrcDrv.DefaultBrightness);
                                        ctrlItem.put(ButtonKeys.litgtSy[2], FragmentCanLightSrcDrv.DefaultBrightness);
                                        ctrlItem.put(ButtonKeys.litgtSy[4], FragmentCanLightSrcDrv.DefaultBrightness);
                                        ctrlItem.put(ButtonKeys.enginSpeedSy[1], FragmentCanMotorCtrlCard.DefaultBelterSpd);
                                        ctrlItem.put(ButtonKeys.enginSpeedSy[2], FragmentCanMotorCtrlCard.DefaultServoSpd);

                                        btnCfgJson.put(ButtonKeys.SYSTEMPARAMETER, ctrlItem);
                                        jsonChanged = true;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                }

                                /**检查算法参数字段*/
                                if (!btnCfgJson.has(ButtonKeys.ALGPARA)) {
                                    try {
                                        JSONObject j_obj = new JSONObject();
                                        for (int i = 0; i < ButtonKeys.GeoMethodPara.length; i++) {
                                            j_obj.put(ButtonKeys.GeoMethodPara[i], 0);
                                        }
                                        for (int i = 0; i < ButtonKeys.SurMethodPara.length; i++) {
                                            j_obj.put(ButtonKeys.SurMethodPara[i], 0);
                                        }
                                        btnCfgJson.put(ButtonKeys.ALGPARA, j_obj);
                                        jsonChanged = true;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                /**如果对json进行了更改，写入文件中*/
                                if (jsonChanged) {
                                    btnCfgJsonProfile.write2sd(btnCfgJson);
                                }
                                /**向HomeActicity发送配置文件的名字*/
                                Intent intent = new Intent();
                                intent.putExtra(Constants.btnCfgRetStr, btnCfgJsonProfile.getID());
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
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