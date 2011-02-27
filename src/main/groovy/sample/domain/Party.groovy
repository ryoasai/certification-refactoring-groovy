package sample.domain

import sample.common.entity.EntityBase
import sample.common.entity.NameId

abstract class Party extends EntityBase<Long> implements NameId<Long> {

	/** 氏名 */
	String name

	/** 郵便番号 */
	String postalCode

	/** 住所 */
	String address

	/** 電話番号 */
	String telephoneNo

	/** FAX番号 */
	String faxNo

	/** emailアドレス */
	String email

}
