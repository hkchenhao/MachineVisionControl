package com.hanyu.hust.testnet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.hanyu.hust.testnet.R;

/**
 * Can 总线设置
 */
public class FragmentCan extends BaseFragment {

	private static final String[] mBaudrates = { "100kbps", "125kbps", "250kbps", "500kbps","1Mbps"};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fr_can, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Spinner sp_can_baud = (Spinner) getActivity().findViewById(R.id.sp_can_baud);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.layout_spinner, mBaudrates);
		sp_can_baud.setAdapter(adapter);
	}
}
