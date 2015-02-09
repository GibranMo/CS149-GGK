import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Represents the timeline of time chunks
public class QuantaTimeLine {
    
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
    
    //First Come First Served.
    public void FCFS (ArrayList<Process> listOfProcesses){
        
        int i = 0;
        
        while (i < listOfProcesses.size()) {
            
            //inserting a ready process into some timeslot of our quanta time line
            //quantaLine.get(i).assignProcess(listOfProcesses.get(i));
            quantaLine.get(i).useUpQuanta();
            
            //Check if the expected runtime of current process (i) will fit into 1 quanta which is 3.0
            //float processTimeRem = quantaLine.get(i).getProcessTimeRemaining(); //extract time of current process
            float processTimeRem = listOfProcesses.get(i).getExpRunTime();
            
            if (processTimeRem > quantaSize)
            {
                //Find out how many times the time fits into a quanta
                double numberOfTimeSlots = processTimeRem / quantaSize;
                //Ceiling Function - Rounding up Any decimal to a whole number
                int additonalQuantas = (int) Math.ceil(numberOfTimeSlots);
                
                i++; //We need at least one more quanta slice, so advance the index
                for (int k = 1; k < additonalQuantas; k++ )
                {
                    if (i > 50)
                    {
                        quantaLine.add(new Quanta());
                        System.out.println("****");
                    }
                    advanceQuantaPosition(i, listOfProcesses);
                    i++;
                }
                continue;
            }
            System.out.println("HERE");
            i++;
        }
        
    }
    //Assigning a process to a quanta slice and running that process 
    private static void advanceQuantaPosition(int index, ArrayList<Process> processes) {
        
        
        //quantaLine.get(index).assignProcess(processes.get(index));
        quantaLine.get(index).useUpQuanta(); // Takes same amount of time every time (3 seconds)
        
    }
    
    
}
