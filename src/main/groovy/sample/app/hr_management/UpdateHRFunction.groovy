package sample.app.hr_management

import javax.inject.Inject
import org.springframework.stereotype.Component
import sample.common.console.Console
import sample.common.program.Function
import sample.repository.HumanResourceRepository
import sample.repository.OccupationRepository
import sample.domain.HumanResource

/**
 * 人材情報更新
 */
@Component
class UpdateHRFunction implements Function {

    @Inject
    HumanResourceRepository hrRepository

    @Inject
    OccupationRepository occupationRespository

    @Inject
    HumanResourceView hrView

    @Inject
    Console console

    /**
     * 人材管理(更新)メニューの実行
     */
    void run() {
        def hr = selectHumanResource()
        hrView.display hr

        int itemNo = inputItemNo()

        inputData(hr, HumanResource.FIELDS_MAP.entrySet().toList()[itemNo - 1])

        // 入力された値をファイルに保存
        hrRepository.update(hr)
    }

    private def selectHumanResource() {
        // 人材ID入力
        long hrId = console.acceptLong('人材IDを入力してください。') {input ->
            hrRepository.findById(input) != null // 人材存在チェック
        }

        hrRepository.findById(hrId)
    }


    private int inputItemNo() {
        console.display '更新したい項目を入力してください。'

        console.acceptFromMenuItems(HumanResource.FIELDS_MAP, 30)
    }


    /**
     * 人材情報の入力
     *
     * @param occupationList
     *            業種リストを表す文字列配列
     * @return 入力情報
     */
    private def inputData(hr, selectedField) {
        def message = '更新後の値を入力してください。'

        if (selectedField.value == '性別') {
            hr[selectedField.key] = console.accept(message + '(M：男性 F：女性)\n[M,F]') {input ->
                'M'.equals(input) || 'F'.equals(input)
            }

        } else if (selectedField.value == '業種') {
            hr[selectedField.key] = console.acceptFromIdList(occupationRespository.findAll(), message)
        } else {
            hr[selectedField.key] = console.accept(message)
        }
    }

}
