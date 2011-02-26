package sample.domain

import java.util.ArrayList
import java.util.Arrays
import java.util.List

class Partner extends Party {

	/**
	 * URL
	 */
	String url

	/**
	 * ’S“–ŽÒ
	 */
	String personInCharge

	/**
	 * ’÷‚ß“ú
	 */
	String cutoffDay

	/**
	 * Žx•¥‚¢“ú
	 */
	String paymentDay

	@Override
	String[] toArray() {
			
		List<String> dataList = 
			new ArrayList<String>(
					Arrays.asList(
							String.valueOf(getId()),
							getName(),
							getPostalCode(),
							getAddress(),
							getTelephoneNo(),
							getFaxNo(),
							getUrl(),
							getPersonInCharge(),
							getEmail(),
							getCutoffDay(),
							getPaymentDay()))
		
		dataList.addAll(createDateColumns())
		return dataList.toArray(new String[dataList.size()])
	}


	@Override
	void fromArray(String[] data) {
		int i = 0

		setId(Long.parseLong(data[i++]))
		setName(data[i++])
		setPostalCode(data[i++])
		setAddress(data[i++])
		setTelephoneNo(data[i++])
		setFaxNo(data[i++])
		setUrl(data[i++])
		setEmail(data[i++])
		setPersonInCharge(data[i++])
		setCutoffDay(data[i++])
		setPaymentDay(data[i++])

		readMetaCulumns(data, i)
	}	

}
