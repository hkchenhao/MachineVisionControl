package com.hanyu.hust.testnet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.SysDevPacket.*;

/**
 * 本地网卡配置类
 */
public class FragmentLocalNet extends BaseFragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fr_local_net, container, false);
	}	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Net net = Parameters.getInstance().net;
		((EditText)getActivity().findViewById(R.id.et_local_ip1)).setText(net.ip_address[0] + "." + net.ip_address[1]
				+ "." + net.ip_address[2] + "." + net.ip_address[3]);
		((EditText)getActivity().findViewById(R.id.et_tcp_port1)).setText(net.port + "");	
	}
	
	
	void choose(int position)
	{
		int tcpPort = sp.getInt("net_port_" + (position+1), 0);
		((EditText)getActivity().findViewById(R.id.et_tcp_port1)).setText(tcpPort+"");	
	}

	@Override
	public void onChoose(int position) {
		super.onChoose(position);
		choose(position);
	}
}
