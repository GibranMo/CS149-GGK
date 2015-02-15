import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

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
    public float quantaSize = 1;
    
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
    
    
    //First Come First Served.
    public void FCFS (ArrayList<Process> listOfProcesses){
        
        sortByArrivalTime(listOfProcesses);
        
        
        FCFSResults = new SimResults();
        int sumOfCompletedProcesses = 0;
        ArrayList<Integer> setTurnAroundTimes = new ArrayList <Integer> ();
        ArrayList<Integer> setWaitingTimes = new ArrayList <Integer> ();
        
        
        
        int i = 0; //for indexing the ready process queue
        int j = 0; //for indexing the quanta time line. At the beginning, it will be synchronized with 'i' but then it will
        //start getting ahead.
        while (i < listOfProcesses.size()) {
            
            setWaitingTimes.add(j);
            //System.out.println("inserting " + j );
            if (j == quantaLine.size())
            {
                //forget about the rest of the process left in the listOfProcess. Quanta Line is full at this point
                break;
            }
            //**********************************
            quantaLine.get(j).useUpQuanta();//** Immediately burn next quanta spot. (The logic for deciding
            //********************************** whether or not more quanta slots or needed comes right up next).
            
            
            //System.out.println(">>quanta spot-" + j + " process name: " + listOfProcesses.get(i).getProcessName() ); //Since we are doing FCFS, 'i' represents the name of each process
            
            
            //Add process to timeline
            FCFSResults.addToTimeline(listOfProcesses.get(i).getProcessName());
            
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
                    //System.out.println(">>quanta spot-" + j + " process name: " + listOfProcesses.get(i).getProcessName());
                    
                    //add process to timeline
                    FCFSResults.addToTimeline(listOfProcesses.get(i).getProcessName());
                    
                    
                    j++;
                    setTurnAroundTimes.add(j);
                    //System.out.println("inserting " + j );
                    
                }
                
                i++;
                continue;
            }
            
            i++;
            j++;
            setTurnAroundTimes.add(j);
            //System.out.println("inserting " + j );
            
            
        }
        
        //Set num of completed processes
        FCFSResults.setNumOfCompletedProcesses(sumOfCompletedProcesses);
        
        int sum1 = 0;
        for (int k = 0; k < setTurnAroundTimes.size(); k ++)
            sum1 += setTurnAroundTimes.get(k);
        
        FCFSResults.setSumOfTurnaround(sum1);
        
        int sum2 = 0;
        for (int k = 0; k < setWaitingTimes.size(); k++)
            sum2 += setWaitingTimes.get(k);
        
        FCFSResults.setSumOfWaiting(sum2);
        
    }
    
    
    /*
     * Exact same algorithm as FCFS, but 'listOfProcesses' is received here sorted by expected runtime.
     */
    public void SJF (ArrayList<Process> listOfProcesses){
        
        
        SJFsort(listOfProcesses);
        SJFResults = new SimResults();
        int sumOfCompletedProcesses = 0;
        ArrayList<Integer> setTurnAroundTimes = new ArrayList <Integer> ();
        ArrayList<Integer> setWaitingTimes = new ArrayList <Integer> ();
        
        
        
        int i = 0; //for indexing the ready process queue
        int j = 0; //for indexing the quanta time line. At the beginning, it will be synchronized with 'i' but then it will
        //start getting ahead.
        while (i < listOfProcesses.size()) {
            
            setWaitingTimes.add(j);
            //System.out.println("inserting " + j );
            if (j == quantaLine.size())
            {
                //forget about the rest of the process left in the listOfProcess. Quanta Line is full at this point
                break;
            }
            //**********************************
            quantaLine.get(j).useUpQuanta();//** Immediately burn next quanta spot. (The logic for deciding
            //********************************** whether or not more quanta slots or needed comes right up next).
            
            
            //System.out.println(">>quanta spot-" + j + " process name: " + listOfProcesses.get(i).getProcessName() ); //Since we are doing FCFS, 'i' represents the name of each process
            
            
            //Add process to timeline
            FCFSResults.addToTimeline(listOfProcesses.get(i).getProcessName());
            
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
                    //System.out.println(">>quanta spot-" + j + " process name: " + listOfProcesses.get(i).getProcessName());
                    
                    //add process to timeline
                    SJFResults.addToTimeline(listOfProcesses.get(i).getProcessName());
                    
                    
                    j++;
                    setTurnAroundTimes.add(j);
                    //System.out.println("inserting " + j );
                    
                }
                
                i++;
                continue;
            }
            
            i++;
            j++;
            setTurnAroundTimes.add(j);
            //System.out.println("inserting " + j );
        }
        
        //Set num of completed processes
        SJFResults.setNumOfCompletedProcesses(sumOfCompletedProcesses);
        
        int sum1 = 0;
        for (int k = 0; k < setTurnAroundTimes.size(); k ++)
            sum1 += setTurnAroundTimes.get(k);
        
        SJFResults.setSumOfTurnaround(sum1);
        
        int sum2 = 0;
        for (int k = 0; k < setWaitingTimes.size(); k++)
            sum2 += setWaitingTimes.get(k);
        
        SJFResults.setSumOfWaiting(sum2);
        
        
    }
    
    //TODO: Keith
    public void SRT (ArrayList<Process> readyQueue){
    	
        //Sort by arrival time
        sortByArrivalTime(readyQueue);
        
        //Create process queue with estimated run time for 100 quanta only
    	Queue<Process> processQueue = getBatchProcesses(readyQueue);
    	
    	Driver.printStuff(processQueue);
        
        //Convert readyQueue to actual queue structure
    	Deque<Process> processes = convertToDeque(processQueue);
        
        //Used to store suspended processes
        Stack<Process> suspendedProcesses = new Stack<Process>();
        
        //Initialize for sim results
    	SRTResults = new SimResults();
        double turnaroundT = 0.0, responseT = 0.0;
        int throughput = processes.size(), quantaCount = 1, waitingT = 0;
        
        //used to calculate correct stats
        Process firstP = processes.peek();
        
        while(processes.size() > 0 && quantaCount < QUANTA_LIMIT){
        	
        	Process currP = processes.poll();
        	
        	//idling due to no jobs to process
        	while((int)currP.getArrivalTime() > quantaCount){
        		SRTResults.addToTimeline("- idle -");
    			quantaCount++;
        	}
        	
        	//if job has never been paused, it means its the first time its being processed
        	if(!currP.isPaused() && currP != firstP){
        		responseT += quantaCount - currP.getArrivalTime();
        	}
        	
        	//process current job until complete or a job with a shorter running time arrives
        	while((int)Math.ceil(currP.getExpRunTime()) > 0){
        		
        		//if job was previously suspended, update waiting time on resume
        		if(currP.isPaused()){
        			waitingT += quantaCount - currP.getPausedTime();
        			currP.resume();
        		}
        		
        		//decrement run time, add to time line, and increment to next quanta
        		currP.decrementExpRunTime();
        		SRTResults.addToTimeline(currP.getProcessName());
        		quantaCount++;
        		
        		//check if there is another job
        		if(processes.size() > 0){
        			
        			//if next job has arrived in ready queue, check if next job's expected run time is shorter than current job run time
            		if((int)processes.peek().getArrivalTime() <= quantaCount && processes.peek().getExpRunTime() < currP.getExpRunTime()){
            			
            			//suspends current job and stores it for later
            			currP.pause(quantaCount - 1);// one less because it was paused in previous quanta
            			suspendedProcesses.push(currP);
            			break;
            		}
        		}
        		
        		
        		//Job is completed, check if there are previous
        		if((int)Math.ceil(currP.getExpRunTime()) <= 0){
        			
        			//calculate turnaround time
        			turnaroundT += (quantaCount - 1) - currP.getArrivalTime();// one less because it was paused in previous quanta
        			
        			//Check if there is a suspended job
        			if(suspendedProcesses.size() > 0){
        				
        				//Check if there's another job to compare with suspended job
        				if(processes.size() > 0){
        					
        					//Check if suspended job's run time is shorter than next job's run time
            				if(suspendedProcesses.peek().getExpRunTime() < processes.peek().getExpRunTime()){
            					
            					//suspended job has priority if shorter run time and will be resumed
                				processes.addFirst(suspendedProcesses.pop());
            				}
        				}else{
        					//suspended job has priority if shorter run time and will be resumed
            				processes.addFirst(suspendedProcesses.pop());
        				}
        				
        			}
        		}
        	}
        }
        
        SRTResults.setNumOfCompletedProcesses(throughput);
        SRTResults.setSumOfTurnaround(turnaroundT);
        SRTResults.setSumOfWaiting(waitingT);
        SRTResults.setSumOfResponse(responseT);
    }
    
    //TODO: Keith
    public void RR (ArrayList<Process> readyQueue){
    	
    	//max quantum time
    	final int TIME_QUANTUM = 1;
    	
    	//Sort by arrival time
    	sortByArrivalTime(readyQueue);
    	
    	//Create process queue with estimated run time
    	Queue<Process> processes = getBatchProcesses(readyQueue);
    	
    	//Print random generated processes for simulation
    	Driver.printStuff(readyQueue);
    	
    	//Initialize results set
        RRResults = new SimResults();
        double turnaroundT = 0.0, waitingT = 0.0, responseT = 0.0;
        int throughput = processes.size(), quantaCount = 1;
        
        //used to calculate correct stats
        Process firstP = processes.peek();
        
        while(processes.size() > 0){
        	
        	//idling due to no jobs to process
        	while((int)processes.peek().getArrivalTime() > quantaCount){
        		RRResults.addToTimeline("- idle -");
    			quantaCount++;
        	}
        	
        	if(processes.peek().isPaused() && processes.peek() != firstP){
        		//if job was previously paused, add to waiting time
        		waitingT += quantaCount - processes.peek().getPausedTime();
        		processes.peek().resume();
        	}else {
        		//if job was not previously paused, its the first time the job is processed
        		responseT += quantaCount - 1;
        	}
        	
    		//process the job based on quantum limit
			int quantumLimit = 0;
			while(true){
				
				if((int)Math.ceil(processes.peek().getExpRunTime()) > 0){
					//if time quantum is reached, pause and move current job to the end of the queue
					if(quantumLimit == TIME_QUANTUM){
						processes.peek().pause(quantaCount - 1);
						processes.add(processes.poll());
						break;
					}
					
					//add job name to time line, decrement job expected run time
					RRResults.addToTimeline(processes.peek().getProcessName());
					processes.peek().decrementExpRunTime();
					quantaCount++;
					quantumLimit++;
				}
				else{
					//current job is completed, add to turn around time
					turnaroundT += (quantaCount - 1) - processes.poll().getArrivalTime();// one less because it was paused in previous quanta
					break;
				}
			}
			
        }
      
        //Set stats for results calculation
        RRResults.setNumOfCompletedProcesses(throughput);
        RRResults.setSumOfTurnaround(turnaroundT);
        RRResults.setSumOfWaiting(waitingT);
        RRResults.setSumOfResponse(responseT);
    }
    
    //TODO: Graeme
    public void HPFPre (ArrayList<Process> processes){
        
    }
    
    //TODO: Graeme
    public void HPFNonPre (ArrayList<Process> processes){
        
    }
    
    private static void sortByArrivalTime(ArrayList<Process> processes) {
        //Implement Comparator Interface so I can sort Processes by Arrival Time
        Collections.sort(processes, new Comparator<Process> () {
            @Override public int compare(Process p1, Process p2) {
                return Float.compare(p1.getArrivalTime(), p2.getArrivalTime());
            }
        });
    }
    
    //Sorts the processes in ascending order of expected runtime.
    public static void SJFsort(ArrayList<Process> processes) {
        //Implement Comparator Interface so I can sort Processes by Arrival Time
        Collections.sort(processes, new Comparator<Process> () { 
            @Override public int compare(Process p1, Process p2) {
                return Float.compare(p1.getExpRunTime(), p2.getExpRunTime());
            }
        });	
    }
    
    //Helper function to approximate number of processes for 100 quanta based on estimate of expected run time
    private static Queue<Process> getBatchProcesses(ArrayList<Process> processes){
    	Queue<Process> validProcesses = new LinkedList<Process>();
    	int sumOfRuntime = 0, pCount = 0;
    	
    	for(Process p : processes){
    		if(sumOfRuntime >= QUANTA_LIMIT){
    			break;
    		}
    		sumOfRuntime += Math.ceil(p.getExpRunTime());
    		validProcesses.add(p);
    	}
		return validProcesses;
    }
    
    //Convert Queue to Deque (double ended queue)
    private static Deque<Process> convertToDeque(Queue<Process> processes){
    	Deque<Process> queue = new LinkedList<Process>();
    	for(Process p : processes){
    		queue.add(p);
    	}
		return queue;
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
