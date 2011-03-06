package sample.app.hr_management

import javax.annotation.PostConstruct
import javax.inject.Inject
import org.springframework.stereotype.Component
import sample.common.console.Console
import sample.common.console.View
import sample.domain.HumanResource
import sample.domain.Occupation
import sample.repository.OccupationRepository

/**
 * 人材情報詳細表示
 */
@Component
class HumanResourceView implements View<HumanResource> {

    static final def FIELDS_MAP = [
        id:'人材ID',
        name:'氏名',
        postalCode:'郵便番号',
        address:'住所',
        telephoneNo:'電話番号',
        faxNo:'FAX番号',
        email:'e-mailアドレス',
        birthDay:'生年月日',
        genderType:'性別',
        occupationId:'業種',
        yearOfExperience:'経験年数',
        schoolBackground:'最終学歴',
        requestedSalary:'希望単価' ]

    static final def LINE_BREAK_AFTER = [
            '郵便番号',
            '住所',
            'FAX番号',
            'e-mailアドレス',
            '性別',
            '経験年数',
    ] as Set

	@Inject
	OccupationRepository occupationRepository

	@Inject
	Console console
	
	private Map<Long, Occupation> occupationMap

	@PostConstruct
	void init() {
		occupationMap = occupationRepository.findAllAsMap()
	}

	void display(HumanResource hr) {
		
		console.display '' // 改行

		// 人材情報の表示
		FIELDS_MAP.each { key, value ->
			StringBuilder sb = new StringBuilder("${value}  : ")
			
			if (value == '性別') {
				if (hr.genderType == 'M') {
					sb << '男'
				} else if (hr.genderType == 'F') {
					sb << '女'
				}
			
			} else if (value == '業種') {
				sb << getOccupationName(hr.occupationId)
			} else {
				sb << hr.properties[key]
			}
			
			if (value == '経験年数') {
				sb << '年'
			} else if (value == '希望単価') {
				sb << '円'
			}

			if (LINE_BREAK_AFTER.contains(value)) {
				sb << '\n'
			} else {
				sb << '\t'
			}
			
			console.display sb.toString()
		}
	}
	
	/**
	 * 業種IDより業種名の取得
	 * 
	 * @param occupationId 業種IDを表す文字列
	 * @return 業種名
	 */
	private String getOccupationName(long occupationId) {
		try {
			Occupation occupation = occupationMap.get(occupationId)
		
			if (occupation == null) return null
			
			occupation.name
		} catch (NumberFormatException ex) {
			null
		}
	}

}
