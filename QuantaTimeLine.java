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
        
        int i = 0; //for indexing the ready process queue
        int j = 0; //for indexing the quanta time line. At the beginning, it will be synchronized with 'i' but then it will
        //start getting ahead.
        while (i < listOfProcesses.size()) {
            
            if (j >= quantaLine.size())
            {
                quantaLine.add(new Quanta());
            }
            //**********************************
            quantaLine.get(j).useUpQuanta();//** Immediately burn next quanta spot. (The logic for deciding
            //********************************** whether or not more quanta slots or needed comes right up next). I know, this check should
            //have come first.
            
            //****OFFICIAL REQUIRED OUTPUT - Site 1 (there is another spot below  where this output is needed)(Matching quanta spot with process)
            System.out.println(">>quanta spot-" + j + " process name: " + i ); //Since we are doing FCFS, 'i' represents the name of each process
            //*******************************************************************
            
            //Check if the expected runtime of current process (i) will fit into 1 quanta which is 3.0
            //float processTimeRem = quantaLine.get(i).getProcessTimeRemaining(); //extract time of current process
            float processTimeRem = listOfProcesses.get(i).getExpRunTime();
            
            if (processTimeRem > quantaSize)
            {
                
                //Find out how many times the time fits into a quanta
                double numberOfTimeSlots = processTimeRem / quantaSize;
                //Ceiling Function - Rounding up Any decimal to a whole number
                int additonalQuantas = (int) Math.ceil(numberOfTimeSlots);
                
                
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
                    System.out.println(">>quanta spot-" + j + " process name: " + i );
                    
                    j++;
                    
                }
                
                i++;
                continue;
            }
            
            i++;
            j++;
        }
        
    }

    
}
