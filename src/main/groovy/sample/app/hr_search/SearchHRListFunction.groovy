package sample.app.hr_search

import javax.inject.Inject
import org.springframework.stereotype.Component
import sample.common.console.Console
import sample.common.program.AbstractDispatcher
import sample.common.program.Function
import sample.domain.HumanResource
import sample.domain.Occupation
import sample.repository.HumanResourceRepository
import sample.repository.OccupationRepository

/**
 * 人材検索処理
 */
@Component
class SearchHRListFunction extends AbstractDispatcher implements Function {

    /**
     * 検索方法一覧のリスト
     */
    private static final String[] MENU_LIST = [
            '検索方法を指定してください。',
            'N->氏名から検索\tT->業種から検索',
            'E->人材検索終了(メニューに戻る)']

    /**
     * 検索方法コード一覧のリスト
     */
    private static final def CODE_LIST = ['N', 'T', 'E']

    @Inject
    HumanResourceRepository hrRepository

    @Inject
    OccupationRepository occupationRepository

    @Inject
    HumanResourceListView hrListView

    @Inject
    Console console

    @Override
    protected String printMenuAndWaitForInput() {
        console.display ''
        console.display MENU_LIST

        console.acceptFromList(CODE_LIST, 'どの機能を実行しますか？')
    }

    /**
     * 氏名もしくは業種から検索機能を呼び出す
     *
     * @param code 機能コードを表す整数値
     */
    @Override
    protected void runFunction(String code) {

        if ('N' == code) {
            def input = console.accept('氏名に含まれる文字列を指定してください。')
            searchHRListByName(input)

        } else if ('T' == code) {

            def occupationList = occupationRepository.findAll() // 業種リストの取得
            def occupationType = console.acceptFromNameIdList(occupationList, '\n業種を選択してください。')

            // 業種IDから人材検索
            searchHRListByOccupationType(occupationType?.toLong())
        }
    }

    private def searchHRListByName(String name) {

        def exampleHR = new HumanResource();
        exampleHR.setName(name)

        doSearchHRList(exampleHR)
    }

    private def searchHRListByOccupationType(long occupationId) {

        def exampleHR = new HumanResource();
        exampleHR.setOccupationId(occupationId)

        doSearchHRList(exampleHR)
    }

    private def doSearchHRList(HumanResource exampleHR) {
        def hrList = hrRepository.findByExample(exampleHR)
        hrListView.display(hrList)
    }
}
