package com.hanyu.hust.testnet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.SysDevPacket.Parameters;
import com.hanyu.hust.testnet.entity.SysDevPacket.Trigger;

/**
 * 触发模式参数配置类
 */
public class FragmentTrigPara extends BaseFragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fr_trig_para, container, false);
	}	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Trigger trigger = Parameters.getInstance().trigger;
		
		((EditText) getActivity()
				.findViewById(R.id.device_setting_trigger_delay))
				.setText(trigger.trigDelay + "");

		((EditText) getActivity()
				.findViewById(R.id.device_setting_trigger_part_delay))
				.setText(trigger.partDelay + "");

		((EditText) getActivity()
				.findViewById(R.id.device_setting_trigger_velocity))
				.setText(trigger.velocity + "");

		((EditText) getActivity()
				.findViewById(R.id.device_setting_trigger_depart_wide))
				.setText(trigger.departWide + "");

		((EditText) getActivity().findViewById(R.id.device_setting_trigger_explead))
				.setText(trigger.expLead + "");
	}
	
	
	
}
