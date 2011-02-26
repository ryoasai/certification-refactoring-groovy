package sample.domain

import java.util.ArrayList
import java.util.Arrays
import java.util.List

/**
 * �l�ރG���e�B�e�B
 */
class HumanResource extends Party {


	/** �a���� */
	String birthDay

	/** ���� */
	String genderType

	/** �Ǝ�ID */
	long occupationId

	/** �o���N�� */
	String yearOfExperience

	/** �ŏI�w�� */
	String schoolBackground

	/** ��]�P�� */
	String requestedSalary

	// TODO
	// �ȉ��̕����̃R�[�h�̓��^��񂩂�Ŏ��������ł���͂�

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
							getEmail(),
							getBirthDay(),
							getGenderType(),
							String.valueOf(getOccupationId()),
							getYearOfExperience(),
							getSchoolBackground(),
							getRequestedSalary()))
		
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
		setEmail(data[i++])
		setBirthDay(data[i++])
		setGenderType(data[i++])
		setOccupationId(Long.valueOf(data[i++]))
		setYearOfExperience(data[i++])
		setSchoolBackground(data[i++])
		setRequestedSalary(data[i++])

		readMetaCulumns(data, i)
	}	

}
