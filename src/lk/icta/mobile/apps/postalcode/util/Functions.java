/**
* @copyright	Copyright (C) 2010 - 2010 Kaushalya Kumarasinghe
* @license		GNU/GPL
* This Application is released on behalf of ICTA (Information and Communication Technology Agency of Sri Lanka)
* to the public under the GNU General Public License
*/

package lk.icta.mobile.apps.postalcode.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

public class Functions {
	
	public static ProgressDialog getProgressDialog(Activity activity, String message){
		ProgressDialog progressDialog;
		progressDialog = new ProgressDialog(activity);				
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(message);
		progressDialog.show();
		return progressDialog;
	}

	public static AlertDialog getConnectionFailedAlert(final Activity activity, String message) {
		
		AlertDialog connectionFailedAlert = new AlertDialog.Builder(activity).create();
		connectionFailedAlert.setTitle("Connection Failed");
		connectionFailedAlert.setMessage(message);
		connectionFailedAlert.setButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				activity.finish();
			}
		});
		
		connectionFailedAlert.show();
		return connectionFailedAlert;
		
	}
	
	public static AlertDialog getUserInputValidateAlert(final Activity activity, String message, String title) {
		
		AlertDialog userInputValidateAlert = new AlertDialog.Builder(activity).create();
		userInputValidateAlert.setTitle(title);
		userInputValidateAlert.setMessage(message);
		userInputValidateAlert.setButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		userInputValidateAlert.show();
		return userInputValidateAlert;
		
	}

}