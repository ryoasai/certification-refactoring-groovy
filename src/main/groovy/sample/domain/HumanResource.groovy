package sample.domain

import java.util.ArrayList
import java.util.Arrays
import java.util.List

/**
 * 人材エンティティ
 */
class HumanResource extends Party {


	/** 誕生日 */
	String birthDay

	/** 性別 */
	String genderType

	/** 業種ID */
	long occupationId

	/** 経験年数 */
	String yearOfExperience

	/** 最終学歴 */
	String schoolBackground

	/** 希望単価 */
	String requestedSalary

	// TODO
	// 以下の部分のコードはメタ情報からで自動生成できるはず

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
