import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Quanta {
    
    public Quanta() {}
    
    //This just really means run the process assigned to this quanta.
    public void useUpQuanta(){
        
        //quantasProcess.runProcess();// Takes up the same amount of chunk/time every time
        long start = System.currentTimeMillis();
        long end = start + 50; // It doesn't matter what time we assign to a quanta. This is 50 milliseconds
        while (System.currentTimeMillis() < end)
        {
            
        }
    }
    
}
