package sample.common;

import sample.common.entity.EntityBase

public class SampleEntity extends EntityBase<Long> {

	String name
	String age
	
	@Override
	public String[] toArray() {
			
		def dataList = [
			id,
			name,
			age
		]

		dataList + createDateColumns()
	}


	@Override
	public void fromArray(String[] data) {
		int i = 0

		id = data[i++]?.toLong()
		name =data[i++]
		age = data[i++]
		
		readMetaCulumns(data, i)
	}

}
