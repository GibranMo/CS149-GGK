import java.util.LinkedList;
import java.util.Queue;



public class Paging {
	
	private int memSize; //main memory size
	private Queue<Process> readyQueue; //ready queue of processes
	
	public Paging(Queue<Process> readyQueue, int memSize){
		this.readyQueue = readyQueue;
		this.memSize = memSize;
	}

	//TODO: Graeme
	public void FIFO(){
		
	}
	
	//TODO: Graeme
	public void LRU(){
		
	}
	
	//TODO: Gibran
	public void LFU(){
		
	}
	
	//TODO: Gibran
	public void MFU(){
		
	}
	
	//TODO: Gibran
	public void randomPick(){
		
	}
	
}
