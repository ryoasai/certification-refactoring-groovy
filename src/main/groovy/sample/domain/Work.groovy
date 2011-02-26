package sample.domain

import java.util.ArrayList
import java.util.Arrays
import java.util.List

import sample.common.entity.EntityBase

/**
 * 稼動状況エンティティ
 */
class Work extends EntityBase<WorkKey> {

	/** 人材ID */
	long hrId
	
	long workStatusNo

	/** 取引先ID */
	long partnerId

	/** 稼動開始日 */
	String startDate

	/** 稼動終了日 */
	String endDate

	/** 契約単価 */
	String contractSalary

	@Override
	void setId(WorkKey id) {
		super.setId(id)
		
		setHrId(id.getHrId())
		setWorkStatusNo(id.getWorkStatusId())
	}
	
	// TODO
	// 以下の部分のコードはメタ情報からで自動生成できるはず
	
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
