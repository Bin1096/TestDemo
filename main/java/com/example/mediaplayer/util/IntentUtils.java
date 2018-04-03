package com.example.mediaplayer.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.mediaplayer.R;


public class IntentUtils {

	/**
	 * Activity之间跳转
	 * 
	 * @param activity
	 * @param cls
	 */
	public static void startActivity(Activity activity, Class<?> cls) {
		startActivity(activity, cls, null);
	}

	/**
	 * Activity之间跳转
	 * 
	 * @param context
	 * @param cls
	 */
	public static void startActivity(Context context, Class<?> cls) {
		startActivity(context, cls, null);
	}

	/**
	 * Activity之间跳转
	 * 
	 * @param context
	 * @param cls
	 * @param extras
	 */
	public static void startActivity(Context context, Class<?> cls,
									 Bundle extras) {
		startActivity(context, cls, extras, true);
	}

	/**
	 * Activity之间跳转
	 * 
	 * @param context
	 * @param cls
	 * @param extras
	 */
	public static void startActivity(Context context, Class<?> cls,
									 Bundle extras, boolean anim) {
//		Intent intent = new Intent();
//		intent.setClass(activity, cls);
//
//		if (extras != null) {
//			intent.putExtras(extras);
//		}
//
//		activity.startActivity(intent);
//
//		if(anim) {
//			activity.overridePendingTransition(R.anim.anim_activity_enter_right,
//					R.anim.anim_activity_exit_left);
//		}
		startActivityForResult((Activity) context, cls, extras, 0, true);
	}

	public static void startActivityForResult(Activity activity, Class<?> cls, Bundle extras, int requestCode, boolean anim){
		Intent intent = new Intent();
		intent.setClass(activity, cls);

		if (extras != null) {
			intent.putExtras(extras);
		}

		activity.startActivityForResult(intent, requestCode);

		if(anim) {
			activity.overridePendingTransition(R.anim.anim_activity_enter_right,
					R.anim.anim_activity_exit_left);
		}
	}


	/**
	 * 获取安装APK的Intent
	 * 
	 * @param uri
	 * @return
	 */
	public static Intent getInstallAPKIntent(Uri uri) {
		Intent installIntent = new Intent(Intent.ACTION_VIEW);
		installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 新开辟一个栈
		installIntent.setDataAndType(uri,
				"application/vnd.android.package-archive");
		return installIntent;
	}

	/**
	 * 获取无线网络设置
	 * 
	 * @return
	 */
	public static Intent getVPNSettingIntent() {
		Intent intent = new Intent(
				android.provider.Settings.ACTION_WIRELESS_SETTINGS);
		return intent;
	}
}
