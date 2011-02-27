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
	
	private def selectedHumanResource

	/**
	 * 人材管理(追加)の実行
	 */
	void run() {
		
		selectedHumanResource = new HumanResource()
		selectedHumanResource.fromArray(inputData())
		
		hrRepository.create(selectedHumanResource)

		console.display "人材ID： ${selectedHumanResource.id}で登録されました。"
	}

	/**
	 * 人材情報の入力
	 * 
	 * @param occupationList
	 *            業種リストを表す文字列配列
	 * @return 入力情報
	 */
	String[] inputData() {
		String[] data = new String[HumanResourceView.FIELDS.size()]
		
		for (i in 1 ..< HumanResourceView.FIELDS.size()) {
			if (HumanResourceView.FIELDS[i].equals('性別')) {
				data[i] = console.accept("${HumanResourceView.FIELDS[i]}を入力してください。", {input ->
					'M'.equals(input) || 'F'.equals(input)
				})
			
			} else if (HumanResourceView.FIELDS[i].equals('業種')) {
				data[i] = console.acceptFromIdList(occupationRespository.findAll(), "${HumanResourceView.FIELDS[i]}を入力してください。")
			} else {
				data[i] = console.accept("${HumanResourceView.FIELDS[i]}を入力してください。")
			}
		}
		
		data
	}
}
