package sample.common.io

import sample.common.SystemException


class EntityNotFoundException extends SystemException {
	private static final long serialVersionUID = 1L
	
	EntityNotFoundException() {
		super()
	}

	EntityNotFoundException(String message, Throwable cause) {
		super(message, cause)
	}

	EntityNotFoundException(String message) {
		super(message)
	}

	EntityNotFoundException(Throwable cause) {
		super(cause)
	}

}
