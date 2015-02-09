import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Quanta {
	
	private Process quantasProcess;
	
	public Quanta(Process p) {
		
		quantasProcess = p;
	}
	
	//This just really means run the process assigned to this quanta.
	public void runQuanta(){
		
		quantasProcess.runProcess();
		
	}
	

}
