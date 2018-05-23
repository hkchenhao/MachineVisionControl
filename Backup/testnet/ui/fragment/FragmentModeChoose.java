package com.hanyu.hust.testnet.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.ui.DebugModeActivity;

/**
 * 模式选择界面
 */
public class FragmentModeChoose extends BaseFragment {

	private static final String[] mMode = { "分拣模式", "演示模式", "调试模式"};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fr_mode_choose, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Spinner sp_mode_choose = (Spinner) getActivity().findViewById(R.id.sp_mode_choose);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.layout_spinner, mMode);
		sp_mode_choose.setAdapter(adapter);
	}
	
	@Override
	protected void OnApply() {
		super.OnApply();
		startActivity(new Intent(getActivity(), DebugModeActivity.class));
	}
}
