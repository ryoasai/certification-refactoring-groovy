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
 * �ғ��󋵓���
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
	 * �ғ��󋵊Ǘ�(�ǉ�)�̎��s
	 */
	void run() {
		Work work = inputData()

		doCreate(work)
	}


	/**
	 * �ғ��󋵂̓���
	 */
	private Work inputData() {
		Work work = new Work()
		
		long hrId = console.acceptLong('�l��ID����͂��Ă��������B', {input ->
			hrRepository.findById(input) != null // �l��ID���݃`�F�b�N
		})

		work.setHrId(hrId)
		work.setPartnerId(Long.valueOf(console.acceptFromNameIdList(partnerRepository.findAll(), '������I�����Ă��������B')))
		work.setStartDate(console.accept('�ғ��J�n������͂��Ă��������B'))
		work.setEndDate(console.accept('�ғ��I��������͂��Ă��������B'))
		work.setContractSalary(console.accept('�_��P������͂��Ă��������B'))
		
		work
	}

	/**
	 * �ғ��󋵂̃t�@�C���ւ̓o�^
	 */
	private void doCreate(Work work) {
		workRepository.create(work)
		
		console.display('�o�^����܂����B')
	}

}
