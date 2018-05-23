package com.hanyu.hust.testnet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.SysDevPacket.Parameters;
import com.hanyu.hust.testnet.entity.SysDevPacket.UART;

/**
 * 串口配置界面类
 * 目前新版本未用到串口，已经废弃
 */
public class FragmentUart extends BaseFragment {

	private static final String[] mBaudrates = { "9600", "19200", "38400", "115200"};

	private static final String[] mCheckMode = { "无校验", "奇校验", "偶校验"};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fr_uart, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Spinner sp_uart_baud = (Spinner) getActivity().findViewById(R.id.sp_uart_baud);
		Spinner sp_check_mode = (Spinner) getActivity().findViewById(R.id.sp_check_mode);

		ArrayAdapter<String> adapterBaud = new ArrayAdapter<String>(getActivity(),
				R.layout.layout_spinner, mBaudrates);
		ArrayAdapter<String> adapterMode = new ArrayAdapter<String>(getActivity(),
				R.layout.layout_spinner, mCheckMode);

		sp_uart_baud.setAdapter(adapterBaud);
		sp_check_mode.setAdapter(adapterMode);

		UART uart = Parameters.getInstance().uart;
		Spinner parity = (Spinner) getActivity().findViewById(R.id.parity);
		Spinner stop_bit = (Spinner) getActivity().findViewById(R.id.stop_bit);
		Spinner data_len = (Spinner) getActivity().findViewById(R.id.data_len);
	}
	
	
	@Override
	protected void OnApply() {
		super.OnApply();
		Gson gson = new Gson();
		JsonObject json = new JsonObject();
		UART uart = Parameters.getInstance().uart;
//		uart.baudRate = Integer.parseInt(value[0]);
//		uart.work_mode |= (Integer.parseInt(value[1]) & 0x03);
//		json.add("uart", gson.toJsonTree(uart));		
	}
}
