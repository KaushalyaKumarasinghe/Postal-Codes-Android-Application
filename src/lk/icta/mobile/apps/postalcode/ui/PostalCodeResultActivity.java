/**
* @copyright	Copyright (C) 2010 - 2010 Kaushalya Kumarasinghe
* @license		GNU/GPL
* This Application is released on behalf of ICTA (Information and Communication Technology Agency of Sri Lanka)
* to the public under the GNU General Public License
*/

package lk.icta.mobile.apps.postalcode.ui;

import java.net.ConnectException;

import lk.icta.mobile.apps.postalcode.R;

import lk.icta.mobile.apps.postalcode.obj.PostOfficesByPostalCode;
import lk.icta.mobile.apps.postalcode.obj.PostalCodesByPostOffice;
import lk.icta.mobile.apps.postalcode.util.Functions;
import lk.icta.mobile.apps.postalcode.webservice.PostalCodeWebService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PostalCodeResultActivity extends Activity {
	
	/* Declaring GUI components */
	TextView noInfoResultText;
	TextView resultTitle; 
	TextView postOfficeNameResultTxt;
	TextView postOfficeNameTxt;
	TextView postalCodeResultTxt;
	TextView postalCodeTxt;
	TextView telephoneNumResultTxt;
	TextView telephoneNumTxt;
	TextView faxNumResultTxt;
	TextView faxNumTxt;
	LinearLayout resultLayout;

	/* Variable to pass the name of the resulted post office */
	String postOfficeResult;
	/* Variable to pass the name of the resulted postal code */
	String postalCodeResult;
	/* Variable to pass the name of the resulted telephone number */
	String telephoneNumResult;
	/* Variable to pass the name of the resulted fax number */
	String faxNumResult;	
	/* Variable to pass the selected divisionId from divisionSpinner */
	String selectedDivisionId;
	/* Variable to pass the selected post office from postOfficeSpinner */
	String selectedPostOffice;
	/* Variable to pass the postal code input */
	String postalCodeInput;	
	/* The boolean variable which is set to true if and only if the user search by the post office name */
	boolean searchByPostOffice;
	/* Variable to store number of results */
	public static int resultCount;	
	
	/* Handler to process getPostalCodeByPostOffice message */
	Handler getPostalCodeResultHandler;
	/* Handler to process getPostOfficeByPostalCode message */
	Handler getPostOfficeResultHandler;
	
	/* Declaring progress dialog */
	ProgressDialog progressDialog;
	
	/* Declaring alert dialog to indicate if it is not possible to connect with the web service*/
	AlertDialog connectionFailedAlert;
	
	
	PostalCodesByPostOffice postalCodesByPostOffice;
	PostOfficesByPostalCode postOfficesByPostalCode;


	public boolean isSearchByPostOffice() {
		return searchByPostOffice;
	}

	public void setSearchByPostOffice(boolean searchByPostOffice) {
		this.searchByPostOffice = searchByPostOffice;
	}
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_postalcode_result);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		/* Initializing GUI components to display results */
		resultTitle = (TextView) findViewById(R.id.result_title_label);
		postOfficeNameResultTxt = (TextView) findViewById(R.id.post_office_label);
		postalCodeResultTxt = (TextView) findViewById(R.id.postal_code_label);
		telephoneNumResultTxt = (TextView) findViewById(R.id.tp_number_label);
		faxNumResultTxt = (TextView) findViewById(R.id.fax_number_label);
		
		postOfficeNameTxt = (TextView) findViewById(R.id.post_office_result_label);
		postalCodeTxt = (TextView) findViewById(R.id.postal_code_reuslt_label);
		telephoneNumTxt = (TextView) findViewById(R.id.tp_number_result_label);
		faxNumTxt = (TextView) findViewById(R.id.fax_number_result_label);

		selectedDivisionId = getIntent().getExtras().getString("divisionId");
		selectedPostOffice = getIntent().getExtras().getString("postOfficeNameEn");
		postalCodeInput = getIntent().getExtras().getString("postalCode");
		searchByPostOffice = getIntent().getExtras().getBoolean("searchByPostOffice");
		
		
		/* Setting GUI components invisible at the begining */
		resultTitle.setVisibility(View.INVISIBLE);
		postOfficeNameTxt.setVisibility(View.INVISIBLE);
		postOfficeNameResultTxt.setVisibility(View.INVISIBLE);
		postalCodeTxt.setVisibility(View.INVISIBLE);
		postalCodeResultTxt.setVisibility(View.INVISIBLE);
		telephoneNumTxt.setVisibility(View.INVISIBLE);
		telephoneNumResultTxt.setVisibility(View.INVISIBLE);
		faxNumTxt.setVisibility(View.INVISIBLE);	
		faxNumResultTxt.setVisibility(View.INVISIBLE);
		
		progressDialog = Functions.getProgressDialog(this, "Please wait...");

		if (isSearchByPostOffice()) {
			
			/* Calls the web service to get postal code by post office name and division id */
			new WSGetPostalCodeByPostOffice().execute(null, null, null);

			getPostalCodeResultHandler = new Handler() {

				public void handleMessage(Message message) {
					
					/*
					 *  If the response message is not an empty message display the output. Otherwise display 
					 * alert dialog which contains the error message.
					 *  */
					if (message.what != -1) {
						resultCount = postalCodesByPostOffice.getCount();
						
						/*
						 *  If no information available set GUI components invisible and display information 
						 * not available toast message. Otherwise display the post office information.
						 *  */
						if (postalCodesByPostOffice.getCount() <= 0) {
							
							resultTitle.setVisibility(View.INVISIBLE);
							postOfficeNameTxt.setVisibility(View.INVISIBLE);
							postOfficeNameResultTxt
									.setVisibility(View.INVISIBLE);
							postalCodeTxt.setVisibility(View.INVISIBLE);
							postalCodeResultTxt.setVisibility(View.INVISIBLE);
							telephoneNumTxt.setVisibility(View.INVISIBLE);
							telephoneNumResultTxt.setVisibility(View.INVISIBLE);
							faxNumTxt.setVisibility(View.INVISIBLE);
							faxNumResultTxt.setVisibility(View.INVISIBLE);

							Toast noInfomationToastMesage = Toast.makeText(PostalCodeResultActivity.this, "No information available for the postal code : " + postalCodeInput, Toast.LENGTH_LONG);
							noInfomationToastMesage.show();
							
							progressDialog.dismiss();

							return;
						} else {
							
							/* If information available set GUI components to visible */
							resultTitle.setVisibility(View.VISIBLE);
							postOfficeNameTxt.setVisibility(View.VISIBLE);
							postOfficeNameResultTxt.setVisibility(View.VISIBLE);
							postalCodeTxt.setVisibility(View.VISIBLE);
							postalCodeResultTxt.setVisibility(View.VISIBLE);
							telephoneNumTxt.setVisibility(View.VISIBLE);
							telephoneNumResultTxt.setVisibility(View.VISIBLE);
							faxNumTxt.setVisibility(View.VISIBLE);
							faxNumResultTxt.setVisibility(View.VISIBLE);

							String postOfficeName = postalCodesByPostOffice
									.getPostOffices()[0];
							String postalCode = postalCodesByPostOffice
									.getPostalCodes()[0];
							String tpNumber = postalCodesByPostOffice
									.getTelephoneNumbers()[0];
							String faxNumber = postalCodesByPostOffice
									.getFaxNumbers()[0];

							if (!postOfficeName.equals("anyType{}")) {
								postOfficeNameTxt.setText(postOfficeName);
							} else {

								postOfficeNameTxt.setText(null);
							}
							if (!postalCode.equals("anyType{}")) {
								postalCodeTxt.setText(postalCode);
								Log.v("postalCode", postalCode);
							} else {

								postalCodeTxt.setText(" - ");
								Log.v("postalCode", postalCode);
							}
							if (!tpNumber.equals("anyType{}")) {
								telephoneNumTxt.setText(tpNumber);
							} else {

								telephoneNumTxt.setText(" - ");
							}
							if (!faxNumber.equals("anyType{}")) {
								faxNumTxt.setText(faxNumber);
								Log.v("faxNumber", faxNumber);
							} else {

								faxNumTxt.setText(" - ");
								Log.v("faxNumber", faxNumber);
							}
						}
						progressDialog.dismiss();
						
					} else {
						
						/* If an empty response message is retrieved, displays connection failed alert message */
						connectionFailedAlert = Functions.getConnectionFailedAlert(PostalCodeResultActivity.this, "Sorry. \nUnable to provide the service this time. Please try again latter.");
					}
				}
			};
			
			
		} else {

			/* Calls the web service to get the Post Office information by postal code */
			new WSGetPostOfficeByPostalCode().execute(null, null, null);

			getPostOfficeResultHandler = new Handler() {

				public void handleMessage(Message message) {
					
					/* If the response message is not an empty message display the output. Otherwise display 
					 * alert dialog which contains the error message.
					 *  */
					if (message.what != -1) {
						resultCount = postOfficesByPostalCode.getCount();
						
						/*
						 *  If no information available set GUI components invisible and display information 
						 * not available toast message. Otherwise display the post office information.
						 *  */
						if (postOfficesByPostalCode.getCount() <= 0) {

							resultTitle.setVisibility(View.INVISIBLE);
							postOfficeNameTxt.setVisibility(View.INVISIBLE);
							postOfficeNameResultTxt
									.setVisibility(View.INVISIBLE);
							postalCodeTxt.setVisibility(View.INVISIBLE);
							postalCodeResultTxt.setVisibility(View.INVISIBLE);
							telephoneNumTxt.setVisibility(View.INVISIBLE);
							telephoneNumResultTxt.setVisibility(View.INVISIBLE);
							faxNumTxt.setVisibility(View.INVISIBLE);
							faxNumResultTxt.setVisibility(View.INVISIBLE);

							Toast noInfomationToastMesage = Toast.makeText(PostalCodeResultActivity.this, "No information available for the postal code : " + postalCodeInput, Toast.LENGTH_LONG);
							noInfomationToastMesage.show();
							progressDialog.dismiss();

						} else {
							
							/* If information available set GUI components to visible */
							resultTitle.setVisibility(View.VISIBLE);
							postOfficeNameTxt.setVisibility(View.VISIBLE);
							postOfficeNameResultTxt.setVisibility(View.VISIBLE);
							postalCodeTxt.setVisibility(View.VISIBLE);
							postalCodeResultTxt.setVisibility(View.VISIBLE);
							telephoneNumTxt.setVisibility(View.VISIBLE);
							telephoneNumResultTxt.setVisibility(View.VISIBLE);
							faxNumTxt.setVisibility(View.VISIBLE);
							faxNumResultTxt.setVisibility(View.VISIBLE);

							String postOfficeName = postOfficesByPostalCode
									.getPostOffices()[0];
							String postalCode = postOfficesByPostalCode
									.getPostalCodes()[0];
							String tpNumber = postOfficesByPostalCode
									.getTelephoneNumbers()[0];
							String faxNumber = postOfficesByPostalCode
									.getFaxNumbers()[0];

							if (!postOfficeName.equals("anyType{}")) {
								postOfficeNameTxt.setText(postOfficeName);
							} else {

								postOfficeNameTxt.setText(null);
							}
							
							if (!postalCode.equals("anyType{}")) {
								postalCodeTxt.setText(postalCode);
								Log.v("postalCode", postalCode);
							} else {

								postalCodeTxt.setText(" - ");
							}
							if (!tpNumber.equals("anyType{}")) {
								telephoneNumTxt.setText(tpNumber);
							} else {

								telephoneNumTxt.setText(" - ");
							}
							if (!faxNumber.equals("anyType{}")) {
								faxNumTxt.setText(faxNumber);
							} else {

								faxNumTxt.setText(" - ");
							}
						}
						progressDialog.dismiss();
						
					} else {
						
						/* If an empty response message is retrieved, displays connection failed alert message */
						connectionFailedAlert = Functions.getConnectionFailedAlert(PostalCodeResultActivity.this, "Sorry. \nUnable to provide the service this time. Please try again latter.");
						getParent().finish();
					} 
				}
				
			}; 
			
		}
		
	}

	public void onBackPressed() {		
		super.onBackPressed();
		
		finish();
	}
	
	/* 
	 * Asynchronous back ground process to retrieve the post office information by Post office name. If an exception occured
	 * sends and empty message to the web service client
	 * 
	 *  */
	class WSGetPostalCodeByPostOffice extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			try {
				postalCodesByPostOffice = PostalCodeWebService
						.getPostalCodeByPostOffice(
								Integer.parseInt(selectedDivisionId),
								selectedPostOffice);
				getPostalCodeResultHandler
						.sendMessage(getPostalCodeResultHandler.obtainMessage());

				return null;
			} catch (ConnectException e) {
				// TODO: handle exception
				// Send an empty message if an ConnectException occurred
				getPostalCodeResultHandler.sendEmptyMessage(-1);
				progressDialog.dismiss();
				Log.i("ConnectException", "ConnectException occured");
				e.printStackTrace();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				// Send an empty message if an exception occurred
				getPostalCodeResultHandler.sendEmptyMessage(-1);
				progressDialog.dismiss();
				Log.i("Exception", "Exception occured");
				e.printStackTrace();
			}
			return null;
		}
	}

	/* 
	 * Asynchronous back ground process to retrieve the post office information by postal code. If an exception occured
	 * sends and empty message to the web service client
	 *  
	 *  */
	class WSGetPostOfficeByPostalCode extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			try {
				postOfficesByPostalCode = PostalCodeWebService
						.getPostOfficeByPostalCode(postalCodeInput);
				getPostOfficeResultHandler
						.sendMessage(getPostOfficeResultHandler.obtainMessage());

				return null;
			} catch (ConnectException e) {
				// TODO: handle exception
				// Send an empty message if a ConnectException occurred
				getPostOfficeResultHandler.sendEmptyMessage(-1);
				progressDialog.dismiss();
				Log.i("ConnectException", "ConnectException occured");
				e.printStackTrace();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				// Send an empty message if an exception occurred
				getPostOfficeResultHandler.sendEmptyMessage(-1);
				progressDialog.dismiss();
				Log.i("Exception", "Exception occured");
				e.printStackTrace();
			}
			return null;
		}
	}

}