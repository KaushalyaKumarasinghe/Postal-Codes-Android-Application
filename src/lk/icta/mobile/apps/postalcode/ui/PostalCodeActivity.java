/**
 * @copyright	Copyright (C) 2010 - 2010 Kaushalya Kumarasinghe
 * @license		GNU/GPL
 * This Application is released on behalf of ICTA (Information and Communication Technology Agency of Sri Lanka)
 * to the public under the GNU General Public License
 */

package lk.icta.mobile.apps.postalcode.ui;

import java.net.ConnectException;
import lk.icta.mobile.apps.postalcode.R;
import lk.icta.mobile.apps.postalcode.obj.Divisions;
import lk.icta.mobile.apps.postalcode.obj.PostOffices;
import lk.icta.mobile.apps.postalcode.obj.PostalCodesByPostOffice;
import lk.icta.mobile.apps.postalcode.util.Functions;
import lk.icta.mobile.apps.postalcode.webservice.PostalCodeWebService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class PostalCodeActivity extends Activity {

	/* Declaring GUI components */
	Spinner divisionsSpinner;
	Spinner postOfficeSpinner;
	EditText postalCodeEditText;
	Handler handler;
	Handler postOfficeHandler;
	Handler postalCodeHandler;

	/* Variable to store division information by division id */
	Divisions divisions;

	/*
	 * Variable to store post office information by post office name & division
	 * id
	 */
	PostOffices postOffices;

	/* Variable to store post office information by postal code */
	PostalCodesByPostOffice postalCodesByPostOffice;

	/* Variable to store the id of the selected division */
	public static int selectedDivisoinId;

	/*
	 * Variable to pass postal code entered by the user to search post office
	 * information
	 */
	String postalCodeInput;

	/* Variable to store postal code */
	public static String poCode = null;

	/*
	 * Boolean variable which is set to true if and only if the user search by
	 * post office.
	 */
	boolean searchByPostOffice = false;

	/* declaring progress dialog */
	ProgressDialog progressDialog;

	/*
	 * Declaring alert dialog to indicate if it is not possible to connect with
	 * the web service
	 */
	AlertDialog connectionFailedAlert;

	/* Declaring alert dialog to indicate if user has entered invalid input data */
	AlertDialog userInputValidateAlert;

	public static String getPoCode() {
		return poCode;
	}

	public static void setPoCode(String poCode) {
		PostalCodeActivity.poCode = poCode;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_postalcode);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		/* Initializing GUI components */
		divisionsSpinner = (Spinner) findViewById(R.id.select_division_dropdown);
		postOfficeSpinner = (Spinner) findViewById(R.id.select_postoffice_dropdown);
		postalCodeEditText = (EditText) findViewById(R.id.postal_code_textbox);

		progressDialog = Functions.getProgressDialog(this, "Please wait...");

		/* Calls the web service to load all divisions from the data base */
		new WSGetAllDivisions().execute(null, null, null);

		postalCodeEditText.setText(null);
		handler = new Handler() {

			public void handleMessage(Message message) {

				/*
				 * If it is possible to connect with the server loads division
				 * list to divisionSpinner. Otherwise display connection failed
				 * alert message
				 */
				if (message.what != -1) {
					final DivisionList divisionList[] = new DivisionList[divisions
							.getDivisionNames().length + 1];
					divisionList[0] = new DivisionList("",
							"-- Select Division --");

					for (int i = 1; i < divisionList.length; i++) {
						divisionList[i] = new DivisionList(
								divisions.getDivisionIds()[i - 1],
								divisions.getDivisionNames()[i - 1]);
						Log.v("divisionName : ", divisionList[i].getValue());
					}

					/* Setting key - value pairs to divisionSpinner */
					ArrayAdapter<DivisionList> divisionsAdapter = new ArrayAdapter<DivisionList>(
							PostalCodeActivity.this,
							android.R.layout.simple_spinner_item, divisionList);
					divisionsSpinner.setAdapter(divisionsAdapter);
					progressDialog.dismiss();

					/*
					 * Setting OnItemSelected listeners. if the selected value
					 * of divisionSpinner is null, load an empty list for
					 * postOfficeSpinner. Otherwise load the post offices which
					 * belongs to the selected division.
					 */
					divisionsSpinner
							.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

								public void onItemSelected(
										AdapterView<?> parent, View view,
										int position, long id) {
									postalCodeEditText.setText(null);

									/*
									 * Getting the selected value of the
									 * division list
									 */
									DivisionList selectedDivision = divisionList[position];

									/*
									 * If value of selected item is not null,
									 * assign it to selectedDivisionId.
									 * Otherwise assign it to -1
									 */

									if (!selectedDivision.getValue().equals("")) {
										selectedDivisoinId = Integer
												.parseInt(selectedDivision
														.getValue());
									} else {
										selectedDivisoinId = -1;
									}

									/*
									 * If selectedDivisionId != -1, calls the
									 * web service to retrieve post offices of
									 * the selected division. Otherwise returns
									 * an empty list of post offices.
									 */

									if (selectedDivisoinId != -1) {

										new WSGetPostOfficeByDivision()
												.execute(null, null, null);

										progressDialog = Functions
												.getProgressDialog(
														PostalCodeActivity.this,
														"Please wait...");

										postOfficeHandler = new Handler() {

											public void handleMessage(
													Message message) {

												/*
												 * If it is possible to connect
												 * with the server loads post
												 * office list to
												 * postOfficeSpinner. Otherwise
												 * display connection failed
												 * alert message
												 */
												if (message.what != -1) {
													final PostOfficeList[] postOfficeList = new PostOfficeList[postOffices
															.getPostOfficeNameList().length + 1];
													postOfficeList[0] = new PostOfficeList(
															"",
															"-- Select Post Office --");
													for (int i = 1; i < postOfficeList.length; i++) {
														postOfficeList[i] = new PostOfficeList(
																postOffices
																		.getPostOfficeNameEnList()[i - 1],
																postOffices
																		.getPostOfficeNameList()[i - 1]);
													}

													/*
													 * Setting key - value pairs
													 * to postOfficeSpinner.
													 */
													ArrayAdapter<PostOfficeList> postOfficeAdapter = new ArrayAdapter<PostOfficeList>(
															PostalCodeActivity.this,
															android.R.layout.simple_spinner_item,
															postOfficeList);
													postOfficeSpinner
															.setAdapter(postOfficeAdapter);
													progressDialog.dismiss();
												} else {

													/*
													 * Display connection failed
													 * alert message
													 */
													connectionFailedAlert = Functions
															.getConnectionFailedAlert(
																	PostalCodeActivity.this,
																	"Sorry. \nUnable to provide the service this time. Please try again latter.");
												}
											}
										};
									} else {

										/*
										 * Set an empty list of post offices to
										 * postOfficeSpinner while loading
										 * division list to divisionSpinner
										 */
										String[] postOfficeEmptyList = { "-- Select Post Office --" };
										ArrayAdapter<String> postOfficeEmptyAdapter = new ArrayAdapter<String>(
												PostalCodeActivity.this,
												android.R.layout.simple_spinner_item,
												postOfficeEmptyList);
										postOfficeSpinner
												.setAdapter(postOfficeEmptyAdapter);
										progressDialog.dismiss();
									}

								}

								public void onNothingSelected(
										AdapterView<?> parent) {

								}
							});

				} else {

					/* Display connection failed alert message */
					connectionFailedAlert = Functions
							.getConnectionFailedAlert(PostalCodeActivity.this,
									"Sorry. \nUnable to provide the service this time. Please try again latter.");

				}

			}

		};

	}

	public void onBackPressed() {
		super.onBackPressed();
		postalCodeEditText.setText("");
		finish();
	}

	public void onSearchPostalCodeButtonClick(View v) {

		if ((int) divisionsSpinner.getSelectedItemPosition() != 0) {
			this.searchByPostOffice = true;
			postalCodeEditText.setText(null);
			Intent resultSceen = new Intent(this,
					PostalCodeResultActivity.class);
			resultSceen.putExtra("searchByPostOffice", true);
			resultSceen.putExtra("divisionId",
					divisions.getDivisionIds()[(int) divisionsSpinner
							.getSelectedItemId() - 1]);

			Log.v("divisionId",
					divisions.getDivisionIds()[(int) divisionsSpinner
							.getSelectedItemId() - 1]);

			if (postOfficeSpinner.getSelectedItemPosition() != 0) {
				resultSceen
						.putExtra(
								"postOfficeNameEn",
								postOffices.getPostOfficeNameEnList()[(int) postOfficeSpinner
										.getSelectedItemId() - 1]);
				Log.v("postOfficeNameEn",
						postOffices.getPostOfficeNameEnList()[(int) postOfficeSpinner
								.getSelectedItemId() - 1]);
				startActivity(resultSceen);
			} else {
				/* Display user input validation alert message */
				userInputValidateAlert = Functions.getUserInputValidateAlert(
						PostalCodeActivity.this, "Please select a post office",
						"Warning!");
			}
		} else {
			/* Display user input validation alert message */
			userInputValidateAlert = Functions.getUserInputValidateAlert(
					PostalCodeActivity.this, "Please select a division",
					"Warning!");

		}
	}

	public void onSearchPostOfficeButtonClick(View v) {
		poCode = postalCodeEditText.getText().toString();
		divisionsSpinner.setSelection(0);

		if (poCode != null) {
			poCode = poCode.trim();
		}

		if (!poCode.equals("")) {
			Intent resultSceen = new Intent(this,
					PostalCodeResultActivity.class);
			resultSceen.putExtra("searchByPostOffice", false);
			resultSceen.putExtra("postalCode", poCode);
			startActivity(resultSceen);

		} else {

			/* Display user input validation alert message */
			userInputValidateAlert = Functions.getUserInputValidateAlert(
					PostalCodeActivity.this, "Please enter a postal code",
					"Warning!");

		}
	}

	/*
	 * Asynchronous back ground process to retrieve the all divisions from the
	 * data base. If an exception occured sends and empty message to the web
	 * service client
	 */
	class WSGetAllDivisions extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			try {
				divisions = PostalCodeWebService.getAllDivisions();
				handler.sendMessage(handler.obtainMessage());
				return null;

			} catch (ConnectException e) {
				/*
				 * Sends an empty message to the web service client if an
				 * ConnectException occurred
				 */
				handler.sendEmptyMessage(-1);
				progressDialog.dismiss();
				Log.i("ConnectException", "ConnectException occured");
				e.printStackTrace();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				// Send an empty message if an exception occurred
				handler.sendEmptyMessage(-1);
				progressDialog.dismiss();
				Log.i("Exception", "Exception occured");
				e.printStackTrace();
			}
			return null;
		}
	}

	/*
	 * Asynchronous back ground process to retrieve the post offices of the
	 * selected division. If an exception occurred sends and empty message to
	 * the web service client
	 */
	class WSGetPostOfficeByDivision extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			try {
				postOffices = PostalCodeWebService
						.getPostalCodeByDivision(selectedDivisoinId);
				postOfficeHandler
						.sendMessage(postOfficeHandler.obtainMessage());
				return null;

			} catch (ConnectException e) {
				// TODO: handle exception
				// Send an empty message if an ConnectException occurred
				handler.sendEmptyMessage(-1);
				progressDialog.dismiss();
				Log.i("ConnectException", "ConnectException occured");
				e.printStackTrace();

			} catch (Exception e) {
				// TODO: handle exception
				// Send an empty message if an exception occurred
				handler.sendEmptyMessage(-1);
				progressDialog.dismiss();
				Log.i("Exception", "Exception occured");
				e.printStackTrace();
			}
			return null;
		}
	}

	class DivisionList {
		public DivisionList(String value, String spinnerText) {
			this.spinnerText = spinnerText;
			this.value = value;
		}

		public String getSpinnerText() {
			return spinnerText;
		}

		public String getValue() {
			return value;
		}

		public String toString() {
			return spinnerText;
		}

		String spinnerText;
		String value;
	}

	class PostOfficeList {
		public PostOfficeList(String value, String spinnerText) {
			this.spinnerText = spinnerText;
			this.value = value;
		}

		public String getSpinnerText() {
			return spinnerText;
		}

		public String getValue() {
			return value;
		}

		public String toString() {
			return spinnerText;
		}

		String spinnerText;
		String value;
	}
}