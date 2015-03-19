import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Swapping {
	
	private int memSize; //main memory size
	private int runtime; //simulation run time
	private Queue<Process> readyQueue; //ready queue of processes
	
	LinkedList<Partition> mainMem; //main memory
	Map<String, Process> inMem; //memory tracker used to keep track of process running time

	public Swapping(Queue<Process> readyQueue, int memSize, int runtime){
		this.readyQueue = readyQueue;
		this.memSize = memSize;
		this.runtime = runtime;
	}
	
	//TODO: Keith
	//Finds the first hole big enough
	public int firstFit(){
		
		System.out.println("Time | Swap | PID | Size | Dur. | Memory Map\n-------------------------------------------------");
		Queue<Process> tempReadyQueue = readyQueue; //Ready queue
		
		//Re-initialize main memory and memory tracker
		mainMem = createMemManager(); //main memory
		inMem = new HashMap<String, Process>(); //memory tracker
		
		int swapCount = 0; //count number of processes swapped in
		
		//print free memory map
		int currTime = 0;
		print("", currTime, null);
		
		currTime++;
		while(currTime < runtime && tempReadyQueue.size() > 0){
			
			//Perform swap out if process is finish in main memory
			swapOut(currTime);
			
			//Check for free partition hole
			int requiredSize = tempReadyQueue.peek().getSize(); //process size
			for(int pIndex = 0; pIndex < mainMem.size(); pIndex++){
				
				int freeSize = 0; //counter to keep track of consecutive free partitions
				boolean found = false; //flag if free partition hole was found
				
				//If partition is free, check if consecutive partitions are free
				if(mainMem.get(pIndex).isFree()){
					do{
						if(mainMem.get(pIndex).isFree()){
							freeSize++; //counting consecutive free partition
							
							//Set found to true if process fits in the free partition(s)
							if(freeSize == requiredSize){
								found = true;
								break;
							}
						}else{
							//break if not enough consecutive partitions are free
							break;
						}
						pIndex++;
					}while(pIndex < mainMem.size());
					
					//If hole is found, swap in
					if(found){
						Process p = tempReadyQueue.poll(); //Remove process from queue for swapping
						
						//Calculate starting and ending partition
						int startIndex = (pIndex + 1) - requiredSize ;
						p.setFirstPartition(startIndex);
						p.setLastPartition(pIndex);
						
						//add process to memory tracker
						inMem.put(p.getPID(), p); 
						
						//Swapping in process
						while(startIndex <= pIndex){
							mainMem.get(startIndex).setPID(p.getPID());
							mainMem.get(startIndex).isFree(false);
							startIndex++;
						}
						
						swapCount++;
						print("in", currTime, p);
						break;
					}
				}
			}
			//increment running time
			currTime++;
		}
		return swapCount;
	}
	
	//TODO: Keith
	//Searches for the next hole, continuing from the previous search point
	public int nextFit(){
		
		System.out.println("Time | Swap | PID | Size | Dur. | Memory Map\n-------------------------------------------------");
		Queue<Process> tempReadyQueue = readyQueue; //Ready queue
		
		//Re-initialize main memory and memory tracker
		mainMem = createMemManager(); //main memory
		inMem = new HashMap<String, Process>(); //memory tracker
		
		int swapCount = 0; //count number of processes swapped in
		int searchIndex = 0; //keeps track of current search index
		
		//print free memory map
		int currTime = 0;
		print("", currTime, null);
		
		currTime++;
		while(currTime < runtime && tempReadyQueue.size() > 0){
			
			//Perform swap out if process is finish in main memory
			swapOut(currTime);
			
			//Check for free partition hole
			int requiredSize = tempReadyQueue.peek().getSize(); //process size
			int pIndex = searchIndex;
			
			while(pIndex < mainMem.size()){
				
				int freeSize = 0; //counter to keep track of consecutive free partitions
				boolean found = false; //flag if free partition hole was found
				
				//If partition is free, check if consecutive partitions are free
				if(mainMem.get(pIndex).isFree()){
					do{
						if(mainMem.get(pIndex).isFree()){
							freeSize++; //counting consecutive free partition
							
							//Set found to true if process fits in the free partition(s)
							if(freeSize == requiredSize){
								found = true;
								break;
							}
						}else{
							//break if not enough consecutive partitions are free
							break;
						}
						pIndex++;
					}while(pIndex < mainMem.size());
					
					//If hole is found, swap in
					if(found){
						Process p = tempReadyQueue.poll(); //Remove process from queue for swapping
						
						//Calculate starting and ending partition
						int startIndex = (pIndex + 1) - requiredSize ;
						p.setFirstPartition(startIndex);
						p.setLastPartition(pIndex);
						
						//set last search index
						searchIndex = pIndex + 1;
						
						//add process to memory tracker
						inMem.put(p.getPID(), p); 
						
						//Swapping in process
						while(startIndex <= pIndex){
							mainMem.get(startIndex).setPID(p.getPID());
							mainMem.get(startIndex).isFree(false);
							startIndex++;
						}
						
						swapCount++;
						print("in", currTime, p);
						break;
					}
				}
				
				//If process has been swapped in, break out of search loop
				if(found)
					break;
				
				//If next index has wrapped around, break out of search loop
				if((pIndex + 1) == searchIndex)
					break;
				
				//if the next index has reached the end of main memory partitions, wrap around and continue search
				if((pIndex + 1) >= mainMem.size())
					pIndex = -1;
				
				pIndex++;
			}
			//increment running time
			currTime++;
		}
		return swapCount;
	}
	
	//TODO: Keith
	//Search for the smallest hole that is big enough
	public void bestFit(){
		
	}
	
	//TODO: Graeme
	public void worstFit(){
		
	}
	
	//Helper method to perform checking and swap out
	public void swapOut(int currTime){
		//Traverse through main memory to check for processes that need to be swapped out
		for(int pIndex = 0; pIndex < mainMem.size(); pIndex++){
			
			//If partition is not free, it means a process is running in that partition
			if(!mainMem.get(pIndex).isFree()){
				
				//Get the process from the memory tracker
				Process p = inMem.get(mainMem.get(pIndex).getPID());
				
				//If the process is still running, decrement the running time
				if(p.getDuration() > 0){
					
					p.decrementDuration(); //decrement running time
					
					//if process is finished, swap out
					if(p.getDuration() == 0){
						
						//Swap out process, set partition(s) to free
						while(pIndex <= p.getLastPartition()){
							mainMem.get(pIndex).clear();
							pIndex++;
						}
						pIndex--;//adjust current partition since we jumped forward one partition
						print("out", currTime, p);
						inMem.remove(p); //remove process from memory tracker
					}else{
						//skip to the last partition the current process is allocated
						pIndex = p.getLastPartition();
					}
				}
			}
		}
	}
	
	//Print method to print memory map on swap in and swap out
	public void print(String event, int time, Process p){
		//Get memory map
		StringBuilder memMap = new StringBuilder();
		for(Partition partition : mainMem){
			memMap.append(partition.getPID() + " ");
		}
		
		//Output process being swapped in or out
		//Output current memory map
		if(event.equals("in")){
			System.out.printf(" %02d  | in   | %s | %02d   | %d    | %s\n", time, p.getPID(), p.getSize(), p.getDuration(), memMap.toString());
		}else if(event.equals("out")){
			System.out.printf(" %02d  | out  | %s |      |      | %s\n", time, p.getPID(), memMap.toString());
		}else{
			System.out.printf(" %02d  |      |     |      |      | %s\n", time, memMap.toString());
		}
	}
	
	//Helper method to re-initialize main memory manager
	//Creates partition in main memory
	public LinkedList<Partition> createMemManager(){
		//Initialize main memory and set each unit to free
		LinkedList<Partition> mainMem = new LinkedList<Partition>();
		for(int i = 0; i < memSize; i++)
			mainMem.add(new Partition());
		return mainMem;
	}
	
	//Inner class to handle partition info
	private class Partition{
		private boolean free;
		private String pid;
		public Partition(){
			free = true;
			pid = ".";
		}
		public void clear(){
			free = true;
			pid = ".";
		}
		public boolean isFree(){
			return free;
		}
		public void isFree(boolean free){
			this.free = free;
		}
		public String getPID(){
			return pid;
		}
		public void setPID(String pid){
			this.pid = pid;
		}
	}
}


