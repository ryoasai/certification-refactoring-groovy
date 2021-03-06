package sample.app.hr_search

import javax.annotation.PostConstruct
import javax.inject.Inject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import sample.app.hr_management.HumanResourceView
import sample.app.work_management.WorkListView
import sample.common.console.Console
import sample.common.console.View
import sample.common.program.AbstractDispatcher
import sample.domain.HumanResource
import sample.domain.Occupation
import sample.domain.Work
import sample.repository.HumanResourceRepository
import sample.repository.OccupationRepository
import sample.repository.WorkRepository

@Component
class HumanResourceListView extends AbstractDispatcher implements View<List<HumanResource>> {

    /**
     * 機能一覧
     */
    private static final def MENU_LIST = ['P->前の10件\tN->次の10件', 'E->検索一覧終了( 検索条件指定に戻る )']

    /**
     * メニュー文字列
     */
    private static final def CODE_LIST = ['人材ID', 'P', 'N', 'E']

    @Inject
    HumanResourceRepository hrRepository

    @Inject
    WorkRepository workRespository

    @Inject
    OccupationRepository occupationRepository

    @Inject
    WorkListView workListView

    @Inject
    HumanResourceView hrView

    @Autowired
    Console console

    /**
     * 業種リスト
     */
    private def occupationList

    private def hrList

    /**
     * ページ番号
     */
    private int page = 1

    @PostConstruct
    void init() {
        occupationList = occupationRepository.findAll();
    }

    void next() {
        page++
    }

    void previous() {
        page--
    }

    @Override
    protected boolean isEndCommand(String inputCode) {
        'E'.equals(inputCode)
    }

    @Override
    protected void beforeDisplayMenu() {
        displayHRListOnPage()
    }

    @Override
    protected void runFunction(String inputCode) {
        if ('P' == inputCode) {
            // 前の10件
            if (page > 1) {
                previous()
            }
        } else if ('N' == inputCode) {
            // 次の10件
            next()
        } else { // 人材ID入力
            displayHumanResource(inputCode)
        }
    }

    /**
     * 人材一覧の表示
     *
     * @return 表示件数
     */
    @Override
    void display(List<HumanResource> hrList) {
        this.hrList = hrList

        run()
    }

    @Override
    protected String printMenuAndWaitForInput() {
        console.display ''; // 改行
        console.display MENU_LIST; // 機能一覧の表示

        console.acceptFromList(CODE_LIST, '')
    }

    private def displayHRListOnPage() {
        while (true) { // 人材情報がヒットしなければ表示を繰り返す
            if (doDisplayHRListOnPage() > 0 || page == 1)
                break; // 人材一覧が表示されたか1ページ目のときにループを抜ける

            previous(); // 表示するレコードがなければ1ページ前を表示
        }
    }

    private int doDisplayHRListOnPage() {
        console.display('検索結果一覧')
        int count = 0; // 表示件数

        for (i in ((page - 1) * 10)..<(page * 10)) {
            if (i >= hrList.size()) break

            HumanResource hr = hrList.get(i)

            console.display(
                    hr.getId() + '\t' + hr.getName() + '\t'
                            + ((hr.getName().length() < 4) ? '\t' : '') // 3文字以下の名前のときタブ追加
                            + getOccupationName(hr.getOccupationId()))

            count++; // 表示件数をカウント
        }

        if (count == 0) {
            console.display '人材情報はありません。'
        }

        count
    }


    private def displayHumanResource(String inputCode) {
        try {
            HumanResource hr = hrRepository.findById(Long.parseLong(inputCode))
            hrView.display(hr)

            // 人材IDのセット
            if (hr == null) { // 人材情報を表示
                console.display '入力された人材情報は登録されていません。'
            }

            console.display '\n稼働状況---------------------------------------------'
            workListView.display(findWorkListByHRId(hr.id))

            console.accept 'エンターキーを押すと検索結果一覧に戻ります。'

        } catch (NumberFormatException e) {
            console.display '入力された人材情報は登録されていません。'
        }
    }

    /**
     * 業種IDから業種名の取得
     *
     * @param occupationId
     *            業種IDを表す文字列
     * @return 業種名
     */
    private def getOccupationName(long occupationId) {
        def found = occupationList.find {occupation ->
            occupation.id == occupationId
        }

        found?.name
    }

    private def findWorkListByHRId(long hrId) {
        Work workExample = new Work()
        workExample.hrId = hrId

        workRespository.findByExample(workExample)
    }

}
