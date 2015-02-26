#include <stdlib.h>
#include <stdio.h>

typedef struct node {
	int data;
	struct node *next;
	struct node *prev;
} Node;

typedef struct queue {
	Node *first;
	Node *last;
	int size;
} Queue;


void create(Queue *queue) {
	queue->first = queue->last = NULL;
	queue->size = 0;
}

int peek(Queue queue) {
	return queue.first->data;
}

int poll(Queue *queue) {
	Node *temp = queue->first;
	
	if (temp == NULL) {
		printf("\n Error: empty queue.\n");
		return -1;
	}

	int data = temp->data;
	
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

void offer(Queue *queue, int data) {
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

int main() {
	Queue q1;
	Queue q2;
	create(&q1);
	create(&q2);

	offer(&q1, 20);
	offer(&q1, 25);
	offer(&q1, 23);
	printf("%d\n", peek(q1));

	offer(&q2, 1);
	offer(&q2, 2);
	offer(&q2, 7);
	printf("%d\n", poll(&q2));

	while (!isEmpty(q1)) {
		printf("%d\n", poll(&q1));
	}

	while (!isEmpty(q2)) {
		printf("%d\n", poll(&q2));
	}

	return 0;
}