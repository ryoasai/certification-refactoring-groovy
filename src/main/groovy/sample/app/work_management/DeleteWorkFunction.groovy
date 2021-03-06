package sample.app.work_management

import javax.inject.Inject
import org.springframework.stereotype.Component
import sample.common.console.Console
import sample.common.program.Function
import sample.domain.Work
import sample.domain.WorkKey
import sample.repository.HumanResourceRepository
import sample.repository.WorkRepository

/**
 * 稼働状況削除
 */
@Component
class DeleteWorkFunction implements Function {

    @Inject
    WorkRepository workRepository

    @Inject
    HumanResourceRepository hrRepository

    @Inject
    WorkListView workListView

    @Inject
    Console console

    void run() {

        // 人材ID入力
        def hrId = console.acceptLong('人材IDを入力してください。') {input ->
            hrRepository.findById(input) != null // 人材ID存在チェック
        }

        // 人材IDに関連する稼動リストの検索
        def workList = findWorkListByHRId(hrId)
        if (workList.isEmpty()) {
            return
        }

        // 稼働状況を表示
        workListView.display(workList)

        // 削除する稼働状況IDの取得
        def workId = console.acceptFromIdList(workList, '削除したい稼働状況の番号を入力してください。')

        if (console.confirm('この情報を削除しますか？(Y はい　N いいえ)', 'Y', 'N')) {
            workRepository.delete(new WorkKey(hrId, workId.toLong()))
            console.display '削除しました。'
        }
    }

    private def findWorkListByHRId(long hrId) {
        def workExample = new Work()
        workExample.hrId = hrId

        workRepository.findByExample(workExample)
    }

}
