#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <assert.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/ioctl.h>
#include <string.h>
#include <time.h>


#define NUM_PROC 5
#define BUFFER_SIZE	100
#define READ_END	0
#define WRITE_END	1
#define DURATION 30

int fd[NUM_PROC][2];  // file descriptors for the pipe
int timedout[NUM_PROC];
time_t startTime;//start time is at 0

// The SIGALRM interrupt handler.
void SIGALRM_handler1(int signo)
{
    assert(signo == SIGALRM);
    timedout[0] = 1;
}

void SIGALRM_handler2(int signo)
{
    assert(signo == SIGALRM);
    timedout[1] = 1;
}

void SIGALRM_handler3(int signo)
{
    assert(signo == SIGALRM);
    timedout[2] = 1;
}

void SIGALRM_handler4(int signo)
{
    assert(signo == SIGALRM);
    timedout[3] = 1;
}

void SIGALRM_handler5(int signo)
{
    assert(signo == SIGALRM);
    timedout[4] = 1;
}

void ChildProcess(int pnum, int id, int random) {
    
    struct itimerval tval;
    
    timerclear(& tval.it_interval);
    timerclear(& tval.it_value);
    tval.it_value.tv_sec = DURATION;    // 10 second timeout
    
    switch (pnum) {
        case 0:
            (void) signal(SIGALRM, SIGALRM_handler1);
            break;
        case 1:
            (void) signal(SIGALRM, SIGALRM_handler2);
            break;
        case 2:
            (void) signal(SIGALRM, SIGALRM_handler3);
            break;
        case 3:
            (void) signal(SIGALRM, SIGALRM_handler4);
            break;
        case 4:
            (void) signal(SIGALRM, SIGALRM_handler5);
            break;
    }
    
    timedout[pnum] = 0;
    
    // Close the unused READ end of the pipe.
    //close(fd[pnum][READ_END]);
    
    setitimer(ITIMER_REAL, & tval, NULL);  // set timer
    
    if(pnum == 4)
    {
        
        
        while (!timedout[pnum])
         {
             time_t now;
             time(&now);
             float elapsed = difftime(now, startTime);
             
             char keyBoardInput[100];
             char keyBoardInput2[100];
             
             printf("Enter something>");
             scanf("%99s", keyBoardInput);
             sprintf(keyBoardInput2, "0:%06.3lf: %s", elapsed, keyBoardInput);
             //sprintf(keyBoardInput2, "0:%06.3lf:", elapsed);

             // Max length of 99 characters; puts a null terminated string in path, thus 99 chars + null is the max
             
             write(fd[pnum][WRITE_END], keyBoardInput2, strlen(keyBoardInput2)+1);
             
         }
        
        printf("*end 4\n");
        // Close the WRITE end of the pipe.
        close(fd[pnum][WRITE_END]);
        
        printf("Child%d: Terminating.\n", pnum);
        //exit(0);

    }
    
    
    
    
    int msgNum = 1;
    while (!timedout[pnum]) {
        char string[BUFFER_SIZE];
        
        // sleep for a random time of 0, 1 or 2 seconds
        
        int sleepTime = rand()%3;
        //printf(">>pid: %d  sleepTime: %d ParamRandom: %d\n", pnum, sleepTime, random);
        sleep(sleepTime);
        
        
        
        // read from a file
        
        // get current time
        time_t now;
        time(&now);
        float elapsed = difftime(now, startTime);
        
        //sprintf(string, "0:%06.3lf:", elapsed);
        sprintf(string, "0:%06.3lf: Child %d message %d", elapsed, pnum, msgNum);
        // Write to the WRITE end of the pipe.
        write(fd[pnum][WRITE_END], string, strlen(string)+1);
        //printf("Child%d: Wrote '%s' to the pipe.\n", pnum, string);
        msgNum++;
    }
    printf("outside\n");
    // Close the WRITE end of the pipe.
    close(fd[pnum][WRITE_END]);
    
    printf("Child%d: Terminating.\n", pnum);
    exit(0);
}

int main(void) {
    
    srand(time(0));
    pid_t pids[NUM_PROC];  // child process id
    time(&startTime); //set timer start time at 0
    printf("0:00:000: Start\n");
    int i;
    
    // Create the pipes.
    for (i = 0; i < NUM_PROC; i++) {
        if (pipe(fd[i]) == -1) {
            fprintf(stderr,"pipe() failed");
            return 1;
        }
    }
    
    //Add file descriptors to input
    fd_set inputs, inputfds;
    FD_ZERO(&inputs);
    for (i = 0; i < NUM_PROC ; i++) {
        FD_SET(fd[i][READ_END], &inputs);
    }
    
    // start children
    for (i = 0; i < NUM_PROC; i++) {
        int r = rand() % 10;
        if ((pids[i] = fork()) < 0) {
            perror("fork");
            exit(1);
        }
        else if (pids[i] == 0) {
            int r = rand() % 10;
            ChildProcess(i, pids[i], r);	// give the child the index of its pipe file descriptor
        }
    }
    
    // PARENT PROCESS
    // read from the pipes and write to output.txt
    struct timeval timeout;
    int result;
    int fdCnt;
    char read_msg[BUFFER_SIZE];
    for(;;)	{
        
        inputfds = inputs;
        
        // 2.5 seconds.
        timeout.tv_sec = 1;
        timeout.tv_usec = 250000;
        
        // Get select() results.
        //result = select(FD_SETSIZE, &inputfds,(fd_set *) 0, (fd_set *) 0, &timeout);
        result = select(FD_SETSIZE, &inputfds, NULL, NULL, &timeout);
        
        switch(result) {
            case 0: {
                time_t now;
                time(&now);
                float elapsed = difftime(now, startTime);
                if((int) elapsed >= DURATION){
                    printf("0:%06.3lf: End\n", elapsed);
                    exit(0);
                }
                //printf("**0:%06.3lf\n", elapsed);
                break;
            }
                
            case -1: {
                perror("select");
                exit(1);
            }
                
            default: {
                for(fdCnt = 0; fdCnt < NUM_PROC; fdCnt++){
                    if (FD_ISSET(fd[fdCnt][READ_END], &inputfds)) {
                        memset(read_msg, '\0', sizeof(read_msg));
                        read(fd[fdCnt][READ_END], read_msg, BUFFER_SIZE);
                        //printf("%s\n", read_msg);
                        
                        
                        FILE *fp;
                        
                        fp = fopen("/Users/Gibran/Documents/OSclass/CS149workspace/Assn6/Assn6/output.txt", "a+shit");
                        if(fp == NULL)
                        {
                            printf("**Error");
                            exit(0);
                            
                        }
                        strcat(read_msg, "\n");
                        fprintf(fp, read_msg);
                            
                        //fclose(fp);
                        
                    }
                }
                break;
            }
        }
    }
    
    /*
     int status;
     printf("Parent: Wait for child to complete.\n")    ;
     waitpid(-1, &status, 0);
     printf("Parent: Terminating.\n");
     */
}
