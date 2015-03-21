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
		
		Queue<Process> tempReadyQueue = clone(readyQueue); //Ready queue
		System.out.println("Time | Swap | PID | Size | Dur. | Memory Map\n-------------------------------------------------");
		
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
			
			int requiredSize = tempReadyQueue.peek().getSize(); //process size
			int freeSize = 0; //counter to keep track of consecutive free partitions
			int pIndex = 0; //current partition index
			
			//Check for first free partition hole
			while(pIndex < mainMem.size()){
				
				//If partition is free, check to see if consecutive partitions are also free
				if(mainMem.get(pIndex).isFree()){
					
					//counting free partitions
					freeSize++; 
					
					//if process size fits in free partition(s)
					if(freeSize == requiredSize){
						
						//Remove process from queue for swapping
						Process p = tempReadyQueue.poll(); 
						
						//swap in process
						swapIn(currTime, p, pIndex);
						
						//increase swap count
						swapCount++;
						break;
					}
					
				}else{
					//not enough free consecutive partitions, reset counter
					freeSize = 0;
				}
				
				pIndex++;
			}
	
			//increment running time
			currTime++;
		}
		return swapCount;
	}
	
	//TODO: Keith
	//Searches for the next hole, continuing from the previous search point
	public int nextFit(){
		
		Queue<Process> tempReadyQueue = clone(readyQueue); //Ready queue
		System.out.println("Time | Swap | PID | Size | Dur. | Memory Map\n-------------------------------------------------");
		
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
			int freeSize = 0; //counter to keep track of consecutive free partitions
			int pIndex = searchIndex; //set current index to the last search index
			
			//Check for free consecutive partitions			
			do{
				if(mainMem.get(pIndex).isFree()){
					
					//count consecutive free partitions
					freeSize++;
					
					//if process size fits in free partition(s), swap in
					if(freeSize == requiredSize){
						
						//Remove process from queue for swapping
						Process p = tempReadyQueue.poll();
						
						//Swap in process
						swapIn(currTime, p, pIndex);
						
						//set last search index
						searchIndex = pIndex;
						
						//increase swap count
						swapCount++;
						break;
					}
				}else{
					//reset counter since consecutive partitions are not free
					freeSize = 0;
				}
				pIndex++;
				
				//if next index is out of range, wrap around
				if(pIndex == mainMem.size()){
					pIndex = 0;
					freeSize = 0;
				}
				
			} while(pIndex < mainMem.size() && pIndex != searchIndex);
			
			//increment running time
			currTime++;
		}
		return swapCount;
	}
	
	//TODO: Keith
	//Search for the smallest hole that is big enough
	public int bestFit(){
		
		Queue<Process> tempReadyQueue = clone(readyQueue); //Ready queue
		System.out.println("Time | Swap | PID | Size | Dur. | Memory Map\n-------------------------------------------------");
		
		//Re-initialize main memory and memory tracker
		mainMem = createMemManager(); //main memory
		inMem = new HashMap<String, Process>(); //memory tracker
		int swapCount = 0; //count number of processes swapped in
		
		//print free memory map
		int currTime = 0;
		print("", currTime, null);
		
		currTime++;
		while(currTime < runtime && tempReadyQueue.size() > 0){
			/*
			//Perform swap out if process is finish in main memory
			swapOut(currTime);
			
			int requiredSize = tempReadyQueue.peek().getSize(); //process size
			int freeSize = 0; //counter to keep track of consecutive free partitions
			int pIndex = 0; //current partition index
			int min
			
			//Check for first free partition hole
			while(pIndex < mainMem.size()){
				
				if(mainMem.get(pIndex).isFree()){					
					freeSize++;
				}else{
					
					if(freeSize >= requiredSize && freeSize < min){
						
						
					}
					
					freeSize = 0;
				}
				
				pIndex++;
			}
	
			//increment running time
			currTime++;*/
		}
		return swapCount;
	}
	
	//TODO: Graeme
	public int worstFit(){

		Queue<Process> tempReadyQueue = clone(readyQueue); //Ready queue
		System.out.println("Time | Swap | PID | Size | Dur. | Memory Map\n-------------------------------------------------");
		
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
			
			int requiredSize = tempReadyQueue.peek().getSize(); //process size
			int freeSize = 0; //counter to keep track of consecutive free partitions
			int pIndex = 0; //index of largest partition
			int maxSize = 0; //size of largest partition
			
			// Check for largest partition hole
			for (int index = 0; index < mainMem.size(); index++) {
				
				//If partition is free, check to see if consecutive partitions are also free
				if(mainMem.get(pIndex).isFree()){
					//counting free partitions
					freeSize++;
				}else{
					//compare to previously found largest partition and reset counter
					if (freeSize > maxSize) {
						maxSize = freeSize;
						pIndex = index;
					}
					freeSize = 0;
				}
			}
			
			//if process size fits in largest free partition, swap in
			if(maxSize >= requiredSize){
				
				//Remove process from queue for swapping
				Process p = tempReadyQueue.poll();
				
				//Swap in process
				swapIn(currTime, p, pIndex);
				
				//increase swap count
				swapCount++;
			}
			
			//increment running time
			currTime++;
		}
		return swapCount;
	}
	
	//Helper method to swap in process
	//currTime is the current simulation running time
	//p is the process to be swapped in
	//currIndex is the last partition the process is allocated
	public void swapIn(int currTime, Process p, int currIndex){
		
		//Calculate starting and ending partition
		int startIndex = (currIndex + 1) - p.getSize() ;
		
		//This is used in swapOut() to make clearing partitions easier
		p.setFirstPartition(startIndex);
		p.setLastPartition(currIndex);
		
		//add process to memory tracker
		inMem.put(p.getPID(), p); 
		
		//Swapping in process
		while(startIndex <= currIndex){
			mainMem.get(startIndex).setPID(p.getPID());
			mainMem.get(startIndex).isFree(false);
			startIndex++;
		}
		print("in", currTime, p);
	}
	
	//Helper method to perform checking and swap out
	//currTime is the current simulation running time
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
	//event is either "in" or "out" for swapping
	//time is the simulation current running time
	//p is the process to being swapped in or out
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
	
	//Helper method to create clone of ready queue - prevent algorithms from altering original queue
	public Queue<Process> clone(Queue<Process> source){
		Queue<Process> destination = new LinkedList<Process>();
		for(Process p : source){
			destination.add(p.getClone());
		}
		return destination;
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


