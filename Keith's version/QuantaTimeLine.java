import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Represents the timeline of time chunks
public class QuantaTimeLine {
	
	//number of quanta to reach, sim results may have 100+
    private final static int QUANTA_LIMIT = 100;
    
    //Store results for each algorithm run
    private SimResults FCFSResults;
    private SimResults SJFResults;
    private SimResults SRTResults;
    private SimResults RRResults;
    private SimResults HPFPreResults;
    private SimResults HPFNonPreResults;
	
    private static ArrayList <Quanta >quantaLine = new ArrayList <Quanta>();
    public float quantaSize = 3; // Equal to 3 seconds  (Hence, you will see the number 3000 representing milliseconds
    
    //Just allocate 100 spaces
    public QuantaTimeLine() {
        
        int i  = 0;
        while (i < 100)
        {
            Quanta newQuanta = new Quanta ();
            quantaLine.add(newQuanta);
            i++;
        }
    }
    
    //TODO: Gibran
    //First Come First Served.
    public void FCFS (ArrayList<Process> listOfProcesses){
    	
    	FCFSResults = new SimResults();
    	int sumOfCompletedProcesses = 0;
    	        
        int i = 0; //for indexing the ready process queue
        int j = 0; //for indexing the quanta time line. At the beginning, it will be synchronized with 'i' but then it will
        //start getting ahead.
        while (i < listOfProcesses.size()) {
            
            if (j == quantaLine.size())
            {
                //forget about the rest of the process left in the listOfProcess. Quanta Line is full at this point
                break;
            }
            //**********************************
            quantaLine.get(j).useUpQuanta();//** Immediately burn next quanta spot. (The logic for deciding
            //********************************** whether or not more quanta slots or needed comes right up next).
            
            //****OFFICIAL REQUIRED OUTPUT - Site 1 (there is another spot below  where this output is needed)(Matching quanta spot with process)
            System.out.println(">>quanta spot-" + j + " process name: " + listOfProcesses.get(i).getProcessName() ); //Since we are doing FCFS, 'i' represents the name of each process
            //*******************************************************************
            
            //Add process to timeline
            FCFSResults.addToTimeline(listOfProcesses.get(i));
            
           //increment num of completed processes
            sumOfCompletedProcesses++;
            
            //Check if the expected runtime of current process (i) will fit into 1 quanta which is 3.0
            //float processTimeRem = quantaLine.get(i).getProcessTimeRemaining(); //extract time of current process
            float processTimeRem = listOfProcesses.get(i).getExpRunTime();
            
            if (processTimeRem > quantaSize)
            {
                //Ceiling Function - Rounding up Any decimal to a whole number
                int additonalQuantas = (int) Math.ceil(processTimeRem);
                
                //use j to advance position in quanta time line
                j++; //Process didn't fit in one quanta slice, so advance at least one spot in quanta time line
                for (int k = 1; k < additonalQuantas; k++ ) //Iterate the number of times the process fits into quantas slices
                {
                    if (j >= quantaLine.size()) //need to expand quanta line
                    {
                        quantaLine.add(new Quanta());
                        
                    }
                    //advanceQuantaPosition(j, listOfProcesses); //Use up quanta time line while remaining at the same remaining at same ready process index
                    quantaLine.get(j).useUpQuanta();
                    System.out.println(">>quanta spot-" + j + " process name: " + listOfProcesses.get(i).getProcessName());
                    
                    //add process to timeline
                    FCFSResults.addToTimeline(listOfProcesses.get(i));
                    j++;
                    
                }
                
                i++;
                continue;
            }
            
            i++;
            j++;
            
        }
        
        //Set num of completed processes
        FCFSResults.setNumOfCompletedProcesses(sumOfCompletedProcesses);
        
    }
    
    //TODO: Gibran
    public void SJF (ArrayList<Process> processes){
    	
    }
    
    //TODO: Keith
    public SimResults SRT (ArrayList<Process> processes){
    	SRTResults = new SimResults();
    	double sumOfTurnaround = 0,sumOfWaiting = 0, sumOfResponse = 0;
    	int sumOfCompletedProcesses = 0;
    	/*
    	 * Run simulation
    	 * Set turnaround, waiting, response, throughput, timeline for results
    	 */
    	return SRTResults;
    }
    
    //TODO: Keith
    public SimResults RR (ArrayList<Process> processes){
    	RRResults = new SimResults();
    	/*
    	 * Run simulation
    	 * Set turnaround, waiting, response, throughput, timeline for results
    	 */
    	return RRResults;
    }
    
    //TODO: Graeme
    public void HPFPre (ArrayList<Process> processes){
    	
    }
    
    //TODO: Graeme
    public void HPFNonPre (ArrayList<Process> processes){
    	
    }
    
    /* RETURN SIMULATION RESULTS */
    public SimResults getFCFSResults(){
    	return FCFSResults;
    }
    
    public SimResults getSJFResults(){
    	return SJFResults;
    }
    
    public SimResults getSRTResults(){
    	return SRTResults;
    }
    
    public SimResults getRRResults(){
    	return RRResults;
    }
    
    public SimResults getHPFPreResults(){
    	return HPFPreResults;
    }
    
    public SimResults getHPFNonPreResults(){
    	return HPFNonPreResults;
    }

    
}
