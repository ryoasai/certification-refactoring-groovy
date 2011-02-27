package sample.common.program

abstract class AbstractDispatcher implements Runnable {

	void run() {
		
		while (true) {
			
			beforeDisplayMenu()
			
			String inputCode = printMenuAndWaitForInput()

			if (isEndCommand(inputCode)) {
				// 終了
				break
			}
			
			runFunction(inputCode)
		}
	}

	protected void beforeDisplayMenu() {}

	protected boolean isEndCommand(String inputCode) {
		'E'.equals(inputCode)
	}

	protected abstract String printMenuAndWaitForInput()
	
	protected abstract void runFunction(String inputCode)

	
}
