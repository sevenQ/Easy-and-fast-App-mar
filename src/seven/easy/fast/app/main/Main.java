/*
 * Copyright (C) 2011 The Seven Open Source Project
 *	
   Email:breakprisonsona@gmail.com
    Autthor:Seven
    Company:7Bench
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package seven.easy.fast.app.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import seven.easy.fast.app.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity implements OnItemClickListener {
	private static final String TAG = " Main ";
	private ListView lv;
	//private static List<PackageInfo> pi_list;
	private static List<AppBean> pi_listUser;
	private  HashMap<Integer, String> user_label;
	private  HashMap<Integer, String> user_package;
	private  HashMap<Integer, Drawable> user_icon;
	private  HashMap<String, Integer> user_times;
	
	//private Handler updateHandler;
	private Handler maxHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			lv.setAdapter(adapter);
			pd.dismiss();
		}
	};
	private Handler maxHandler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			adapter.notifyDataSetChanged();
		}
	};
	
	private ProgressDialog pd;
	private SharedPreferences sp;
	private BatteryReceiver br;
	private int listSize;
	private PackageManager pm;
	private BaseAdapter adapter = new ProgramList();
	//I18N
	private String list_title;
	private String list_content;
	private String time_unit;
	private String operate;
	private String cancel;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	/*	if (DEVELOPER_MODE) {
		
	         StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	                 .detectDiskReads()
	                 .detectDiskWrites()
	                 .detectNetwork()   // or .detectAll() for all detectable problems
	                 .penaltyLog()
	                 .build());
	         StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	                 .detectLeakedSqlLiteObjects()
	                 .detectLeakedClosableObjects()
	                 .penaltyLog()
	                 .penaltyDeath()
	                 .build());
	     }*/
		
		pm = getPackageManager();
		br = new BatteryReceiver();
		registerReceiver(br, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
				android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
				android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		sp = getSharedPreferences("data", MODE_PRIVATE);
		initList();
		//new Thread(labelHandler).start();
		new Thread(half_Handler).start();
		new Thread(remain_Handler).start();
	}

	private void initList() {
		
		list_title=getString(R.string.list_title);
		list_content=getString(R.string.list_content);
		time_unit=getString(R.string.time_unit);
		operate=getString(R.string.operate);
		cancel=getString(R.string.cancel);
		
		lv = (ListView) findViewById(R.id.lv_01);
		lv.setOnItemClickListener(this);
		pd = new ProgressDialog(this);
		pd.setTitle(list_title);
		pd.setMessage(list_content);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// updateHandler.post(progressHandler);
		// setProgressBarIndeterminateVisibility(true);
		pd.show();
		user_label = new HashMap<Integer, String>();
		user_package = new HashMap<Integer, String>();
		user_icon = new HashMap<Integer, Drawable>();
		user_times = new HashMap<String, Integer>();
		
		//initialize all user's Apps list
		List<PackageInfo> pi_list = this.getAllList();
		pi_listUser = new ArrayList<AppBean>();
		
		for (PackageInfo pi : pi_list) {
			ApplicationInfo ai = pi.applicationInfo;
			if (((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
					|| ((ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)) {
				// 别加自己 don't add itself.
				if (!pi.packageName.equals("seven.easy.fast.app")) {
					AppBean ab = new AppBean();
					//LogHelper.d(TAG + pi.activities[0]);
					if(pi.activities != null)
						ab.activityInfo = pi.activities[0];
					ab.packageName = pi.packageName;
					ab.applicationInfo = pi.applicationInfo;
					//LogHelper.d(TAG + " applicationInfo label : " + pm.getApplicationIcon(ab.applicationInfo).toString() );
					pi_listUser.add(ab);
				}
			}
		}
		
		listSize = pi_listUser.size();
		pd.setMax(listSize - 1);
	}


	private Runnable half_Handler = new Runnable() {

		@Override
		public void run() {
			//android.os.Process.setThreadPriority(android.os.Process.myTid(),-19);
			
			//LogHelper.d(TAG + " package and times start: " + System.currentTimeMillis() );
			for (int i = 0; i < listSize; i++) {
				String packageName = pi_listUser.get(i).packageName;
				user_package.put(i, packageName);
				user_times.put(packageName, sp.getInt(packageName, 1));
				//user_label.put(i, (String) pi_listUser.get(i).applicationInfo.loadLabel(pm));
				user_label.put(i, pm.getApplicationLabel(pi_listUser.get(i).applicationInfo).toString());
				pd.setProgress(2*i);
				if(i == (listSize/2 + 1)){
					maxHandler.sendEmptyMessage(0);
				}
			}
			//maxHandler.sendEmptyMessage(0);
			//LogHelper.d(TAG + " package and times end: " + System.currentTimeMillis() );
		}
	};
	
	private Runnable remain_Handler = new Runnable() {
		@Override
		public void run() {
			//android.os.Process.setThreadPriority(android.os.Process.myTid(),-15);
			
			//LogHelper.d(TAG + " icon start: " + System.currentTimeMillis() );
			for (int i = 0; i < listSize; i++) {
				String packageName = pi_listUser.get(i).packageName;
				try {
					user_icon.put(i, pm.getApplicationIcon(packageName));
					LogHelper.d(TAG + "icon := "+ pm.getApplicationIcon(packageName));
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
			//LogHelper.d(TAG + " icon end: " + System.currentTimeMillis() );
			maxHandler2.sendEmptyMessage(0);
		}
	};
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// Log.v("seven", "how many time on onItemClick");
		final AppBean ab = pi_listUser.get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(operate);
		builder.setItems(R.array.choseitem,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							//String packageName = ab.packageName;
							//PackageItemInfo ai = ab.applicationInfo;
							//String activityName = ab.activityInfo.name;
							if (ab.activityInfo == null) {
								MyToast.myToastShow(Main.this, R.drawable.emo,
										"No activity can be operated!",
										Toast.LENGTH_SHORT);
								return;
							}
							Intent intent = new Intent();
							intent.setComponent(new ComponentName(ab.packageName,
									ab.activityInfo.name));
							int times = sp.getInt(ab.packageName, 1);
							Editor editor = sp.edit();
							editor.putInt(ab.packageName, times + 1);
							editor.commit();
							startActivityForResult(intent, 1);
							//finish();
							break;
						case 1:
							Uri uri = Uri.parse("package:" + ab.packageName);
							Intent uninstallIntent = new Intent(
									Intent.ACTION_DELETE, uri);
							startActivityForResult(uninstallIntent, 0);
							break;
						}

					}
				});
		builder.setNegativeButton(cancel, null);
		builder.create().show();
	}

	class BatteryReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Main.this.setTitle(" " + context.getString(R.string.battery) + " "
					+ intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) + "%"
					+ " | " + context.getString(R.string.app) + " | "
					+ context.getString(R.string.times));
		}

	}

	@Override
	protected void onDestroy() {
		if (br != null) {
			this.unregisterReceiver(br);
			br = null;
		}
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// LogHelper.d(TAG + " deletePackageName " + "deletePackageName");

			 List<PackageInfo> pi_list = this.getAllList();
			// List<PackageInfo> pi_listUserAfterDelete = new
			// ArrayList<PackageInfo>();
			pi_listUser.clear();
			for (PackageInfo pi : pi_list) {
				ApplicationInfo ai = pi.applicationInfo;
				if (((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
						|| ((ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)) {
					// 别加自己
					if (!pi.packageName.endsWith("seven.easy.fast.app")) {
						AppBean ab = new AppBean();
						if(pi.activities != null)
							ab.activityInfo = pi.activities[0];
						ab.applicationInfo = pi.applicationInfo;
						ab.packageName = pi.packageName;
						pi_listUser.add(ab);
					}
				}
			}
			listSize = pi_listUser.size();
			for (int i = 0; i < listSize; i++) {
				//user_label.put(i, (String) pi_listUser.get(i).applicationInfo.loadLabel(pm));
				user_label.put(i, pm.getApplicationLabel(pi_listUser.get(i).applicationInfo).toString());
				String packageName = pi_listUser.get(i).packageName;
				user_package.put(i, packageName);
				user_times.put(packageName, sp.getInt(packageName, 1));
				try {
					user_icon.put(i, pm.getApplicationIcon(packageName));
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				//user_icon.put(i,pi_listUser.get(i).applicationInfo.loadIcon(pm));
			}
			adapter.notifyDataSetChanged();
			//lv.setAdapter(adapter);

			/**
			 * adjust/judge delete the AppS or no ! depend on list.size
			 */
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	protected static class AppBean{
		ActivityInfo activityInfo;
		ApplicationInfo applicationInfo;
		String packageName;
	}
	
	protected static class ViewHolder {
		TextView lv_item__appname;
		ScrollTextView lv_item__apppackage;
		ImageView lv_item_icon;
		TextView tv_times;
	}

	private class ProgramList extends BaseAdapter {

		@Override
		public int getCount() {
			return pi_listUser.size();
		}

		@Override
		public Object getItem(int position) {
			return pi_listUser.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {
				// Log.v("seven",
				// "how many times on getView when convertView == null");
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_view_items, null);
				holder = new ViewHolder();
				holder.lv_item__appname = (TextView) convertView.findViewById(R.id.lv_item__appname);
				holder.lv_item__apppackage = (ScrollTextView) convertView.findViewById(R.id.lv_item__apppackage);
				holder.lv_item_icon = (ImageView) convertView.findViewById(R.id.lv_item_icon);
				holder.tv_times = (TextView) convertView.findViewById(R.id.tv_times);
				convertView.setTag(holder);
			} else {
				// Log.v("seven",
				// "how many times on getView when convertView != null");
				holder = (ViewHolder) convertView.getTag();
			}
			holder.lv_item__appname.setText(user_label.get(position));
			holder.lv_item__apppackage.setText(user_package.get(position));
			holder.lv_item_icon.setImageDrawable(user_icon.get(position));
			holder.tv_times.setText(user_times.get(user_package.get(position))+ time_unit);
			//myPosition = position;
			return convertView;
		}
	}
	
	private final List<PackageInfo> getAllList(){
		return this.getPackageManager().getInstalledPackages(
				PackageManager.GET_UNINSTALLED_PACKAGES
				| PackageManager.GET_ACTIVITIES);
	}
}
