package sample.app.hr_management

import javax.inject.Inject
import org.springframework.stereotype.Component
import sample.common.console.Console
import sample.common.program.Function
import sample.repository.HumanResourceRepository
import sample.repository.OccupationRepository

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
		StringBuilder sb = new StringBuilder()
		sb.append('\n更新したい項目を入力してください。\n')
		
		displayMenuItems(sb)

		console.acceptInt(sb.toString())
	}

	private void displayMenuItems(StringBuilder buf) {
        HumanResourceView.FIELDS_MAP.eachWithIndex {key, value, i ->
            if (i == 0) return

            buf << i
            buf << '.'
            buf << value

			// TODO かなり醜いロジックだが、現状のロジックを保存しておく。
			// 本来はタブ位置を汎用的に自動調整するロジックを書くべき
			
			if (i == 1 || i == 8 || i == 9)
				buf << '\t'
			if (i == 3 || i == 5 || i == 7 || i == 10 || i == 12)
				buf << '\n'
			else if (i != 6)
				buf << '\t'
		}
		
		buf.append('\n[1-12]>')
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
			data[itemNo] = console.accept('更新後の値を入力してください。(M：男性 F：女性)\n[M,F]', {input->
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
