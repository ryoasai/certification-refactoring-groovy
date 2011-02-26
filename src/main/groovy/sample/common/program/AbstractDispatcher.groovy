package sample.common.program

abstract class AbstractDispatcher implements Runnable {

	void run() {
		
		while (true) {
			
			beforeDisplayMenu()
			
			String inputCode = printMenuAndWaitForInput()

			if (isEndCommand(inputCode)) {
				// èIóπ
				break
			}
			
			runFunction(inputCode)
		}
	}

	protected void beforeDisplayMenu() {}

	protected boolean isEndCommand(String inputCode) {
		return 'E'.equals(inputCode)
	}

	protected abstract String printMenuAndWaitForInput()
	
	protected abstract void runFunction(String inputCode)

	
}
