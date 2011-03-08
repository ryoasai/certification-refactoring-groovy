package sample.app.hr_management

import javax.inject.Inject
import org.springframework.stereotype.Component
import sample.common.console.Console
import sample.common.program.Function
import sample.domain.HumanResource
import sample.repository.HumanResourceRepository
import sample.repository.OccupationRepository

/**
 * 人材情報入力処理
 */
@Component
class InputHRFunction implements Function {

    @Inject
    HumanResourceRepository hrRepository

    @Inject
    OccupationRepository occupationRespository

    @Inject
    Console console

    /**
     * 人材管理(追加)の実行
     */
    void run() {

        def hr = new HumanResource()
        inputData(hr)

        hrRepository.create(hr)

        console.display "人材ID： ${hr.id}で登録されました。"
    }

    /**
     * 人材情報の入力
     */
    private def inputData(def hr) {

        HumanResource.FIELDS_MAP.each { key, value ->
            def message = "${value}を入力してください。"

            if (value == '性別') {
                hr[key] = console.accept(message, {input ->
                    'M'.equals(input) || 'F'.equals(input)
                })

            } else if (value == '業種') {
                hr[key] = console.acceptFromIdList(occupationRespository.findAll(), message)
            } else {
                hr[key] = console.accept(message)
            }
        }
    }
}
