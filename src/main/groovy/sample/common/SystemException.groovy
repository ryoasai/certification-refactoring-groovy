package sample.common

class SystemException extends RuntimeException {

	private static final long serialVersionUID = 1L

	SystemException() {
		super()
	}

	SystemException(String message, Throwable cause) {
		super(message, cause)
	}

	SystemException(String message) {
		super(message)
	}

	SystemException(Throwable cause) {
		super(cause)
	}

}
