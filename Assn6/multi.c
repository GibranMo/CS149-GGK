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
#define BUFFER_SIZE	32
#define READ_END	0
#define WRITE_END	1

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

void ChildProcess(int pnum) {
	struct itimerval tval;

	timerclear(& tval.it_interval);
	timerclear(& tval.it_value);
	tval.it_value.tv_sec = 10;    // 10 second timeout

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
	close(fd[pnum][READ_END]);

	setitimer(ITIMER_REAL, & tval, NULL);  // set timer

	while (!timedout) {
		char string[BUFFER_SIZE];

		// sleep for a random time of 0, 1 or 2 seconds
		int sleepTime = rand()%3;
		sleep(sleepTime);

		// read from a file

		// get current time
		time_t now;
		time(&now);
		double elapsed = difftime(now, startTime);
		sprintf(string, "0:%06.3lf: ", elapsed);

		// Write to the WRITE end of the pipe.
		//write(fd[WRITE_END], string, strlen(string)+1);
		printf("Child%d: Wrote '%s' to the pipe.\n", pnum, string);
	}

	// Close the WRITE end of the pipe.
	close(fd[pnum][WRITE_END]);

    printf("Child%d: Terminating.\n", pnum);
    exit(0);
}

int main(void) {
	pid_t pids[NUM_PROC];  // child process id
	int i;

	// Create the pipes.
	for (i = 0; i < NUM_PROC; i++) {
		if (pipe(fd[i]) == -1) {
			fprintf(stderr,"pipe() failed");
			return 1;
		}
	}

	// start children
	for (i = 0; i < NUM_PROC-1; i++) {
		if ((pids[i] = fork()) < 0) {
			perror("fork");
			exit(1);
		}
		else if (pids[i] == 0) {
			ChildProcess(i);	// give the child the index of its pipe file descriptor
		}
	}

	// PARENT PROCESS
	// read from the pipes and write to output.txt

	int status;
	printf("Parent: Wait for child to complete.\n")    ;
	waitpid(-1, &status, 0);
    printf("Parent: Terminating.\n");
}
