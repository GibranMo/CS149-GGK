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
        
        ArrayList<Integer> setTurnAroundTimes = new ArrayList <Integer> ();
        ArrayList<Integer> setWaitingTimes = new ArrayList <Integer> ();
        
        int i = 0; //for indexing the ready process queue
        int j = 0; //for indexing the quanta time line. At the beginning, it will be synchronized with 'i' but then it will
        //start getting ahead.
        int turnAroundSum = 0;
        int sumWait = 0;
        int sumOfCompletedProcesses = 0;
        
        while (i < listOfProcesses.size()) {
            
            if (j == quantaLine.size())
            {
                
                break;
            }
            
            sumWait += j;
            
            FCFSResults.addToTimeline(listOfProcesses.get(i).getProcessName());
            
            sumOfCompletedProcesses++;
            
            float processTimeRem = listOfProcesses.get(i).getExpRunTime();
            
            if (processTimeRem > quantaSize)
            {
                int additonalQuantas = (int) Math.ceil(processTimeRem);
                j++;
                for (int k = 1; k < additonalQuantas; k++ )
                {
                    if (j >= quantaLine.size())
                    {
                        quantaLine.add(new Quanta());
                        
                    }
                    FCFSResults.addToTimeline(listOfProcesses.get(i).getProcessName());
                    j++;
                    
                    
                    //turnAroundSum += j;
                    //System.out.println("<><> " + turnAroundSum);
                }
                turnAroundSum += j;
                i++;
                continue;
            }
            
            i++;
            j++;
            
            turnAroundSum += j;
            //System.out.println("<><> " + turnAroundSum);
            
        }
        
        //System.out.println(">>" + sumOfCompletedProcesses);
        FCFSResults.setNumOfCompletedProcesses(sumOfCompletedProcesses);
        
        
        FCFSResults.setSumOfTurnaround(turnAroundSum);
        
        int sum2 = 0;
        for (int k = 0; k < setWaitingTimes.size(); k++)
            sum2 += setWaitingTimes.get(k);
        
        FCFSResults.setSumOfWaiting(sumWait);
        FCFSResults.setSumOfResponse(sumWait);
        
    }
    
    //Shortest Job First
    public void SJF (ArrayList<Process> listOfProcesses){
    	
        SJFsort(listOfProcesses); //sorted by ascending expected runtime
        SJFResults = new SimResults();
        
        double turnAroundTime = 0;
        double waitingTime = 0;
        int completedProcesses = 0;
        
        ArrayList<Integer> setTurnAroundTimes = new ArrayList <Integer> ();
        ArrayList<Integer> setWaitingTimes = new ArrayList <Integer> ();
        ArrayList<Integer> indexesToRemove = new ArrayList <Integer> ();
        Queue <Process> arrivedProcesses = new LinkedList <Process> ();
        
        int j = 0;
        
        while (true) {
            
            
            if (j >= QUANTA_LIMIT || listOfProcesses.size() == 0)
                break;
            
            int m = 0;
            indexesToRemove = new ArrayList <Integer> ();
            arrivedProcesses = new LinkedList <Process> (); //reset the Queue, or clear it
            while (m < listOfProcesses.size())
            {
                
                int arrTime = (int) Math.floor(listOfProcesses.get(m).getArrivalTime());
                double debugTime =  listOfProcesses.get(m).getArrivalTime();
                //System.out.println(">>" + listOfProcesses.get(m).getProcessName() + "-" + listOfProcesses.get(m).getArrivalTime());
                if (arrTime <= j) {
                    
                    arrivedProcesses.add(listOfProcesses.get(m));
                    indexesToRemove.add(m);
                }
                m++;
            }
            
            //remove processes
            for (int i = 0; i < indexesToRemove.size(); i++){
                
                int x = indexesToRemove.get(i);
                x =  x-i ;
                listOfProcesses.remove(x);
            }
            
            if (arrivedProcesses.isEmpty()){
                
                SJFResults.addToTimeline("- idle -");
                j++;
                if (j >= QUANTA_LIMIT)
                    break;
                continue;
            }
            
            while (true)
            {
                
                Process p = arrivedProcesses.poll();
                if (p == null) break;
                completedProcesses++;
                waitingTime += j;
                int additionalQuantas = (int) Math.ceil(p.getExpRunTime());
                
                for (int k = 1; k <= additionalQuantas; k++)
                {
                    if (j >= quantaLine.size()) //need to expand quanta line
                    {
                        quantaLine.add(new Quanta());
                    }
                    
                    
                    SJFResults.addToTimeline(p.getProcessName());;
                    j++;
                }
                turnAroundTime += j - p.getArrivalTime();
                if (j >= QUANTA_LIMIT)
                    break;
            }
        }
        
        SJFResults.setNumOfCompletedProcesses(completedProcesses);
        SJFResults.setSumOfTurnaround(turnAroundTime);
        SJFResults.setSumOfWaiting(waitingTime);
        SJFResults.setSumOfResponse(waitingTime);
    }
    
    //Shortest Remaining Time
    public void SRT (ArrayList<Process> readyQueue){
    	
        //Sort by arrival time
        sortByArrivalTime(readyQueue);
        
        //Create process queue with estimated run time for 100 quanta only
    	Queue<Process> processQueue = getBatchProcesses(readyQueue);
    	
    	//Driver.printStuff(processQueue);
        
        //Convert readyQueue to actual queue structure
    	Deque<Process> processes = convertToDeque(processQueue);
        
        //Used to store suspended processes
        Stack<Process> suspendedProcesses = new Stack<Process>();
        
        //Initialize for sim results
    	SRTResults = new SimResults();
        double turnaroundT = 0.0, responseT = 0.0;
        int throughput = processes.size(), quantaCount = 0, waitingT = 0;
        
        while(processes.size() > 0 && quantaCount < QUANTA_LIMIT){
        	
        	Process currP = processes.poll();
        	
        	//idling due to no jobs to process
        	while((int)currP.getArrivalTime() > quantaCount){
        		SRTResults.addToTimeline("- idle -");
    			quantaCount++;
        	}
        	
        	//if job has never been paused, it means its the first time its being processed
        	if(!currP.isPaused()){
        		
        		//don't include response time for job processed at first quanta since it should be 0 time response
        		if(quantaCount != 1){
        			responseT += quantaCount - (int)currP.getArrivalTime();
        			waitingT += quantaCount - (int)currP.getArrivalTime();
        		}
        	}
        	
        	//process current job until complete or a job with a shorter running time arrives
        	while((int)Math.ceil(currP.getExpRunTime()) > 0){
        		
        		//if job was previously suspended, update waiting time on resume
        		if(currP.isPaused()){
        			waitingT += quantaCount - currP.getPausedTime();
        			currP.resume();
        		}
        		
        		//check if there is another job
        		if(processes.size() > 0){
        			//if next job has arrived in ready queue, check if next job's expected run time is shorter than current job run time
            		if(processes.peek().getArrivalTime() <= quantaCount && processes.peek().getExpRunTime() < currP.getExpRunTime()){
            			
            			//suspends current job and stores it for later
            			currP.pause(quantaCount);// one less because it was paused in previous quanta
            			suspendedProcesses.push(currP);
            			break;
            		}
        		}
        		
        		//decrement run time, add to time line, and increment to next quanta
        		currP.decrementExpRunTime();
        		SRTResults.addToTimeline(currP.getProcessName());
        		quantaCount++;
        		
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
    
 
    
    //Round Robin
    public void RR (ArrayList<Process> readyQueue){
    	
    	//max quantum time
    	final int TIME_QUANTUM = 1;
    	
    	//Sort by arrival time
    	sortByArrivalTime(readyQueue);
    	
    	//Create process queue with estimated run time
    	Queue<Process> processes = getBatchProcesses(readyQueue);
    	
    	//Print random generated processes for simulation
    	//Driver.printStuff(readyQueue);
    	
    	//Initialize results set
        RRResults = new SimResults();
        double turnaroundT = 0.0, responseT = 0.0;
        int throughput = processes.size(), quantaCount = 1, waitingT = 0;
        
        while(processes.size() > 0){
        	
        	Process p = processes.poll();
        	
        	//idling due to no jobs to process
        	while((int)p.getArrivalTime() > quantaCount){
        		RRResults.addToTimeline("- idle -");
    			quantaCount++;
        	}
        	
        	if(p.isPaused()){
        		//if job was previously paused, add to waiting time
        		waitingT += quantaCount - p.getPausedTime();
        		p.resume();
        	}else if(quantaCount != 1) {
        		//if job was not previously paused (and its' not the first quanta), its the first time the job is processed
        		responseT += quantaCount - (int)p.getArrivalTime();
        		waitingT += (quantaCount - 1) - (int)p.getArrivalTime();
        	}
        	
    		//process the job based on quantum limit
			int quantumLimit = 0;
			while(true){
				
				if((int)Math.ceil(p.getExpRunTime()) > 0){
					//if time quantum is reached, pause and move current job to the end of the queue
					if(quantumLimit == TIME_QUANTUM && processes.size() > 0){
						p.pause(quantaCount);
						processes.add(p);
						break;
					}
					
					//add job name to time line, decrement job expected run time
					RRResults.addToTimeline(p.getProcessName());
					p.decrementExpRunTime();
					quantaCount++;
					quantumLimit++;
				}
				else{
					//current job is completed, add to turn around time
					turnaroundT += (quantaCount - 1) - p.getArrivalTime();// one less because it was paused in previous quanta
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
    
    //Highest Priority First Preemptive
    public void HPFPre (ArrayList<Process> readyQueue){
    	//max quantum time
    	final int TIME_QUANTUM = 1;
    	
    	//Sort by arrival time
    	sortByArrivalTime(readyQueue);
    	
    	//Create process queue with estimated run time
    	ArrayList<Process> processes = getBatchProcessesList(readyQueue);
    	
    	//Separate into priority classes
    	Queue <Process> priority1 = new LinkedList<Process> ();
    	Queue <Process> priority2 = new LinkedList <Process> ();
    	Queue <Process> priority3 = new LinkedList <Process> ();
    	Queue <Process> priority4 = new LinkedList <Process> ();
    	for (Process p : processes)
    	{
    		int priority = p.getPriority();
    		
    		if (priority == 1)
    		{
    			priority1.add(p);
    		}
    		else if (priority == 2)
    		{
    			priority2.add(p);
    		}
    		else if (priority == 3)
    		{
    			priority3.add(p);
    		}
    		else if (priority == 4)
    		{
    			priority4.add(p);
    		}
    	}
    	
    	/*
    	Driver.printStuff(priority1);
    	Driver.printStuff(priority2);
    	Driver.printStuff(priority3);
    	Driver.printStuff(priority4);
    	*/
    	
    	//Initialize results set
        HPFPreResults = new SimResults();
        int  quantaCount = 1, i = 0;
        
        //calculate individual stats for each priority class
        int throughputP1 = priority1.size(), throughputP2 = priority2.size(), throughputP3 = priority3.size(), throughputP4 = priority4.size();
        double turnaroundP1 = 0.0, turnaroundP2 = 0.0, turnaroundP3 = 0.0, turnaroundP4 = 0.0;
        double responseP1 = 0.0, responseP2 = 0.0, responseP3 = 0.0, responseP4 = 0.0;
        int waitingP1 = 0, waitingP2 = 0, waitingP3 = 0, waitingP4 = 0;
        
        
        //priority 1 (highest)
        while((priority1.size() > 0 || priority2.size() > 0 || priority3.size() > 0 | priority4.size() > 0) && (quantaCount < QUANTA_LIMIT)){
        	boolean finished = false;
        	if(priority1.size() > 0){
        		
        		//if job has arrived
        		if((int)priority1.peek().getArrivalTime() <= quantaCount){
        			
        			if(priority1.peek().isPaused()){
                		//if job was previously paused, add to waiting time
                		waitingP1 += quantaCount - priority1.peek().getPausedTime();
                		priority1.peek().resume();
                	}else if(quantaCount != 1) {
                		//if job was not previously paused (and its' not the first quanta), its the first time the job is processed
                		responseP1 += quantaCount - (int)priority1.peek().getArrivalTime();
                		waitingP1 += (quantaCount - 1) - (int)priority1.peek().getArrivalTime();
                	}
        			
        			//process the job based on quantum limit
        			int quantumLimit = 0;
        			while(true){
        				
        				if((int)Math.ceil(priority1.peek().getExpRunTime()) > 0){
        					//if time quantum is reached, pause and move current job to the end of the queue
        					if(quantumLimit == TIME_QUANTUM && priority1.size() > 0){
        						priority1.peek().pause(quantaCount);
        						priority1.add(priority1.poll());
        						break;
        					}
        					
        					//add job name to time line, decrement job expected run time
        					HPFPreResults.addToTimeline(priority1.peek().getProcessName());
        					priority1.peek().decrementExpRunTime();
        					quantaCount++;
        					quantumLimit++;
        				}
        				else{
        					Process p = priority1.poll();
        					//current job is completed, add to turn around time
        					turnaroundP1 += (quantaCount - 1) - p.getArrivalTime();// one less because it was paused in previous quanta
        					deleteProcess(processes, p);
        					break;
        				}
        			}
        			finished = true;
        		}
        	}
        	
        	//priority 2
        	if(priority2.size() > 0 && !finished){
        		//if job has arrived
        		if((int)priority2.peek().getArrivalTime() <= quantaCount){
        			
        			if(priority2.peek().isPaused()){
                		//if job was previously paused, add to waiting time
                		waitingP2 += quantaCount - priority2.peek().getPausedTime();
                		priority2.peek().resume();
                	}else if(quantaCount != 1) {
                		//if job was not previously paused (and its' not the first quanta), its the first time the job is processed
                		responseP2 += quantaCount - (int)priority2.peek().getArrivalTime();
                		waitingP2 += (quantaCount - 1) - (int)priority2.peek().getArrivalTime();
                	}
        			
        			//process the job based on quantum limit
        			int quantumLimit = 0;
        			while(true){
        				
        				if((int)Math.ceil(priority2.peek().getExpRunTime()) > 0){
        					//if time quantum is reached, pause and move current job to the end of the queue
        					if(quantumLimit == TIME_QUANTUM && priority2.size() > 0){
        						priority2.peek().pause(quantaCount);
        						priority2.add(priority2.poll());
        						break;
        					}
        					
        					//add job name to time line, decrement job expected run time
        					HPFPreResults.addToTimeline(priority2.peek().getProcessName());
        					priority2.peek().decrementExpRunTime();
        					quantaCount++;
        					quantumLimit++;
        				}
        				else{
        					//current job is completed, add to turn around time
        					Process p = priority2.poll();
        					turnaroundP2 += (quantaCount - 1) - p.getArrivalTime();// one less because it was paused in previous quanta
        					deleteProcess(processes, p);
        					break;
        				}
        			}
        			//break to go to next job
        			finished = true;
        		}
        	}
        	
        	//priority 3
        	if(priority3.size() > 0 && !finished){
        		//if job has arrived
        		if((int)priority3.peek().getArrivalTime() <= quantaCount){
        			
        			if(priority3.peek().isPaused()){
                		//if job was previously paused, add to waiting time
                		waitingP3 += quantaCount - priority3.peek().getPausedTime();
                		priority3.peek().resume();
                	}else if(quantaCount != 1) {
                		//if job was not previously paused (and its' not the first quanta), its the first time the job is processed
                		responseP3 += quantaCount - (int)priority3.peek().getArrivalTime();
                		waitingP3 += (quantaCount - 1) - (int)priority3.peek().getArrivalTime();
                	}
        			
        			//process the job based on quantum limit
        			int quantumLimit = 0;
        			while(true){
        				
        				if((int)Math.ceil(priority3.peek().getExpRunTime()) > 0){
        					//if time quantum is reached, pause and move current job to the end of the queue
        					if(quantumLimit == TIME_QUANTUM && priority3.size() > 0){
        						priority3.peek().pause(quantaCount);
        						priority3.add(priority3.poll());
        						break;
        					}
        					
        					//add job name to time line, decrement job expected run time
        					HPFPreResults.addToTimeline(priority3.peek().getProcessName());
        					priority3.peek().decrementExpRunTime();
        					quantaCount++;
        					quantumLimit++;
        				}
        				else{
        					//current job is completed, add to turn around time
        					Process p = priority3.poll();
        					turnaroundP3 += (quantaCount - 1) - p.getArrivalTime();// one less because it was paused in previous quanta
        					deleteProcess(processes, p);
        					break;
        				}
        			}
        			finished = true;
        		}
        	}
        	
        	//priority 4
        	if(priority4.size() > 0 && !finished){
        		//if job has arrived
        		if((int)priority4.peek().getArrivalTime() <= quantaCount){
        			
        			if(priority4.peek().isPaused()){
                		//if job was previously paused, add to waiting time
                		waitingP4 += quantaCount - priority4.peek().getPausedTime();
                		priority4.peek().resume();
                	}else if(quantaCount != 1) {
                		//if job was not previously paused (and its' not the first quanta), its the first time the job is processed
                		responseP4 += quantaCount - (int)priority4.peek().getArrivalTime();
                		waitingP4 += (quantaCount - 1) - (int)priority4.peek().getArrivalTime();
                	}
        			
        			//process the job based on quantum limit
        			int quantumLimit = 0;
        			while(true){
        				
        				if((int)Math.ceil(priority4.peek().getExpRunTime()) > 0){
        					//if time quantum is reached, pause and move current job to the end of the queue
        					if(quantumLimit == TIME_QUANTUM && priority4.size() > 0){
        						priority4.peek().pause(quantaCount);
        						priority4.add(priority4.poll());
        						break;
        					}
        					
        					//add job name to time line, decrement job expected run time
        					HPFPreResults.addToTimeline(priority4.peek().getProcessName());
        					priority4.peek().decrementExpRunTime();
        					quantaCount++;
        					quantumLimit++;
        				}
        				else{
        					//current job is completed, add to turn around time
        					Process p = priority4.poll();
        					turnaroundP4 += (quantaCount - 1) - p.getArrivalTime();// one less because it was paused in previous quanta
        					deleteProcess(processes, p);
        					break;
        				}
        			}
        			finished = true;
        		}
        	}
        	
        	//idling due to no jobs to process
        	if(!finished && quantaCount < QUANTA_LIMIT){
        		HPFPreResults.addToTimeline("- idle -");
    			quantaCount++;
        	}
        }
        
        System.out.println("(Priority 1) Average turnaround time: " + ((double)turnaroundP1 / (double)throughputP1));
        System.out.println("(Priority 1) Average waiting time: " + ((double)waitingP1 / (double)throughputP1));
        System.out.println("(Priority 1) Average response time: " + ((double)responseP1 / (double)throughputP1));
        System.out.println("(Priority 1) Throughput: " + throughputP1);
        
        System.out.println("(Priority 2) Average turnaround time: " + ((double)turnaroundP2 / (double)throughputP2));
        System.out.println("(Priority 2) Average waiting time: " + ((double)waitingP2 / (double)throughputP2));
        System.out.println("(Priority 2) Average response time: " + ((double)responseP2 / (double)throughputP2));
        System.out.println("(Priority 2) Throughput: " + throughputP2);
        
        System.out.println("(Priority 3) Average turnaround time: " + ((double)turnaroundP3 / (double)throughputP3));
        System.out.println("(Priority 3) Average waiting time: " + ((double)waitingP3 / (double)throughputP3));
        System.out.println("(Priority 3) Average response time: " + ((double)responseP3 / (double)throughputP3));
        System.out.println("(Priority 3) Throughput: " + throughputP3);
        
        System.out.println("(Priority 4) Average turnaround time: " + ((double)turnaroundP4 / (double)throughputP4));
        System.out.println("(Priority 4) Average waiting time: " + ((double)waitingP4 / (double)throughputP4));
        System.out.println("(Priority 4) Average response time: " + ((double)responseP4 / (double)throughputP4));
        System.out.println("(Priority 4) Throughput: " + throughputP4);
        
        System.out.println();
      
        //Set stats for results calculation
        HPFPreResults.setNumOfCompletedProcesses(throughputP1 + throughputP2 + throughputP3 + throughputP4);
        HPFPreResults.setSumOfTurnaround(turnaroundP1 + turnaroundP2 + turnaroundP3 + turnaroundP4);
        HPFPreResults.setSumOfWaiting(waitingP1 + waitingP2 + waitingP3 + waitingP4);
        HPFPreResults.setSumOfResponse(responseP1 + responseP2 + responseP3 + responseP4);
    }
    
    //Delete process from array list - helper function for HPF preemptive
    private void deleteProcess(ArrayList<Process> processes, Process find){
    	for(int i = 0; i < processes.size(); i++){
    		if(find.equals(processes.get(i))){
    			processes.remove(i);
    			break;
    		}
    	}
    }
    
    //Highest Priority First Non-Preemptive
    public void HPFNonPre (ArrayList<Process> readyQueue){
    	
    	HPFNonPreResults = new SimResults();
    	
    	//Sort by arrival time
    	sortByArrivalTime(readyQueue);
    	
    	//Create process queue with estimated run time
    	ArrayList<Process>processes = getBatchProcessesList(readyQueue);
    	
    	ArrayList <Process> priority1 = new ArrayList<Process> ();
    	ArrayList <Process> priority2 = new ArrayList <Process> ();
    	ArrayList <Process> priority3 = new ArrayList <Process> ();
    	ArrayList <Process> priority4 = new ArrayList <Process> ();
    	
    	for (int i = 0; i < processes.size(); i++)
    	{
    		int priority = processes.get(i).getPriority();
    		
    		if (priority == 1)
    		{
    			priority1.add(processes.get(i));
    			sortByArrivalTime(priority1);
    		}
    		else if (priority == 2)
    		{
    			priority2.add(processes.get(i));
    			sortByArrivalTime(priority2);
    		}
    		else if (priority == 3)
    		{
    			priority3.add(processes.get(i));
    			sortByArrivalTime(priority3);
    		}
    		else if (priority == 4)
    		{
    			priority4.add(processes.get(i));
    			sortByArrivalTime(priority4);
    		}
 		
    	}
    	
    	//calculate individual stats for each priority class
        int throughputP1 = priority1.size(), throughputP2 = priority2.size(), throughputP3 = priority3.size(), throughputP4 = priority4.size();
        double turnaroundP1 = 0.0, turnaroundP2 = 0.0, turnaroundP3 = 0.0, turnaroundP4 = 0.0;
        double responseP1 = 0.0, responseP2 = 0.0, responseP3 = 0.0, responseP4 = 0.0;
        int waitingP1 = 0, waitingP2 = 0, waitingP3 = 0, waitingP4 = 0;
    	
    	int j = 0;
    	Queue <Process> arrived1 = new LinkedList <Process> ();
    	Queue <Process> arrived2 = new LinkedList <Process> ();
    	Queue <Process> arrived3 = new LinkedList <Process> ();
    	Queue <Process> arrived4 = new LinkedList <Process> ();
    	
    	while (j <= QUANTA_LIMIT)
    	{
    		
    		arrived1 = new LinkedList <Process> ();
        	arrived2 = new LinkedList <Process> ();
        	arrived3 = new LinkedList <Process> ();
        	arrived4 = new LinkedList <Process> ();
    		
    		
    		//The followng 4 'for loops' gather processes within current quanta slot (j)
    		for (int k = 0; k < priority1.size(); k++)
    		{
    			if (Math.floor(priority1.get(k).getArrivalTime()) <= j)
    				arrived1.add(priority1.get(k));	
    		}
    		for (int k = 0; k < priority2.size(); k++)
    		{
    			if (Math.floor(priority2.get(k).getArrivalTime()) <= j)
    				arrived2.add(priority2.get(k));	
    		}
    		for (int k = 0; k < priority3.size(); k++)
    		{
    			if (Math.floor(priority3.get(k).getArrivalTime()) <= j)
    				arrived3.add(priority3.get(k));	
    		}
    		for (int k = 0; k < priority4.size(); k++)
    		{
    			if (Math.floor(priority4.get(k).getArrivalTime()) <= j)
    				arrived4.add(priority4.get(k));	
    		}
    		
    		//check for idle slot of (j)
    		
    		if(arrived1.isEmpty() && arrived2.isEmpty() && arrived3.isEmpty() && arrived4.isEmpty())
    		{
    			HPFNonPreResults.addToTimeline("- idle -");
    			j++;
    			continue;
    		}
    		
    		
    		while (true) 
        	{
        		//break when there are no more processes that have arrived
        		if(arrived1.isEmpty() && arrived2.isEmpty() && arrived3.isEmpty() && arrived4.isEmpty() ) break; 
        		
        		if (j >= QUANTA_LIMIT)
        			break;
        		
        		
        		while(!arrived1.isEmpty())
        		{
        			Process p = arrived1.poll(); 
        			int additionalQuantas = (int) Math.ceil(p.getExpRunTime());
        			HPFnonPHelper(HPFNonPreResults, additionalQuantas, p.getProcessName());
        			
        			j += additionalQuantas;
        		}
        		
        		//System.out.println("1 Done " + j);
        		
        		if (j >= QUANTA_LIMIT)
        			break;
        		
        		while (!arrived2.isEmpty())
        		{
        			Process p = arrived2.poll(); 
        			int additionalQuantas = (int) Math.ceil(p.getExpRunTime());
        			HPFnonPHelper(HPFNonPreResults, additionalQuantas, p.getProcessName());
        			
        			j += additionalQuantas;
        		}
        		
        		//System.out.println("2 Done " + j);
        		
        		if (j >= QUANTA_LIMIT)
        			break;
        		while (!arrived3.isEmpty())
        		{
        			Process p = arrived3.poll(); 
        			int additionalQuantas = (int) Math.ceil(p.getExpRunTime());
        			HPFnonPHelper(HPFNonPreResults, additionalQuantas, p.getProcessName());
        			
        			j += additionalQuantas;
        		}
        		
        		//System.out.println("3 Done " + j);

        		
        		if (j >= QUANTA_LIMIT)
        			break;
        		
        		while (!arrived4.isEmpty())
        		{
        			Process p = arrived4.poll(); 
        			int additionalQuantas = (int) Math.ceil(p.getExpRunTime());
        			HPFnonPHelper(HPFNonPreResults, additionalQuantas, p.getProcessName());
        			
        			j += additionalQuantas;
        		}
        		
        		//System.out.println("4 Done " + j);
        		
        		if (j >= QUANTA_LIMIT)
        			break;
        	}    		
    	}
    	
    	System.out.println("(Priority 1) Average turnaround time: " + ((double)turnaroundP1 / (double)throughputP1));
        System.out.println("(Priority 1) Average waiting time: " + ((double)waitingP1 / (double)throughputP1));
        System.out.println("(Priority 1) Average response time: " + ((double)responseP1 / (double)throughputP1));
        System.out.println("(Priority 1) Throughput: " + throughputP1);
        
        System.out.println("(Priority 2) Average turnaround time: " + ((double)turnaroundP2 / (double)throughputP2));
        System.out.println("(Priority 2) Average waiting time: " + ((double)waitingP2 / (double)throughputP2));
        System.out.println("(Priority 2) Average response time: " + ((double)responseP2 / (double)throughputP2));
        System.out.println("(Priority 2) Throughput: " + throughputP2);
        
        System.out.println("(Priority 3) Average turnaround time: " + ((double)turnaroundP3 / (double)throughputP3));
        System.out.println("(Priority 3) Average waiting time: " + ((double)waitingP3 / (double)throughputP3));
        System.out.println("(Priority 3) Average response time: " + ((double)responseP3 / (double)throughputP3));
        System.out.println("(Priority 3) Throughput: " + throughputP3);
        
        System.out.println("(Priority 4) Average turnaround time: " + ((double)turnaroundP4 / (double)throughputP4));
        System.out.println("(Priority 4) Average waiting time: " + ((double)waitingP4 / (double)throughputP4));
        System.out.println("(Priority 4) Average response time: " + ((double)responseP4 / (double)throughputP4));
        System.out.println("(Priority 4) Throughput: " + throughputP4);
        
         System.out.println();
       
         //Set stats for results calculation
         HPFNonPreResults.setNumOfCompletedProcesses(throughputP1 + throughputP2 + throughputP3 + throughputP4);
         HPFNonPreResults.setSumOfTurnaround(turnaroundP1 + turnaroundP2 + turnaroundP3 + turnaroundP4);
         HPFNonPreResults.setSumOfWaiting(waitingP1 + waitingP2 + waitingP3 + waitingP4);
         HPFNonPreResults.setSumOfResponse(responseP1 + responseP2 + responseP3 + responseP4);
    }
    
    private static void  HPFnonPHelper(SimResults sr, int additional, String pName)
    {
    	for (int i = 1; i <= additional; i++ )
    	 {
    		sr.addToTimeline(pName);
    	 }
    		
    }
    
    //Sort array list by arrival time
    public static void sortByArrivalTime(ArrayList<Process> processes) {
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
    
    //Helper function to approximate number of processes for 100 quanta based on estimate of expected run time
    private static ArrayList<Process> getBatchProcessesList(ArrayList<Process> processes){
    	ArrayList<Process> validProcesses = new ArrayList<Process>();
    	int sumOfRuntime = 0;
    	
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
