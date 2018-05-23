package com.hanyu.hust.testnet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.SysDevPacket.*;

/**
 * Ad25040配置界面
 */
public class FragmentAt25040 extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fr_at25040, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Version version = Parameters.getInstance().version;

		((EditText) getActivity().findViewById(R.id.camera_id)).setText(version.id[0]
				+ "." + version.id[1] + "." + version.id[2] + "."
				+ version.id[3]);
		((EditText) getActivity().findViewById(R.id.version)).setText(version.version
				+ "");

		((EditText) getActivity().findViewById(R.id.write_time))
				.setText(version.write_time[0] + "" + version.write_time[1]
						+ "." + version.write_time[2] + "."
						+ version.write_time[3]);
	}
}
