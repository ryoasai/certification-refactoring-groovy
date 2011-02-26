package sample.domain

import sample.common.entity.EntityBase
import sample.common.entity.NameId

abstract class Party extends EntityBase<Long> implements NameId<Long> {

	/** ���� */
	String name

	/** �X�֔ԍ� */
	String postalCode

	/** �Z�� */
	String address

	/** �d�b�ԍ� */
	String telephoneNo

	/** FAX�ԍ� */
	String faxNo

	/** email�A�h���X */
	String email

}
