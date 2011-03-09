package sample.app.hr_management

import javax.inject.Inject
import org.springframework.stereotype.Component
import sample.common.console.Console
import sample.common.program.Function
import sample.repository.HumanResourceRepository

/**
 * 人材情報削除
 */
@Component
class DeleteHRFunction implements Function {

    @Inject
    HumanResourceRepository hrRepository

    @Inject
    Console console

    @Inject
    HumanResourceView hrView

    /**
     * 人材管理(削除)メニューの実行
     */
    @Override
    void run() {
        def hr = selectHumanResource()
        hrView.display hr
        deleteHumanResource(hr)
    }

    private def selectHumanResource() {
        // 人材ID入力
        long hrId = console.acceptLong('人材IDを入力してください。') {input ->
            hrRepository.findById(input) != null
        }

        hrRepository.findById(hrId)
    }

    private def deleteHumanResource(hr) {
        if (console.confirm('この人材情報を削除しますか？(Y はい　N いいえ)', 'Y', 'N')) {
            hrRepository.delete(hr.id)

            console.display '削除しました。'
        }
    }

}
