#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>
#include <signal.h>
#include <sys/time.h>

#define ID_BASE 101

#define STUDENT_COUNT 75

#define MAX_SEATS 20
#define SIMULATION_DURATION 120

//Student status: graduating senior, regular senior, everyone else
typedef enum {GS = 0, RS = 1, EE= 2} Status;

//CS 149 section: 1, 2 or 3
typedef enum {ANY = 0, SECTION_1 = 1, SECTION_2 = 2, SECTION_3 = 3} Section;

//Student struct
typedef struct
{
    int studentID;
    int arrivalTime;
    int turnaroundTime;
    Status studentStatus;
    Section sectionID;
} Student;

pthread_mutex_t EEMutex;  // mutex protects EE queue
pthread_mutex_t RSMutex;  // mutex protects RS queue
pthread_mutex_t GSMutex;  // mutex protects GS queue

Student section1[MAX_SEATS];
Student section2[MAX_SEATS];
Student section3[MAX_SEATS];

int studentsLeft = STUDENT_COUNT;
int emptySlots = 3 * MAX_SEATS;

// A student arrives.
void studentArrives(Student student) {
	// determine which queue the student belongs in
	// for now assume we only have the EE queue
	//TODO:

	// Acquire the mutex lock to protect the EE queue.
	pthread_mutex_lock(&EEMutex);

	// Add a student into the EE queue.
	//TODO:

	// Release the mutex lock.
	pthread_mutex_unlock(&EEMutex);

	// Signal the EE queue semaphore.
	//sem_post(&filledChairs);  // signal  <-- indicates that the queue size has increased

	// print event
	//TODO:
}

// the student thread
void *student_t(void *param) {
	Student student;
	student.studentID = *((int *) param);
	student.arrivalTime = rand()%SIMULATION_DURATION;
	//student.studentStatus = rand()%3;
	//student.sectionID = rand()%4;
	student.studentStatus = GS;
	student.sectionID = ANY;

	// Students will arrive at random times during the office hour.
	sleep(student.arrivalTime);
	studentArrives(student);

	return NULL;
}

int processStudent(Student student) {
	// print event
	//TODO:

	int enrolled;

	// determine what section the student belongs in
	switch (student.sectionID) {
	case SECTION_1:
		enrolled = addToSection1(student);// returns 0 if section is already full
		break;
	case SECTION_2:
		enrolled = addToSection2(student);// returns 0 if section is already full
		break;
	case SECTION_3:
		enrolled = addToSection3(student);// returns 0 if section is already full
		break;
	case ANY:
		// Add to the section with the most empty slots? Or randomly among the available sections?
		//TODO:
	}

	// if student was dropped add to list of dropped (making sure to lock and unlock)
	//TODO:

	// calculate turnaround time and return it
	//TODO:
}

void *everybodyElse_t(void *param) {
	do {
		// Wait on the EE queue semaphore.
		//sem_wait(&filledChairs);

		// wait for a little while before processing
		sleep(rand()%4 + 3); // 3, 4, 5 or 6 seconds

		// Acquire the mutex lock to protect the chairs and the wait count.
		pthread_mutex_lock(&EEMutex);

		// Critical region: Remove a student from EE queue and process them.
		//Student student = poll(EEQueue);

		// Release the mutex lock.
		pthread_mutex_unlock(&EEMutex);

		//int turnaroundTime = processStudent(student);
	} while(studentsLeft && emptySlots);

	// calculate average turnaround time for this queue
	//TODO:

	return NULL;
}

// Main.
int main(int argc, char *argv[])
{
	int studentIds[STUDENT_COUNT];

	// Initialize the mutexes and the semaphores.
	//TODO:

	// Create the queue threads.
	//TODO:

	// Create the student threads.
	int studentId = ID_BASE;
	pthread_t studentThreadId;
	pthread_attr_t studentAttr;
	pthread_attr_init(&studentAttr);
	pthread_create(&studentThreadId, &studentAttr, student_t, &studentId);
	/*int i;
	for (i = 0; i < STUDENT_COUNT; i++) {
		studentIds[i] = ID_BASE + i;
		pthread_t studentThreadId;
		pthread_attr_t studentAttr;
		pthread_attr_init(&studentAttr);
		pthread_create(&studentThreadId, &studentAttr, student_t, &studentIds[i]);
	}*/

	// Wait for the queue threads to complete
	//TODO:

	// Final statistics.
	//TODO:

	return 0;
}
