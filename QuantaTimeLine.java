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
    
    //TODO: Keith --- COMPLETED
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
                
                //decrement run time, add to time line, and increment to next quanta
                currP.decrementExpRunTime();
                SRTResults.addToTimeline(currP.getProcessName());
                quantaCount++;
                
                //check if there is another job
                if(processes.size() > 0){
                    
                    //if next job has arrived in ready queue, check if next job's expected run time is shorter than current job run time
                    if((int)processes.peek().getArrivalTime() <= quantaCount && processes.peek().getExpRunTime() < currP.getExpRunTime()){
                        
                        //suspends current job and stores it for later
                        currP.pause(quantaCount);// one less because it was paused in previous quanta
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
    
    //TODO: Keith --- COMPLETED
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
    
    //TODO: Graeme
    public void HPFPre (ArrayList<Process> readyQueue){
    	Deque<Process> priority1 = new LinkedList<Process>();
    	Deque<Process> priority2 = new LinkedList<Process>();
    	Deque<Process> priority3 = new LinkedList<Process>();
    	Deque<Process> priority4 = new LinkedList<Process>();
        
        //Sort by arrival time
        sortByArrivalTime(readyQueue);
        
        //Create process queue with estimated run time for 100 quanta only
        Queue<Process> processQueue = getBatchProcesses(readyQueue);
        
        Driver.printStuff(processQueue);
        
        //Initialize for sim results
        HPFPreResults = new SimResults();
        double turnaroundT = 0.0, responseT = 0.0;
        int throughput = 0, quantaCount = 1, waitingT = 0;
        
        Process currP = null;
        int highP = 100;
        
        while(quantaCount < QUANTA_LIMIT){
            
        	while (!processQueue.isEmpty()) {
        		if ((int)Math.ceil(processQueue.peek().getArrivalTime()) > quantaCount) break;
        		
        		Process nextP = processQueue.poll();
        		int priority = nextP.getPriority();
        		
        		if (priority < highP) highP = priority;
        		
        		switch (priority) {
        		case 1:
        			priority1.offer(nextP);
        			break;
        		case 2:
        			priority2.offer(nextP);
        			break;
        		case 3:
        			priority3.offer(nextP);
        			break;
        		case 4:
        			priority4.offer(nextP);        			
        		}
        	}
            
        	if (currP == null) {
        		switch(highP) {
        		case 1:
        			currP = priority1.poll();
        			break;
        		case 2:
        			currP = priority2.poll();
        			break;
        		case 3:
        			currP = priority3.poll();
        			break;
        		case 4:
        			currP = priority4.poll();
        			break;
        		default:
        			HPFPreResults.addToTimeline("- idle -");
                    quantaCount++;
                    continue;
        		}
        	}
        	else if (highP < currP.getPriority()) {
        		currP.pause(quantaCount);
        		switch (currP.getPriority()) {
        		case 1:
        			priority1.offerFirst(currP);
        			break;
        		case 2:
        			priority2.offerFirst(currP);
        			break;
        		case 3:
        			priority3.offerFirst(currP);
        			break;
        		case 4:
        			priority4.offerFirst(currP);
        		}
        		
        		switch (highP) {
        		case 1:
        			currP = priority1.poll();
        			break;
        		case 2:
        			currP = priority2.poll();
        			break;
        		case 3:
        			currP = priority3.poll();
        			break;
        		case 4:
        			currP = priority4.poll();
        		}
        	}
        	
        	if (currP.isPaused()) {
        		waitingT += quantaCount - currP.getPausedTime();
                currP.resume();
        	}
        	else {
        		responseT += quantaCount - (int)currP.getArrivalTime();
                waitingT += quantaCount - (int)currP.getArrivalTime();
        	}
            
            //decrement run time, add to time line, and increment to next quanta
            currP.decrementExpRunTime();
            HPFPreResults.addToTimeline(currP.getProcessName());
            quantaCount++;
            
            if ((int)Math.ceil(currP.getExpRunTime()) <= 0) {
            	//calculate turnaround time and throughput
                throughput++;
                turnaroundT += (quantaCount - 1) - currP.getArrivalTime();// one less because it was paused in previous quanta
                currP = null;
                if (!priority1.isEmpty()) highP = 1;
            	else if (!priority2.isEmpty()) highP = 2;
            	else if (!priority3.isEmpty()) highP = 3;
            	else if (!priority4.isEmpty()) highP = 4;
            	else highP = 100;
            }
        }
        
        if (currP != null) {
        	while ((int)Math.ceil(currP.getExpRunTime()) > 0) {
        		//decrement run time, add to time line, and increment to next quanta
                currP.decrementExpRunTime();
                HPFPreResults.addToTimeline(currP.getProcessName());
                quantaCount++;
        	}
        	
        	throughput++;
            turnaroundT += (quantaCount - 1) - currP.getArrivalTime();// one less because it was paused in previous quanta
        }
        
        HPFPreResults.setNumOfCompletedProcesses(throughput);
        HPFPreResults.setSumOfTurnaround(turnaroundT);
        HPFPreResults.setSumOfWaiting(waitingT);
        HPFPreResults.setSumOfResponse(responseT);
    }
    
    //TODO: Graeme
    public void HPFNonPre (ArrayList<Process> readyQueue){
    	Queue<Process> priority1 = new LinkedList<Process>();
        Queue<Process> priority2 = new LinkedList<Process>();
        Queue<Process> priority3 = new LinkedList<Process>();
        Queue<Process> priority4 = new LinkedList<Process>();
        
        //Sort by arrival time
        sortByArrivalTime(readyQueue);
        
        //Create process queue with estimated run time for 100 quanta only
        Queue<Process> processQueue = getBatchProcesses(readyQueue);
        
        Driver.printStuff(processQueue);
        
        //Initialize for sim results
        HPFNonPreResults = new SimResults();
        double turnaroundT = 0.0, responseT = 0.0;
        int throughput = 0, quantaCount = 1, waitingT = 0;
        
        while(quantaCount < QUANTA_LIMIT){
            
        	while (!processQueue.isEmpty()) {
        		if ((int)Math.ceil(processQueue.peek().getArrivalTime()) > quantaCount) break;
        		
        		Process nextP = processQueue.poll();
        		switch (nextP.getPriority()) {
        		case 1:
        			priority1.offer(nextP);
        			break;
        		case 2:
        			priority2.offer(nextP);
        			break;
        		case 3:
        			priority3.offer(nextP);
        			break;
        		case 4:
        			priority4.offer(nextP);        			
        		}
        	}
            
        	Process currP = null;
        	if (!priority1.isEmpty()) currP = priority1.poll();
        	else if (!priority2.isEmpty()) currP = priority2.poll();
        	else if (!priority3.isEmpty()) currP = priority3.poll();
        	else if (!priority4.isEmpty()) currP = priority4.poll();
        	else {
        		HPFNonPreResults.addToTimeline("- idle -");
                quantaCount++;
                continue;
            }
            
        	responseT += quantaCount - (int)currP.getArrivalTime();
            waitingT += quantaCount - (int)currP.getArrivalTime();
            
            //process current job until complete
            while((int)Math.ceil(currP.getExpRunTime()) > 0){
                //decrement run time, add to time line, and increment to next quanta
                currP.decrementExpRunTime();
                HPFNonPreResults.addToTimeline(currP.getProcessName());
                quantaCount++;
            }
            
            //calculate turnaround time and throughput
            throughput++;
            turnaroundT += (quantaCount - 1) - currP.getArrivalTime();// one less because it was paused in previous quanta
        }
        
        HPFNonPreResults.setNumOfCompletedProcesses(throughput);
        HPFNonPreResults.setSumOfTurnaround(turnaroundT);
        HPFNonPreResults.setSumOfWaiting(waitingT);
        HPFNonPreResults.setSumOfResponse(responseT);
    }
    
    //Sort array list by arrival time
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
