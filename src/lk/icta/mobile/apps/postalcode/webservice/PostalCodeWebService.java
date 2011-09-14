/**
* @copyright	Copyright (C) 2010 - 2010 Kaushalya Kumarasinghe
* @license		GNU/GPL
* This Application is released on behalf of ICTA (Information and Communication Technology Agency of Sri Lanka)
* to the public under the GNU General Public License
*/

package lk.icta.mobile.apps.postalcode.webservice;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import lk.icta.mobile.apps.postalcode.obj.Divisions;
import lk.icta.mobile.apps.postalcode.obj.PostOffices;
import lk.icta.mobile.apps.postalcode.obj.PostOfficesByPostalCode;
import lk.icta.mobile.apps.postalcode.obj.PostalCodesByPostOffice;

//import android.database.CursorJoiner.Result;
import android.util.Log;

public class PostalCodeWebService {
	private static final String NAMESPACE = "http://ws.wso2.org/dataservice";
	private static final String ENDPOINT = "http://192.168.1.125:9762/services/PostalCodeService.PostalCodeServiceHttpSoap12Endpoint";

	private PostalCodeWebService() {
	}

	private static SoapObject callWebService(String actionName,
			SoapObject request) throws Exception {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				ENDPOINT);
		androidHttpTransport.call(actionName, envelope);
		Log.v("result", envelope.bodyOut.toString());
		SoapObject result = (SoapObject) envelope.bodyIn;
		return result;
	}

	public static Divisions getAllDivisions() throws Exception {

		String methodName = "getAllDivisions";
		String actionName = "getAllDivisions";

		SoapObject request = new SoapObject(NAMESPACE, methodName);

		Log.v("epr", ENDPOINT);
		request.addProperty("language", "en");

		SoapObject results = callWebService(actionName, request);
		int length = results.getPropertyCount();

		Divisions divisions = new Divisions();
		divisions.setCount(length);
		divisions.setDivisionIds(new String[length]);
		divisions.setDivisionNames(new String[length]);

		for (int i = 0; i < length; i++) {
			SoapObject result = (SoapObject) results.getProperty(i);
			divisions.getDivisionIds()[i] = String.valueOf(result.getProperty(
					"divisionId").toString());
			divisions.getDivisionNames()[i] = String.valueOf(result
					.getProperty("divisionName").toString());
		}

		return divisions;
	}

	public static PostOffices getPostalCodeByDivision(int divisionId) throws Exception {
		String methodName = "getPostOfficesByDivision";
		String actionName = "getPostOfficesByDivision";
		if(divisionId == -1){
			return null;
		}
		SoapObject request = new SoapObject(NAMESPACE, methodName);
		Log.v("epr", ENDPOINT);
		request.addProperty("language", "en");
		request.addProperty("divisionId", divisionId);
		
		SoapObject results = callWebService(actionName, request);
		int length = results.getPropertyCount();
		
		PostOffices postOffices = new PostOffices();
		postOffices.setCount(length);
		postOffices.setPostOfficeNameList(new String[length]);
		postOffices.setPostOfficeNameEnList(new String[length]);
		
		for (int i = 0; i < length; i++) {
			SoapObject resultPostOffice = (SoapObject) results.getProperty(i);
			postOffices.getPostOfficeNameList()[i] = String.valueOf(resultPostOffice.getProperty("postOfficeName").toString());
			postOffices.getPostOfficeNameEnList()[i] = String.valueOf(resultPostOffice.getProperty("postOfficeNameEn").toString());
			
		}
		
		return postOffices;

	}
	
	public static PostalCodesByPostOffice getPostalCodeByPostOffice(int divisionId, String postOfficeNameEn) throws Exception{
		
		String methodName = "getPostalCodeByPostOffice";
		String actionName = "getPostalCodeByPostOffice";

		SoapObject request = new SoapObject(NAMESPACE, methodName);

		Log.v("epr", ENDPOINT);
		request.addProperty("language", "en");
		request.addProperty("divisionId", divisionId);
		request.addProperty("postOfficeNameEn", postOfficeNameEn);
		
		SoapObject results = callWebService(actionName, request);
		int length = results.getPropertyCount();
		
		PostalCodesByPostOffice postalCodesByPostOffice = new PostalCodesByPostOffice();
						
		postalCodesByPostOffice.setCount(length);
		postalCodesByPostOffice.setPostOffices(new String[length]);
		postalCodesByPostOffice.setPostalCodes(new String[length]);
		postalCodesByPostOffice.setTelephoneNumbers(new String[length]);
		postalCodesByPostOffice.setFaxNumbers(new String[length]);
		
		for (int i = 0; i < length; i++) {
			SoapObject postOfficeByPostalCodeResult = (SoapObject) results.getProperty(i);
			postalCodesByPostOffice.getPostOffices()[i] = String.valueOf(postOfficeByPostalCodeResult.getProperty("postOfficeName"));
			postalCodesByPostOffice.getPostalCodes()[i] = String.valueOf(postOfficeByPostalCodeResult.getProperty("postalCode"));
			postalCodesByPostOffice.getTelephoneNumbers()[i] = String.valueOf(postOfficeByPostalCodeResult.getProperty("telephoneNo"));
			
			if(String.valueOf(postOfficeByPostalCodeResult.getProperty("faxNo"))!= null){
				postalCodesByPostOffice.getFaxNumbers()[i] = String.valueOf(postOfficeByPostalCodeResult.getProperty("faxNo"));
			} else {
				postalCodesByPostOffice.getFaxNumbers()[i] = " - ";
			}
		}
		
		return postalCodesByPostOffice;
		
	}
	
	public static PostOfficesByPostalCode getPostOfficeByPostalCode(String postalCode) throws Exception {
		if(postalCode != null){
			postalCode = postalCode.trim();
		}
		String methodName = "getPostOfficeByPostalCode";
		String actionName = "getPostOfficeByPostalCode";

		SoapObject request = new SoapObject(NAMESPACE, methodName);

		Log.v("epr", ENDPOINT);
		request.addProperty("language", "en");
		request.addProperty("postalCode",postalCode);

		SoapObject results = callWebService(actionName, request);
		int length = results.getPropertyCount();

		PostOfficesByPostalCode postOfficesByPostalCode = new PostOfficesByPostalCode();
						
		postOfficesByPostalCode.setCount(length);
		postOfficesByPostalCode.setPostOffices(new String[length]);
		postOfficesByPostalCode.setPostalCodes(new String[length]);
		postOfficesByPostalCode.setTelephoneNumbers(new String[length]);
		postOfficesByPostalCode.setFaxNumbers(new String[length]);
		
		for (int i = 0; i < length; i++) {
			SoapObject postalCodeByOfficeResult = (SoapObject) results.getProperty(i);
			postOfficesByPostalCode.getPostOffices()[i] = String.valueOf(postalCodeByOfficeResult.getProperty("postOfficeName"));
			postOfficesByPostalCode.getPostalCodes()[i] = postalCode;
			Log.v("postalCode in pcws", postalCode);
			postOfficesByPostalCode.getTelephoneNumbers()[i] = String.valueOf(postalCodeByOfficeResult.getProperty("telephoneNo"));
			postOfficesByPostalCode.getFaxNumbers()[i] = String.valueOf(postalCodeByOfficeResult.getProperty("faxNo"));
		}
		
		return postOfficesByPostalCode;
		
	}
	
}