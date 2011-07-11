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

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast {
	public static void myToastShow(Context context, int imageID,
			String content, int duration) {
		Toast toast = new Toast(context);
		toast.setDuration(duration);
		toast.setGravity(Gravity.BOTTOM, 0, 10);
		LinearLayout toastLinearLayout = new LinearLayout(context);
		toastLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		toastLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
		ImageView toastImageView = new ImageView(context);
		toastImageView.setImageResource(imageID);
		TextView toastTextView = new TextView(context);
		toastTextView.setText(content);
		toastTextView.setBackgroundColor(Color.BLACK);
		toastLinearLayout.addView(toastImageView);
		toastLinearLayout.addView(toastTextView);
		toast.setView(toastLinearLayout);
		toast.show();
	}
}
