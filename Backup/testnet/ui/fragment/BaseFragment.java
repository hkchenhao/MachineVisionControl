package com.hanyu.hust.testnet.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.ui.SystemConfig;
import com.hanyu.hust.testnet.utils.OperationInterface;

/**
 * 系统设置菜单下相机相关碎片基类
 */
public class BaseFragment extends Fragment  {
	
	private static final String TAG = "BaseFragment";
	
	private static final String[] mCameras = { "相机1", "相机2","相机3" };

	protected SharedPreferences sp;

	private TextView tv_camera_choice;

	private Spinner sp_camera_choice;
	
	public Button bt_fragment_apply, bt_fragment_exit, bt_fragment_save;
	
	protected int CameraID = 1;

	/**
	 *回调框架
	 * @param savedInstanceState
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);

		sp_camera_choice = (Spinner) getActivity().findViewById(
				R.id.sp_camera_choice);
		tv_camera_choice = (TextView) getActivity().findViewById(
				R.id.tv_camera_choice);
		ArrayAdapter<String> adapterCamera = new ArrayAdapter<String>(
				getActivity(), R.layout.layout_spinner, mCameras);
		sp_camera_choice.setAdapter(adapterCamera);

		sp_camera_choice.setSelection(((SystemConfig)getActivity()).cameraID - 1);

		sp_camera_choice
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						switch (position) {
						case 0:
							((SystemConfig)getActivity()).cameraID = 1;
							break;
						case 1:
							((SystemConfig)getActivity()).cameraID = 2;
							break;
						case 2:
							((SystemConfig)getActivity()).cameraID = 3;
							break;
						default:
							((SystemConfig)getActivity()).cameraID = 1;
						}

						onChoose(position);
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
					
		});
		
		bt_fragment_save = (Button)getActivity().findViewById(R.id.bt_fg_save);
		bt_fragment_exit = (Button)getActivity().findViewById(R.id.bt_fg_exit);
		bt_fragment_apply = (Button)getActivity().findViewById(R.id.bt_fg_apply);
		
		bt_fragment_apply.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				OnApply();
			}
		});		
		bt_fragment_save.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				onSave();
			}
		});		
		bt_fragment_exit.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				onExit();
			}
		});		
		getActivity().findViewById(R.id.right_layout).setBackgroundColor(Color.parseColor("#eeeeee"));
	}
	
	public int getCameraID() {
		return CameraID;
	}




	public void onChoose(int position)
	{
		
	}
	
	protected void OnApply()
	{
		
	}
	
	protected void onExit()
	{
		Log.d(TAG, "onExit");
		getActivity().finish();
		getActivity().overridePendingTransition(0, R.anim.top_out);
	}

	public void onSave()
	{

	}



}
