package com.hanyu.hust.testnet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hanyu.hust.testnet.R;

/**
 * 网卡设置界面
 */
public class FragmentNetCard extends BaseFragment {
	
	private int[] ip_address = new int[4];
	
	private int[] remote_ip = new int[4];

	private int[] mac = new int[6];

	private int port ;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fr_net_card, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ip_address[0] = sp.getInt("net_ip_address1_" + CameraID, 0);
		ip_address[1] = sp.getInt("net_ip_address2_" + CameraID, 0);
		ip_address[2] = sp.getInt("net_ip_address3_" + CameraID, 0);
		ip_address[3] = sp.getInt("net_ip_address4_" + CameraID, 0);
		
		remote_ip[0] = sp.getInt("net_remote_ip1_" + CameraID, 0);
		remote_ip[1] = sp.getInt("net_remote_ip2_" + CameraID, 0);
		remote_ip[2] = sp.getInt("net_remote_ip3_" + CameraID, 0);
		remote_ip[3] = sp.getInt("net_remote_ip4_" + CameraID, 0);
		
		mac[0] = sp.getInt("net_mac1_" + CameraID, 0);
		mac[1] = sp.getInt("net_mac2_" + CameraID, 0);
		mac[2] = sp.getInt("net_mac3_" + CameraID, 0);
		mac[3] = sp.getInt("net_mac4_" + CameraID, 0);
		mac[4] = sp.getInt("net_mac5_" + CameraID, 0);
		mac[0] = sp.getInt("net_mac1_" + CameraID, 0);
		
		port = sp.getInt("net_port_" + CameraID, 0);
		
		((EditText)getActivity().findViewById(R.id.et_local_ip)).setText(ip_address[0] + "." + ip_address[1]
				+ "." + ip_address[2] + "." + ip_address[3]);
		((EditText)getActivity().findViewById(R.id.et_remote_ip)).setText(remote_ip[0] + "." + remote_ip[1] + "."
				+ remote_ip[2] + "." + remote_ip[3]);
		((EditText)getActivity().findViewById(R.id.et_mac)).setText(mac[0] + ":" + mac[1]
				+ ":" + mac[2] + ":" + mac[3] + ":"
				+ mac[4] + ":" + mac[5]);
		((EditText)getActivity().findViewById(R.id.et_tcp_port)).setText(port + "");
	}
}
