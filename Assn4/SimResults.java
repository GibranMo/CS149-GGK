import java.util.ArrayList;



public class SimResults {

	private ArrayList<String> processMem; //process memory
	
	public SimResults(){
		processMem = new ArrayList<String>();
	}
	
	//Add process id to process memory
	public void addProcess(String pID){
		processMem.add(pID);
	}
	
	//return process memory
	public ArrayList<String> getProcessMem(){
		return processMem;
	}
	
}
