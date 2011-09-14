/**
* @copyright	Copyright (C) 2010 - 2010 Kaushalya Kumarasinghe
* @license		GNU/GPL
* This Application is released on behalf of ICTA (Information and Communication Technology Agency of Sri Lanka)
* to the public under the GNU General Public License
*/

package lk.icta.mobile.apps.postalcode.obj;

public class PostOfficesByPostalCode {
	private String[] postOffices;
	private String[] postalCodes;
	private String[] telephoneNumbers;
	private String[] faxNumbers;
	private int count;
	
	public String[] getPostOffices() {
		return postOffices;
	}
	public void setPostOffices(String[] postOffices) {
		this.postOffices = postOffices;
	}
	public String[] getPostalCodes() {
		return postalCodes;
	}
	public void setPostalCodes(String[] postalCodes) {
		this.postalCodes = postalCodes;
	}
	public String[] getTelephoneNumbers() {
		return telephoneNumbers;
	}
	public void setTelephoneNumbers(String[] telephoneNumbers) {
		this.telephoneNumbers = telephoneNumbers;
	}
	public String[] getFaxNumbers() {
		return faxNumbers;
	}
	public void setFaxNumbers(String[] faxNumbers) {
		this.faxNumbers = faxNumbers;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
