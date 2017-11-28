package com.hanyu.hust.testnet.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.hanyu.hust.testnet.R;

/**
 * 机器学习菜单下算法设置界面
 *
 */
public class FragmentOptSetting extends Fragment {

	private static final String[] mALg = { "算法1", "算法2", "算法3"};

	Spinner sp_geometry, sp_surface, sp_impurity, sp_color_diff;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fr_alg, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		sp_geometry = (Spinner) getActivity().findViewById(R.id.sp_alg_size);

		sp_surface = (Spinner) getActivity().findViewById(R.id.sp_alg_surface);
		sp_impurity = (Spinner) getActivity().findViewById(R.id.sp_alg_pit);
		sp_color_diff = (Spinner) getActivity().findViewById(R.id.sp_alg_color);
		
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
				R.layout.layout_spinner, mALg);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
				R.layout.layout_spinner, mALg);
		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getActivity(),
				R.layout.layout_spinner, mALg);
		ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(getActivity(),
				R.layout.layout_spinner, mALg);
		
		sp_geometry.setAdapter(adapter1);	
		sp_surface.setAdapter(adapter2);
		sp_impurity.setAdapter(adapter3);		
		sp_color_diff.setAdapter(adapter4);

	}
}
