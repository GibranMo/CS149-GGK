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
typedef enum {SECTION_1 = 0, SECTION_2 = 1, SECTION_3 = 2, ANY = 3} Section;

//Student struct
typedef struct
{
    int studentID;
    int arrivalTime;
    int turnaroundTime;
    Status studentStatus;
    Section sectionID;
} Student;

typedef struct node {
	Student data;
	struct node *next;
	struct node *prev;
} Node;

typedef struct {
	Node *first;
	Node *last;
	int size;
} Queue;

Queue eeQueue;
Queue rsQueue;
Queue gsQueue;

pthread_mutex_t sec1_mutex;  // mutex protects section1 array
pthread_mutex_t sec2_mutex;  // mutex protects section2 array
pthread_mutex_t sec3_mutex;  // mutex protects section3 array
pthread_mutex_t dropped_mutex;  // mutex protects dropped student array
pthread_mutex_t eeMutex;  // mutex protects EE queue
pthread_mutex_t rsMutex;  // mutex protects RS queue
pthread_mutex_t gsMutex;  // mutex protects GS queue
sem_t eeSemaphore;        // everybodyElse_t waits on this semaphore
sem_t rsSemaphore;        // regularSenior_t waits on this semaphore
sem_t gsSemaphore;        // graduatingSenior_t waits on this semaphore

Student section1[MAX_SEATS];
Student section2[MAX_SEATS];
Student section3[MAX_SEATS];
Student dropped[STUDENT_COUNT];

int studentsLeft = STUDENT_COUNT;
int emptySlots = 3 * MAX_SEATS;
int sec1_index = 0, sec2_index = 0, sec3_index = 0, dropped_index = 0;
float eeTurnAvg, rsTurnAvg, gsTurnAvg;

void create(Queue *queue) {
	queue->first = queue->last = NULL;
	queue->size = 0;
}

Student peek(Queue queue) {
	return queue.first->data;
}

Student poll(Queue *queue) {
	Node *temp = queue->first;

	if (temp == NULL) {
		printf("\n Error: empty queue.\n");
	}

	Student data = temp->data;

	if (temp->next != NULL)
	{
		queue->first = temp->next;
		queue->first->prev = NULL;
		free(temp);
	}
	else
	{
		queue->first = NULL;
		queue->last = NULL;
		free(temp);
	}
	queue->size--;
	return data;
}

void offer(Queue *queue, Student data) {
	if (queue->last == NULL)
	{
		queue->last = (Node *)malloc(1 * sizeof(Node));
		queue->last->next = NULL;
		queue->last->prev = NULL;
		queue->last->data = data;
		queue->first = queue->last;
	}
	else
	{
		Node *temp = (Node *)malloc(1 * sizeof(Node));
		queue->last->next = temp;
		temp->data = data;
		temp->next = NULL;
		temp->prev = queue->last;
		queue->last = temp;
	}
	queue->size++;
}

int isEmpty(Queue queue) {
	return queue.size == 0;
}

// A student arrives.
void studentArrives(Student student) {
	// determine which queue the student belongs in
	switch (student.studentStatus) {
	case EE:
		// Acquire the mutex lock to protect the EE queue.
		pthread_mutex_lock(&eeMutex);

		// Add a student into the EE queue.
		offer(&eeQueue, student);

		// Release the mutex lock.
		pthread_mutex_unlock(&eeMutex);

		// Signal the EE queue semaphore.
		sem_post(&eeSemaphore);  // signal
		break;
	case RS:
		// Acquire the mutex lock to protect the RS queue.
		pthread_mutex_lock(&rsMutex);

		// Add a student into the RS queue.
		offer(&rsQueue, student);

		// Release the mutex lock.
		pthread_mutex_unlock(&rsMutex);

		// Signal the RS queue semaphore.
		sem_post(&rsSemaphore);  // signal
		break;
	case GS:
		// Acquire the mutex lock to protect the GS queue.
		pthread_mutex_lock(&gsMutex);

		// Add a student into the GS queue.
		offer(&gsQueue, student);

		// Release the mutex lock.
		pthread_mutex_unlock(&gsMutex);

		// Signal the GS queue semaphore.
		sem_post(&gsSemaphore);  // signal
	}

	// print event
	//TODO:
	printf("Student %d arrives\n", student.studentID);
}

// the student thread
void *student_t(void *param) {
	Student student;
	student.studentID = *((int *) param);
	student.arrivalTime = rand()%SIMULATION_DURATION;
	student.studentStatus = rand()%3;
	student.sectionID = rand()%4;
	//student.studentStatus = GS;
	//student.sectionID = ANY;

	// Students will arrive at random times during simulation.
	sleep(student.arrivalTime);
	studentArrives(student);

	return NULL;
}

int addToSection1(Student student) {
	int enrolled;

	pthread_mutex_lock(&sec1_mutex);

	// check to see if the section is full
	if (sec1_index < MAX_SEATS) {
		// enroll the student in the class
		section1[sec1_index++] = student;
		emptySlots--;
		enrolled = 1;
	}
	else enrolled = 0;

	pthread_mutex_unlock(&sec1_mutex);

	return enrolled;
}

int addToSection2(Student student) {
	int enrolled;

	pthread_mutex_lock(&sec2_mutex);

	// check to see if the section is full
	if (sec2_index < MAX_SEATS) {
		// enroll the student in the class
		section2[sec2_index++] = student;
		emptySlots--;
		enrolled = 1;
	}
	else enrolled = 0;

	pthread_mutex_unlock(&sec2_mutex);

	return enrolled;
}

int addToSection3(Student student) {
	int enrolled;

	pthread_mutex_lock(&sec3_mutex);

	// check to see if the section is full
	if (sec3_index < MAX_SEATS) {
		// enroll the student in the class
		section3[sec3_index++] = student;
		emptySlots--;
		enrolled = 1;
	}
	else enrolled = 0;

	pthread_mutex_unlock(&sec3_mutex);

	return enrolled;
}

int processStudent(Student student) {
	// print event
	//TODO:
	printf("Begin processing %d\n", student.studentID);

	int enrolled;

	// determine what section the student belongs in
	Section section = student.sectionID;
	int any = 0;
	if (section == ANY) {
		// choose a random section
		section = rand()%3;
		any = 3;
	}

	do {
		switch (section) {
		case SECTION_1:
			enrolled = addToSection1(student);// returns 0 if section is already full
			break;
		case SECTION_2:
			enrolled = addToSection2(student);// returns 0 if section is already full
			break;
		case SECTION_3:
			enrolled = addToSection3(student);// returns 0 if section is already full
		}

		// if a student failed to enroll and can be enrolled in any section try another section
		if (!enrolled && any) {
			any--;
			section = (section + 1) % 3;
		}
	} while (!enrolled && any);

	// calculate turnaround time
	int turnaround = 0;//<-----------TODO:
	student.turnaroundTime = turnaround;

	// if student was dropped add to list of dropped (making sure to lock and unlock)
	if (!enrolled) {
		pthread_mutex_lock(&dropped_mutex);
		dropped[dropped_index++] = student;
		pthread_mutex_unlock(&dropped_mutex);

		printf("Student %d dropped\n", student.studentID);
	}
	else {
		printf("Student %d enrolled\n", student.studentID);
	}
	studentsLeft--;

	// calculate turnaround time and return it
	return turnaround;
}

void *everybodyElse_t(void *param) {
	int studentCount = 0;
	int totalTurnaround = 0;
	do {
		// Wait on the EE queue semaphore.
		sem_wait(&eeSemaphore);

		// wait for a little while before processing
		sleep(rand()%4 + 3); // 3, 4, 5 or 6 seconds

		// Acquire the mutex lock to protect the chairs and the wait count.
		pthread_mutex_lock(&eeMutex);

		// Critical region: Remove a student from EE queue and process them.
		Student student = poll(&eeQueue);

		// Release the mutex lock.
		pthread_mutex_unlock(&eeMutex);

		int turnaroundTime = processStudent(student);
		studentCount++;
		totalTurnaround += turnaroundTime;
	} while(studentsLeft && emptySlots);

	// calculate average turnaround time for this queue
	eeTurnAvg = (float) totalTurnaround / studentCount;

	return NULL;
}

void *regularSenior_t(void *param) {
	int studentCount = 0;
	int totalTurnaround = 0;
	do {
		// Wait on the EE queue semaphore.
		sem_wait(&rsSemaphore);

		// wait for a little while before processing
		sleep(rand()%3 + 2); // 2, 3 or 4 seconds

		// Acquire the mutex lock to protect the chairs and the wait count.
		pthread_mutex_lock(&rsMutex);

		// Critical region: Remove a student from EE queue and process them.
		Student student = poll(&rsQueue);

		// Release the mutex lock.
		pthread_mutex_unlock(&rsMutex);

		int turnaroundTime = processStudent(student);
		studentCount++;
		totalTurnaround += turnaroundTime;
	} while(studentsLeft && emptySlots);

	// calculate average turnaround time for this queue
	rsTurnAvg = (float) totalTurnaround / studentCount;

	return NULL;
}

void *gradSenior_t(void *param) {
	int studentCount = 0;
	int totalTurnaround = 0;
	do {
		// Wait on the EE queue semaphore.
		sem_wait(&gsSemaphore);

		// wait for a little while before processing
		sleep(rand()%2 + 1); // 1 or 2 seconds

		// Acquire the mutex lock to protect the chairs and the wait count.
		pthread_mutex_lock(&gsMutex);

		// Critical region: Remove a student from EE queue and process them.
		Student student = poll(&gsQueue);

		// Release the mutex lock.
		pthread_mutex_unlock(&gsMutex);

		int turnaroundTime = processStudent(student);
		studentCount++;
		totalTurnaround += turnaroundTime;
	} while(studentsLeft && emptySlots);

	// calculate average turnaround time for this queue
	gsTurnAvg = (float) totalTurnaround / studentCount;

	return NULL;
}

// Main.
int main(int argc, char *argv[])
{
	int studentIds[STUDENT_COUNT];
	int eeQueueId = 0;
	int rsQueueId = 1;
	int gsQueueId = 2;
	create(&eeQueue);
	create(&rsQueue);
	create(&gsQueue);

	// Initialize the mutexes and the semaphores.
	pthread_mutex_init(&sec1_mutex, NULL);
	pthread_mutex_init(&sec2_mutex, NULL);
	pthread_mutex_init(&sec3_mutex, NULL);
	pthread_mutex_init(&dropped_mutex, NULL);
	pthread_mutex_init(&eeMutex, NULL);
	pthread_mutex_init(&rsMutex, NULL);
	pthread_mutex_init(&gsMutex, NULL);
	sem_init(&eeSemaphore, 0, 0);
	sem_init(&rsSemaphore, 0, 0);
	sem_init(&gsSemaphore, 0, 0);

	// Create the queue threads.
	pthread_t eeQueueThreadId;
	pthread_attr_t eeQueueAttr;
	pthread_attr_init(&eeQueueAttr);
	pthread_create(&eeQueueThreadId, &eeQueueAttr, everybodyElse_t, &eeQueueId);

	pthread_t rsQueueThreadId;
	pthread_attr_t rsQueueAttr;
	pthread_attr_init(&rsQueueAttr);
	pthread_create(&rsQueueThreadId, &rsQueueAttr, regularSenior_t, &rsQueueId);

	pthread_t gsQueueThreadId;
	pthread_attr_t gsQueueAttr;
	pthread_attr_init(&gsQueueAttr);
	pthread_create(&gsQueueThreadId, &gsQueueAttr, gradSenior_t, &gsQueueId);

	// Create the student threads.
	int i;
	for (i = 0; i < STUDENT_COUNT; i++) {
		studentIds[i] = ID_BASE + i;
		pthread_t studentThreadId;
		pthread_attr_t studentAttr;
		pthread_attr_init(&studentAttr);
		pthread_create(&studentThreadId, &studentAttr, student_t, &studentIds[i]);
	}

	// Wait for the queue threads to complete
	pthread_join(eeQueueThreadId, NULL);
	pthread_join(rsQueueThreadId, NULL);
	pthread_join(gsQueueThreadId, NULL);

	// Final statistics.
	//TODO:

	return 0;
}
