package com.hanyu.hust.testnet.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hanyu.hust.testnet.R;

/**
 * 自定义Toast弹窗样式
 * @author M
 *
 */
public class EToast extends Toast
{		
	public EToast(Context context)
	{
		super(context);
	}

	/**
	 * Make a standard toast that just contains a text view.
	 * 
	 * @param context
	 *            The context to use. Usually your
	 *            {@link android.app.Application} or
	 *            {@link android.app.Activity} object.
	 * @param text
	 *            The text to show. Can be formatted text.
	 * @param duration
	 *            How long to display the message. Either {@link #LENGTH_SHORT}
	 *            or {@link #LENGTH_LONG}
	 * 
	 */
	
	
	public static Toast makeText(Context context, CharSequence text,
                                 int duration)
	{
		Toast result = new Toast(context);

		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflate.inflate(R.layout.etoast, null);
		TextView tv = (TextView) v.findViewById(R.id.etoast);
		tv.setText(text);

		result.setDuration(duration);
		result.setView(v);

		return result;
	}
	
	public static void showToast(Context context, CharSequence text)
	{
		EToast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}		
}
