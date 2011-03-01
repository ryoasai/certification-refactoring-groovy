package sample.domain

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
			
		def dataList = [
			hrId,
			workStatusNo,
			partnerId,
			startDate,
			endDate,
			contractSalary]
		
		dataList.addAll(createDateColumns())
		dataList.toArray()
	}

	@Override
	void fromArray(String[] data) {
		int i = 0
		hrId = data[i++].toLong()
		workStatusNo = data[i++].toLong()
		partnerId = data[i++].toLong()
		startDate = data[i++]
		endDate = data[i++]
		contractSalary = data[i++]
		
		readMetaCulumns(data, i)
		
		setId(new WorkKey(getHrId(), getWorkStatusNo()))
	}

}
