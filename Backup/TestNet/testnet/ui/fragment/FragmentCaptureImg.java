package com.hanyu.hust.testnet.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.hanyu.hust.testnet.R;

/**
 *  Created by HanYu on 2017/5
 *  获取图像的界面
 */
public class FragmentCaptureImg extends Fragment {
	/**
	 * UI组件
	 */
	private Button bt_left, bt_right, bt_return;//控制按键

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fr_capture_image, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		bt_left = (Button) getActivity().findViewById(R.id.bt_left);
		bt_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});

		bt_right = (Button) getActivity().findViewById(R.id.bt_right);
		bt_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment fragment = new FragmentShowInfo();
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				transaction.replace(R.id.fr_layout_machine_learn, fragment);
				transaction.commit();				
			}
		});

		bt_return = (Button) getActivity().findViewById(R.id.bt_return);
		bt_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
				getActivity().overridePendingTransition(0, R.anim.top_out);
			}
		});

	}
}
