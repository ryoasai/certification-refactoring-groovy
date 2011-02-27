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
		def dataList = [
			String.valueOf(id),
			name,
			postalCode,
			address,
			telephoneNo,
			faxNo,
			email,
			birthDay,
			genderType,
			occupationId,
			yearOfExperience,
			schoolBackground,
			requestedSalary]
		
		dataList.addAll(createDateColumns())
		dataList.toArray()
	}

	@Override
	void fromArray(String[] data) {
		int i = 0

		id = data[i++].toLong()
		name = data[i++]
		postalCode = data[i++]
		address = data[i++]
		telephoneNo = data[i++]
		faxNo = data[i++]
		email = data[i++]
		birthDay = data[i++]
		genderType = data[i++]
		occupationId = data[i++].toLong()
		yearOfExperience = data[i++]
		schoolBackground = data[i++]
		requestedSalary = data[i++]

		readMetaCulumns(data, i)
	}	

}
