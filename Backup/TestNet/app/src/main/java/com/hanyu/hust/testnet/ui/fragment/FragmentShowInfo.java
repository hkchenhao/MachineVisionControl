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
import com.hanyu.hust.testnet.ui.view.EToast;

/**
 * 展示信息类
 */
public class FragmentShowInfo extends Fragment {
	/**
	 * UI组件
	 */
	private Button bt_left, bt_right, bt_return, bt_save;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fr_show_info, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/*向左按钮点击事件*/
		bt_left = (Button) getActivity().findViewById(R.id.bt_left);

		bt_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment fragment = new FragmentCaptureImg();
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				transaction.replace(R.id.fr_layout_machine_learn, fragment);
				transaction.commit();
			}
		});
		/*向右按钮点击事件*/
		bt_right = (Button) getActivity().findViewById(R.id.bt_right);
		bt_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		/*返回按钮点击事件*/
		bt_return = (Button) getActivity().findViewById(R.id.bt_return);
		bt_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
				getActivity().overridePendingTransition(0, R.anim.top_out);
			}
		});

		bt_save = (Button) getActivity().findViewById(R.id.bt_save);
		bt_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EToast.showToast(getActivity(), "保存成功");
			}
		});
	}
}
