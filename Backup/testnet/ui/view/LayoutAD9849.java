package com.hanyu.hust.testnet.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LayoutAD9849 extends LinearLayout {

	private Context context;
	
	private final SeekBarEditLayout[][] seekBarEditLayouts;

	public LayoutAD9849(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		seekBarEditLayouts = new SeekBarEditLayout[2][8];
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(getView(), params);
	}
	
	View getView()
	{
		int count = 0;
		String[][] items =
		{
				{ "VGA", "SHP", "HPL", "RGPL", "P0GA", "P1GA", "P2GA",
						"P3GA" },
				{ "RGDRV", "SHD", "HNL", "RGNL", "H1DRV", "H2DRV", "H3DRV",
						"H4DRV" } };
	
		final LinearLayout viewAd9849 = new LinearLayout(context);
		
		viewAd9849.setOrientation(LinearLayout.HORIZONTAL);
		viewAd9849.setPadding(0, 30, 20, 0);
		LinearLayout layoutLeft = new LinearLayout(context);
		layoutLeft.setOrientation(LinearLayout.VERTICAL);
		LinearLayout layoutRight = new LinearLayout(context);
		layoutRight.setOrientation(LinearLayout.VERTICAL);
	
		LayoutParams paramsLeft = new LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 1);
		LayoutParams paramsRight = new LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 1);
		paramsRight.leftMargin = 28;

		/**
		 * 动态生成布局
		 */
		for (String item : items[0])
		{
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.HORIZONTAL);
	
			LayoutParams paramsInner = new LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1);
			paramsInner.rightMargin = 8;
			TextView textView = new TextView(context);
			textView.setLayoutParams(paramsInner);
			textView.setText(item);
			textView.setGravity(Gravity.END);
	
			seekBarEditLayouts[0][count] = new SeekBarEditLayout(context);
			paramsInner = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 4);
			seekBarEditLayouts[0][count].setLayoutParams(paramsInner);
//			seekBarEditLayouts[0][count]
//					.setValue(ad9849.pageContents[count]);
	
			layout.addView(textView);
			layout.addView(seekBarEditLayouts[0][count++]);
	
			layoutLeft.addView(layout);
		}
		count = 0;
		for (String item : items[1])
		{
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.HORIZONTAL);
	
			LayoutParams paramsInner = new LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1);
			paramsInner.rightMargin = 8;
			TextView textView = new TextView(context);
			textView.setLayoutParams(paramsInner);
			textView.setText(item);
			textView.setGravity(Gravity.END);
	
			seekBarEditLayouts[1][count] = new SeekBarEditLayout(context);
			paramsInner = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 4);
			seekBarEditLayouts[1][count].setLayoutParams(paramsInner);
//			seekBarEditLayouts[1][count]
//					.setValue(ad9849.pageContents[count]);
	
			layout.addView(textView);
			layout.addView(seekBarEditLayouts[1][count++]);
			layoutRight.addView(layout);
		}
	
		viewAd9849.addView(layoutLeft, paramsLeft);
		viewAd9849.addView(layoutRight, paramsRight);
		viewAd9849.setGravity(Gravity.CENTER);
		return viewAd9849;
	}
	
	public void setSeekBarEditLayouts(int i, int j, int value) 
	{
		seekBarEditLayouts[i][j].setValue(value);
	}
	
}
