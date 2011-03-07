package sample.app.work_management

import javax.inject.Inject
import org.springframework.stereotype.Component
import sample.common.console.Console
import sample.common.program.Function
import sample.domain.Work
import sample.repository.HumanResourceRepository
import sample.repository.PartnerRepository
import sample.repository.WorkRepository

/**
 * 稼働状況入力
 */
@Component
class InputWorkFunction implements Function {

    @Inject
    WorkRepository workRepository

    @Inject
    HumanResourceRepository hrRepository

    @Inject
    PartnerRepository partnerRepository

    @Inject
    Console console

    /**
     * 稼働状況管理(追加)の実行
     */
    void run() {
        Work work = inputData()

        doCreate(work)
    }

    /**
     * 稼働状況の入力
     */
    private Work inputData() {
        Work work = new Work()

        long hrId = console.acceptLong('人材IDを入力してください。', {input ->
            hrRepository.findById(input) != null // 人材ID存在チェック
        })

        work.hrId = hrId
        work.partnerId = console.acceptFromNameIdList(partnerRepository.findAll(), '取引先を選択してください。')
        work.startDate = console.accept('稼動開始日を入力してください。')
        work.endDate = console.accept('稼動終了日を入力してください。')
        work.contractSalary = console.accept('契約単価を入力してください。')

        work
    }

    /**
     * 稼働状況のファイルへの登録
     */
    private void doCreate(Work work) {
        workRepository.create(work)

        console.display '登録されました。'
    }

}
