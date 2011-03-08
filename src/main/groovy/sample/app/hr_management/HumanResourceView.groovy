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
        final def TEMPLATE = """
        人材ID ${hr.id}    氏名 ${hr.name}  郵便番号 ${hr.postalCode}
        住所 ${hr.address} 電話番号 ${hr.telephoneNo}   FAX番号 ${hr.faxNo}
        e-mailアドレス ${hr.email}  生年月日 ${hr.birthDay}   性別 ${hr.genderType == 'M' ? '男' : '女'}
        業種 ${getOccupationName(hr.occupationId)} 経験年数 ${hr.yearOfExperience}年
        最終学歴 ${hr.schoolBackground}   希望単価 ${hr.requestedSalary}円
        """.stripIndent()

        console.display '' // 改行
        console.display TEMPLATE
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
