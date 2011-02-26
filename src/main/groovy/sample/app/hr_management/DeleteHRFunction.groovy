package sample.app.hr_management


import javax.inject.Inject

import org.springframework.stereotype.Component

import sample.common.console.Console
import sample.common.program.Function
import sample.domain.HumanResource
import sample.repository.HumanResourceRepository

/**
 * �l�ޏ��폜
 */
@Component
class DeleteHRFunction implements Function {

	@Inject
	HumanResourceRepository hrRepository

	@Inject
	Console console

	@Inject
	HumanResourceView hrView
	
	private HumanResource selectedHumanResource

	/**
	 * �l�ފǗ�(�폜)���j���[�̎��s
	 */
	@Override
	void run() {
		selectHumanResource()
		
		deleteHumanResource()
	}

	private void selectHumanResource() {
		// �l��ID����
		long hrId = console.acceptLong('�l��ID����͂��Ă��������B', {input ->
			hrRepository.findById(input) != null
		})
		
		selectedHumanResource = hrRepository.findById(hrId)
		
		hrView.display(selectedHumanResource)
	}
	
	private void deleteHumanResource() {
		if (console.confirm('���̐l�ޏ����폜���܂����H(Y �͂��@N ������)', 'Y', 'N')) {
			hrRepository.delete(selectedHumanResource.getId())
			console.display('�폜���܂����B') 
		}
	}

}
