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

    private def selectedHumanResource

    /**
     * 人材管理(更新)メニューの実行
     */
    void run() {
        selectHumanResource()

        int itemNo = inputItemNo()
        if (itemNo < 1 || itemNo > 12) { // 項目番号入力エラー
            console.display '項目番号の入力が正しくありません。'
            console.display '更新できませんでした。'
            return
        }

        inputData(itemNo)

        // 入力された値をファイルに保存
        hrRepository.update(selectedHumanResource)
    }

    private void selectHumanResource() {
        // 人材ID入力
        long hrId = console.acceptLong('人材IDを入力してください。', {input ->
            hrRepository.findById(input) != null // 人材存在チェック
        })

        selectedHumanResource = hrRepository.findById(hrId)

        hrView.display selectedHumanResource
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
    void inputData(int itemNo) {
        String[] data = selectedHumanResource.toArray()

        if (itemNo == 8) {
            data[itemNo] = console.accept('更新後の値を入力してください。(M：男性 F：女性)\n[M,F]', {input ->
                'M'.equals(input) || 'F'.equals(input)
            })

        } else if (itemNo == 9) {
            data[itemNo] = console.acceptFromIdList(occupationRespository.findAll(), '更新後の値を入力してください。')
        } else {
            data[itemNo] = console.accept('更新後の値を入力してください。')
        }

        selectedHumanResource.fromArray(data)
    }

}
