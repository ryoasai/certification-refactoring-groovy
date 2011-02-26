package sample.app.hr_management

import javax.inject.Inject

import org.springframework.stereotype.Component

import sample.common.console.Console
import sample.common.program.Function
import sample.domain.HumanResource
import sample.repository.HumanResourceRepository
import sample.repository.OccupationRepository

/**
 * �l�ޏ����͏���
 */
@Component
class InputHRFunction implements Function {

	@Inject
	HumanResourceRepository hrRepository

	@Inject
	OccupationRepository occupationRespository
	
	@Inject
	Console console
	
	private HumanResource selectedHumanResource

	/**
	 * �l�ފǗ�(�ǉ�)�̎��s
	 */
	void run() {
		
		selectedHumanResource = new HumanResource()
		selectedHumanResource.fromArray(inputData())
		
		hrRepository.create(selectedHumanResource)

		console.display("�l��ID�F $selectedHumanResource.id�œo�^����܂����B")
	}

	/**
	 * �l�ޏ��̓���
	 * 
	 * @param occupationList
	 *            �Ǝ탊�X�g��\��������z��
	 * @return ���͏��
	 */
	String[] inputData() {
		String[] data = new String[HumanResourceView.FIELDS.size()]
		
		for (int i = 1; i < HumanResourceView.FIELDS.size(); i++) {
			if (HumanResourceView.FIELDS[i].equals('����')) {
				data[i] = console.accept("$HumanResourceView.FIELDS[i]����͂��Ă��������B", {input ->
					'M'.equals(input) || 'F'.equals(input)
				})
			
			} else if (HumanResourceView.FIELDS[i].equals('�Ǝ�')) {
				data[i] = console.acceptFromIdList(occupationRespository.findAll(), "$HumanResourceView.FIELDS[i]����͂��Ă��������B")
			} else {
				data[i] = console.accept("$HumanResourceView.FIELDS[i]����͂��Ă��������B")
			}
		}
		
		data
	}
}
