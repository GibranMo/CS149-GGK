#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <assert.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/time.h>
#include <string.h>
#include <time.h>

#define NUM_PROC 5      //Number of child processes
#define BUFFER_SIZE 128  //Buffer size
#define READ_END 0      //file descriptor read end
#define WRITE_END 1     //File descriptor write end
#define DURATION 30     //Duration for processes to run

int fd[NUM_PROC][2];  // file descriptors for the pipe
int timedout[NUM_PROC]; //child process timeouts
struct timeval startTime[NUM_PROC]; //child process clocks

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

void ChildProcess(int pnum, int id) {
    
    struct itimerval tval;
    timerclear(& tval.it_interval);
    timerclear(& tval.it_value);
    tval.it_value.tv_sec = DURATION;    //set timeout
    
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
    
    timedout[pnum] = 0; //initialize timeout flag to 0
    
    // Close the unused READ end of the pipe.
    //close(fd[pnum][READ_END]);
    
    setitimer(ITIMER_REAL, & tval, NULL);  //Set timer for process
    gettimeofday(&startTime[pnum], NULL); //start child clock
    struct timeval stopTime;
    double elapsed;

    if(pnum == 4)
    {
        while (!timedout[pnum])
         {   
            char keyBoardInput[BUFFER_SIZE];
            char keyBoardInput2[BUFFER_SIZE];

            printf("Enter something>");
            fgets (keyBoardInput, BUFFER_SIZE, stdin);
            /* Remove trailing newline, if there. */
            if ((strlen(keyBoardInput)>0) && (keyBoardInput[strlen (keyBoardInput) - 1] == '\n')){
                keyBoardInput[strlen (keyBoardInput) - 1] = '\0';
                fflush(stdin);
            }

            gettimeofday(&stopTime, NULL);
            elapsed = (stopTime.tv_sec - startTime[pnum].tv_sec) +
                                ((stopTime.tv_usec - startTime[pnum].tv_usec) / 1000000.0);

            sprintf(keyBoardInput2, "0:%06.3lf: %s", elapsed, keyBoardInput);
            write(fd[pnum][WRITE_END], keyBoardInput2, strlen(keyBoardInput2)+1);
        }
        //printf("*end 4\n");
    }
    else {

        int msgNum = 1;
        while (!timedout[pnum]) {
            char string[BUFFER_SIZE];
            
            // sleep for a random time of 0, 1 or 2 seconds
            int sleepTime = rand()%3;
            //printf(">>pid: %d  sleepTime: %d ParamRandom: %d\n", pnum, sleepTime, random);
            sleep(sleepTime);

            // calculate time
            gettimeofday(&stopTime, NULL);
            double elapsed = (stopTime.tv_sec - startTime[pnum].tv_sec) +
                                ((stopTime.tv_usec - startTime[pnum].tv_usec) / 1000000.0);
            
            sprintf(string, "0:%06.3lf: Child %d message %d", elapsed, pnum, msgNum);
            // Write to the WRITE end of the pipe.
            write(fd[pnum][WRITE_END], string, strlen(string)+1);
            //printf("Child%d: Wrote '%s' to the pipe.\n", pnum, string);
            msgNum++;
        }
    }

    // Close the WRITE end of the pipe.
    close(fd[pnum][WRITE_END]);
    printf("Child%d: Terminating.\n", pnum);
    exit(0);
}

int main(void) {
    
    srand(time(0));
    pid_t pids[NUM_PROC];  // child process id
    struct timeval pStartTime, pStopTime; //parent clock
    //time(&startTime); //set timer start time at 0
    fd_set inputs, inputfds; //file descriptor set
    int i;
    
    // Create the pipes.
    for (i = 0; i < NUM_PROC; i++) {
        if (pipe(fd[i]) == -1) {
            fprintf(stderr,"pipe() failed");
            return 1;
        }
    }
    
    //Add file descriptors to inputs
    FD_ZERO(&inputs);
    for (i = 0; i < NUM_PROC ; i++) {
        FD_SET(fd[i][READ_END], &inputs);
    }
    
    gettimeofday(&pStartTime, NULL); //start parent clock

    // start children
    for (i = 0; i < NUM_PROC; i++) {
        int r = rand() % 10;
        if ((pids[i] = fork()) < 0) {
            perror("fork");
            exit(1);
        }
        else if (pids[i] == 0) {
            //int r = rand() % 10;
            ChildProcess(i, pids[i]);	// give the child the index of its pipe file descriptor
        }
    }
    
    // PARENT PROCESS
    // read from the pipes and write to output.txt
    char read_msg[BUFFER_SIZE]; //buffer for reading pipe message
    char buffer[32 + BUFFER_SIZE]; //buffer for writing to file
    struct timeval timeout;
    int result;
    double elapsed;
    pid_t wpid; //wait pid
    int status;

    for(;;)	{
        
        inputfds = inputs;
        
        // wait at least 1.25 seconds.
        timeout.tv_sec = 1;
        timeout.tv_usec = 250000;
        
        // Get select() results.
        result = select(FD_SETSIZE, &inputfds, NULL, NULL, &timeout);
        
        switch(result) {

            //no file descriptors
            case 0: {
                for(i = 0; i < NUM_PROC; i++){
                    //close file descriptor read ends for finished children
                    if((wpid = waitpid(pids[i], &status, 0)) == pids[i]){//WNOHANG | WUNTRACED
                        close(fd[i][READ_END]);
                        printf("\nClose pipe: %d\n", i);
                    }
                }
                break;
            }
            //error    
            case -1: {
                perror("select");
                exit(1);
            }
            //file descriptors available    
            default: {
                for(i = 0; i < NUM_PROC; i++){

                    //Check file descriptors
                    if (FD_ISSET(fd[i][READ_END], &inputfds)) {

                        memset(read_msg, '\0', sizeof(read_msg)); //zero out buffer
                        read(fd[i][READ_END], read_msg, BUFFER_SIZE); //read into buffer

                        // calculate time
                        gettimeofday(&pStopTime, NULL);
                        elapsed = (pStopTime.tv_sec - pStartTime.tv_sec) +
                                            ((pStopTime.tv_usec - pStartTime.tv_usec) / 1000000.0);

                        memset(buffer, '\0', sizeof(buffer)); //zero out buffer
                        sprintf(buffer, "0:%06.3lf | ", elapsed); //parent timestamp
                        strcat(buffer, read_msg); //child timestamp and message
                        strcat(buffer, "\n"); //newline
                        //printf("%s\n", read_msg);
                        
                        FILE *fp;
                        /* /Users/Gibran/Documents/OSclass/CS149workspace/Assn6/Assn6/ */
                        if((fp = fopen("output.txt", "a+")) == NULL){
                            printf("**Error");
                            exit(1);
                        }
                        else {
                            fprintf(fp, "%s", buffer);
                        }
                        fflush(fp);
                        fclose(fp);
                    }
                }
                break;
            }
        }
    }
    return 0;

    /*
     int status;
     printf("Parent: Wait for child to complete.\n")    ;
     waitpid(-1, &status, 0);
     printf("Parent: Terminating.\n");
     */
}
