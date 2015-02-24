/* CS 149 Assignment 3 Multithreaded Simulation */
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define MAX_STUDENTS 75 //Max number of students
#define MAX_SIM_INTERVAL 120 //Max simulation interval = 2 minutes = 120 seconds

//Student status: graduating senior, regular senior, everyone else
typedef enum {GS, RS, EE} Status;

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

//global
Student students[75];

//Generate random students
void generateStudents()
{
    //set seed for randomizer
    srand(time(NULL));
    int i;
    for(i = 0; i < MAX_STUDENTS; i++)
    {
        //create student
        Student student;
        student.studentID = i;//incremental student id
        student.arrivalTime = rand() % MAX_SIM_INTERVAL;//random arrival time 0 to 120 seconds
        student.sectionID = rand() % 4;//random section 1, 2 or 3
        student.studentStatus = rand() % 3;//random status
        student.turnaroundTime = 0;

        //add student to students array
        students[i] = student;
    }
}

//Sort students array by arrival time
void sortByArrivalTime()
{
    int i, j;
    Student temp;
    for(i = 0; i < MAX_STUDENTS; i++)
    {
        for(j = 0; j < MAX_STUDENTS - 1; j++)
        {
            //Compare arrival time
            if(students[j].arrivalTime > students[j + 1].arrivalTime)
            {
                //swap students
                temp = students[j + 1];
                students[j + 1] = students[j];
                students[j] = temp;
            }
        }
    }
}

//Pretty print students array
void printRandomStudents()
{
    /************************* DEBUGGING - REMOVE AFTER TESTING ***************************/
    int sec1 = 0, sec2 = 0, sec3 = 0, any = 0, ss1 = 0, ss2 = 0, ss3 = 0, totalArrival = 0;
    /*************************************************************************************/

    printf("Student ID  Arrival  Section  Status\t\n-----------------------------------------\n");
    int i;
    for(i = 0; i < MAX_STUDENTS; i++)
    {
        Student student = students[i];
        printf("%5d\t%8d\t%d\t", student.studentID, student.arrivalTime, student.sectionID);
        switch(student.studentStatus)
        {
            case GS:
                printf("GS\n");
                break;
            case RS:
                printf("RS\n");
                break;
            case EE:
                printf("EE\n");
                break;
            default:
                printf("\n");
                break;
        }

        /**************** DEBUGGING - REMOVE AFTER TESTING **********************/
        switch(student.sectionID)
        {
            case SECTION_1:
                sec1++;
                break;
            case SECTION_2:
                sec2++;
                break;
            case SECTION_3:
                sec3++;
                break;
            case ANY:
                any++;
            default:
                break;
        }

        switch(student.studentStatus)
        {
            case GS:
                ss1++;
                break;
            case RS:
                ss2++;
                break;
            case EE:
                ss3++;
                break;
            default:
                break;
        }

        totalArrival += student.arrivalTime;
        /*********************************************************/
    }

    /**************** DEBUGGING - REMOVE AFTER TESTING ******************/
    printf("# of students in Section 1: %d\n", sec1);
    printf("# of students in Section 2: %d\n", sec2);
    printf("# of students in Section 3: %d\n", sec3);
    printf("# of students in Section Any: %d\n", any);
    printf("# of students in all Sections: %d\n\n", sec1 + sec2 + sec3 + any);
    printf("# of GS: %d\n", ss1);
    printf("# of RS: %d\n", ss2);
    printf("# of EE: %d\n", ss3);
    printf("# of Students: %d\n\n", ss1 + ss2 + ss3);
    printf("Total Run Time: %d\n\n", totalArrival);
    /*************************************************************************/
}

int main()
{
    //randomly generate students array
    generateStudents();
    printRandomStudents();

    //Sort students array
    sortByArrivalTime();
    printRandomStudents();

    return 0;
}
