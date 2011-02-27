package sample.common.entity

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
import java.util.List

import sample.common.SystemException


abstract class EntityBase<K> implements ArrayConvertable, Identifiable<K> {

	/** ID */
	K id

	/** 登録日付 */
	Date createDate
	
	/** 更新日付 */
	Date updateDate
	
	/** 削除日付 */
	Date deleteDate
	
	boolean isPersisted() {
		return getId() != null
	}
	
	void preCreate() {
		Date date = new Date()
		
		setCreateDate(date)
		setUpdateDate(date)
	}

	void preUpdate() {
		setUpdateDate(new Date())
	}
	
	/**
	 * 既に論理削除済みかどうか
	 * @return 論理削除済みの場合はtrue
	 */
	boolean isLogicalDeleted() {
		return deleteDate != null
	}

	protected String formatDate(Date date) {
		SimpleDateFormat dateFormat = createDateFormat()
		return dateFormat.format(date)
	}

	protected SimpleDateFormat createDateFormat() {
		return new SimpleDateFormat('yyyyMMdd')
	}

	protected Date parseDate(String dateStr) {
		SimpleDateFormat dateFormat = createDateFormat()
		try {
			return dateFormat.parse(dateStr)
		} catch (ParseException e) {
			throw new SystemException('日付のパーズに失敗:' + dateStr, e)
		}
	}
	
	void logicalDelete() {
		if (isLogicalDeleted()) throw new IllegalStateException('既に論理削除済みです。')
		
		setDeleteDate(new Date())
	}
	
	protected void readMetaCulumns(String[] data, int startColumn) {
		int i = startColumn
		if (i < data.length) {
			setCreateDate(parseDate(data[i++]))
		}
		
		if (i < data.length) {
			setUpdateDate(parseDate(data[i++]))
		}
		
		if (i < data.length) {
			setDeleteDate(parseDate(data[i++]))
		}
	}
	
	protected List<String> createDateColumns() {
		if (isLogicalDeleted()) {
			return Arrays.asList(
				formatDate(getCreateDate()),
				formatDate(getUpdateDate()),
				formatDate(getDeleteDate())
			)
		} else {
			return Arrays.asList(
				formatDate(getCreateDate()),
				formatDate(getUpdateDate())
			)
		}
	}
}
