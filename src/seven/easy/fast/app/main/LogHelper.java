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

import android.util.Log;

public class LogHelper {
	
	private static String TAG = "seven";
	private static boolean flag = true;
	
	public static int d(String msg){
		if(flag){
			return Log.d(TAG, msg);
		}else{
			return -1;
		}
		
	}
	
	public static int e(String msg ,Throwable e){
		if(flag){
		return Log.e(TAG, msg, e);
		}else{
			return -1;
		}
	}
}
