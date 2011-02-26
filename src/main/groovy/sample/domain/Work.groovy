package sample.domain

import java.util.ArrayList
import java.util.Arrays
import java.util.List

import sample.common.entity.EntityBase

/**
 * �ғ��󋵃G���e�B�e�B
 */
class Work extends EntityBase<WorkKey> {

	/** �l��ID */
	long hrId
	
	long workStatusNo

	/** �����ID */
	long partnerId

	/** �ғ��J�n�� */
	String startDate

	/** �ғ��I���� */
	String endDate

	/** �_��P�� */
	String contractSalary

	@Override
	void setId(WorkKey id) {
		super.setId(id)
		
		setHrId(id.getHrId())
		setWorkStatusNo(id.getWorkStatusId())
	}
	
	// TODO
	// �ȉ��̕����̃R�[�h�̓��^��񂩂�Ŏ��������ł���͂�
	
	@Override
	String[] toArray() {
			
		List<String> dataList = 
			new ArrayList<String>(
					Arrays.asList(
							String.valueOf(getHrId()),
							String.valueOf(getWorkStatusNo()),
							String.valueOf(getPartnerId()),
							getStartDate(),
							getEndDate(),
							getContractSalary()))
		
		dataList.addAll(createDateColumns())
		return dataList.toArray(new String[dataList.size()])
	}

	@Override
	void fromArray(String[] data) {
		int i = 0
		setHrId(Long.valueOf(data[i++]))
		setWorkStatusNo(Long.valueOf(data[i++]))
		setPartnerId(Long.valueOf(data[i++]))
		setStartDate(data[i++])
		setEndDate(data[i++])
		setContractSalary(data[i++])
		
		readMetaCulumns(data, i)
		
		setId(new WorkKey(getHrId(), getWorkStatusNo()))
	}

}
