package com.hanyu.hust.testnet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.emercy.dropdownlist.DropDownList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.SysDevPacket.Mode;
import com.hanyu.hust.testnet.entity.SysDevPacket.Parameters;
import com.hanyu.hust.testnet.entity.SysDevPacket.Sensor;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.ui.view.NumberSettingLayout;
import com.hanyu.hust.testnet.ui.view.SeekBarEditLayout;

/**
 * 相机基本信息的配置类
 */
public class FragmentGeneralInfo extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fr_general_info, container, false);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		Sensor sensor = Parameters.getInstance().sensor;
		Mode mode = Parameters.getInstance().mode;

		// 以下两句设置下拉菜单的内容
		((DropDownList) getActivity().findViewById(
				R.id.device_setting_input_type))
				.setItem(R.array.device_setting_input_type);
		((DropDownList) getActivity().findViewById(
				R.id.device_setting_output_type))
				.setItem(R.array.device_setting_output_type);
		((SeekBarEditLayout) getActivity().findViewById(
				R.id.device_setting_exposure)).setMax(65536);
		((NumberSettingLayout) getActivity().findViewById(
				R.id.device_setting_start_x)).setValue(sensor.startPixel_width);
		((NumberSettingLayout) getActivity().findViewById(
				R.id.device_setting_start_y))
				.setValue(sensor.startPixel_height);
		((NumberSettingLayout) getActivity().findViewById(
				R.id.device_setting_input_w)).setValue(sensor.width_input);
		((NumberSettingLayout) getActivity().findViewById(
				R.id.device_setting_input_h)).setValue(sensor.height_input);

		((SeekBarEditLayout) getActivity().findViewById(
				R.id.device_setting_exposure)).setValue(mode.expoTime);

		if (mode.bitType == 8)
			((RadioButton) getActivity().findViewById(
					R.id.device_setting_bit_radio0)).setSelected(true);
		else
			((RadioButton) getActivity().findViewById(
					R.id.device_setting_bit_radio1)).setSelected(true);

		if (mode.trigger == 0)
			((CheckBox) getActivity().findViewById(
					R.id.device_setting_mode_checkbox0)).setChecked(true);
		else if (mode.trigger == 1)
			((CheckBox) getActivity().findViewById(
					R.id.device_setting_mode_checkbox1)).setChecked(true);
		else
			((CheckBox) getActivity().findViewById(
					R.id.device_setting_mode_checkbox2)).setChecked(true);
	}

	/**
	 * 发送相机配置信息
	 * @param cmdHandle
	 */
	void sendGenenalInfo(CmdHandle cmdHandle)
	{
		final JsonObject json = new JsonObject();
		final Gson gson = new Gson();
		
		Sensor sensor = Parameters.getInstance().sensor;
		Mode mode = Parameters.getInstance().mode;

		sensor.startPixel_width = ((NumberSettingLayout) getActivity()
				.findViewById(R.id.device_setting_start_x))
				.getValue();
		sensor.startPixel_height = ((NumberSettingLayout) getActivity()
				.findViewById(R.id.device_setting_start_y))
				.getValue();
		sensor.width_input = ((NumberSettingLayout) getActivity()
				.findViewById(R.id.device_setting_input_w))
				.getValue();
		sensor.height_input = ((NumberSettingLayout) getActivity()
				.findViewById(R.id.device_setting_input_h))
				.getValue();

		mode.expoTime = ((SeekBarEditLayout) getActivity()
				.findViewById(R.id.device_setting_exposure))
				.getValue();
		mode.bitType = ((RadioButton) getActivity()
				.findViewById(R.id.device_setting_bit_radio0))
				.isChecked() ? 8 : 16;

		if (((CheckBox) getActivity()
				.findViewById(R.id.device_setting_mode_checkbox0))
				.isChecked())
		{
			mode.trigger = 0;
		}
		else if (((CheckBox) getActivity()
				.findViewById(R.id.device_setting_mode_checkbox1))
				.isChecked())
		{
			mode.trigger = 1;
		}
		else
		{
			mode.trigger = 2;
		}

		json.add("mode", gson.toJsonTree(mode));
		JsonObject jsonSensor = new JsonObject();
		jsonSensor.add("sensor", gson.toJsonTree(sensor));
//		cmdHandle.setJson(json.toString().getBytes());
//		cmdHandle.setJson(json.toString().getBytes());
	}
		
	@Override
	protected void OnApply() {
		super.OnApply();	
		switch (getCameraID()) {
		case 1:
//			if (CmdHandle1.getInstance() == null)
//				EToast.showToast(getActivity(), "相机1未连接");
//			else
//			{
//				sendGenenalInfo(CmdHandle1.getInstance());
//				EToast.showToast(getActivity(), "相机1发送成功");
//			}
			break;
		case 2:
//			if (CmdHandle2.getInstance() == null)
//				EToast.showToast(getActivity(), "相机2未连接");
//			else
//			{
//				sendGenenalInfo(CmdHandle2.getInstance());
//				EToast.showToast(getActivity(), "相机2发送成功");
//			}
			break;
		default:
			break;
		}
	}	
}
