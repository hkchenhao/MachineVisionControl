package com.hanyu.hust.testnet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.SysDevPacket.*;
import com.hanyu.hust.testnet.ui.view.LayoutAD9849;


/**
 * Ad9849配置视图界面
 */
public class FragmentAd9849 extends BaseFragment{

	private LayoutAD9849 layoutAD9849;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fr_ad9849, container, false);
	}	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		layoutAD9849 = (LayoutAD9849) getActivity().findViewById(R.id.ll_ad9849);

		AD9849 ad9849 = Parameters.getInstance().ad9849;

		int count = 0;	
		for (int i = 0; i < 2; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				layoutAD9849.setSeekBarEditLayouts(i, j, ad9849.pageContents[count++]);
			}
		}
	}
}
