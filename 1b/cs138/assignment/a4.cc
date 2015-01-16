#include <iostream>
#include <string>
#include <cassert>

using namespace std;

struct Qnode {
    string val;
    Qnode* next;
};

struct Queue {
	Qnode* first;
	Qnode* last;
};

struct PQnode {
    int priority;
    Queue q;
    PQnode* next;
};

typedef PQnode* PQ;

void initPQ(PQ& pq) { 
    pq = NULL;
}

bool isEmptyPQ(const PQ& pq) {
	return pq == NULL ? true : false;
}

Qnode* enterQ(Qnode* q, string val) {
	assert(q != NULL);

	Qnode* temp = new Qnode();
	temp->val = val;
	temp->next = NULL;
	q->next = temp;

	return temp;
}

void enterPQ(PQ& pq, string val, int priority) {
	PQ newPQ = new PQnode();
	newPQ->priority = priority;

	Qnode* newQ = new Qnode();
	newQ->val = val;
	newQ->next = NULL;

	newPQ->q.first = newPQ->q.last = newQ;

	if(isEmptyPQ(pq)) newPQ->next = NULL;
	else if(priority < pq->priority) newPQ->next = pq;
	else {
		PQ enterAt = pq;
		while(true) {
			if(priority < enterAt->priority) {
				newPQ->next = enterAt->next;
				enterAt->next = newPQ;
				return;
			}
			else if(priority == enterAt->priority) {
				enterAt->q.last = enterQ(enterAt->q.last, val);
				return;
			}
			else if(enterAt->next != NULL && priority < enterAt->next->priority) {
				newPQ->next = enterAt->next;
				enterAt->next = newPQ;
				return;
			}
			else if(enterAt->next != NULL) enterAt = enterAt->next;
			else {
				newPQ->next = NULL;
				enterAt->next = newPQ;
				return;
			}
		}
	}

	pq = newPQ;
}

string firstPQ(const PQ& pq) {
	assert(!isEmptyPQ(pq) && pq->q.first != NULL);

	return pq->q.first->val;
}

void leaveQ(Qnode* q) {
	delete q;
}

void leavePQ(PQ& pq) {
	assert(!isEmptyPQ(pq));

	if(pq->q.first != pq->q.last) {
		Qnode* temp = pq->q.first->next;

		leaveQ(pq->q.first);

		pq->q.first = temp;
	}
	else {
		PQ temp = pq->next;

		leaveQ(pq->q.first);
		delete pq;

		pq = temp;
	}
}

int numPriorities(const PQ& pq) {
	int i;
	PQ current = pq;

	for(i = 0; current != NULL; i++) {
		current = current->next;
	}

	return i;
}

int sizeByPriority(const PQ& pq, int priority) {
	PQ curPQ = pq;

	while(curPQ->priority != priority) {
		if(curPQ->next != NULL) curPQ = curPQ->next;
		else return 0;
	}

	Qnode* curQ = curPQ->q.first;
	int total = 1;

	while(curQ != curPQ->q.last) {
		total++;
		curQ = curQ->next;
	}

	return total;
}

int sizePQ(const PQ& pq) {
	if(numPriorities(pq) == 0) return 0;

	PQ current = pq;
	int total = 0;

	for(int i = 0; i < numPriorities(pq); i++) {
		total += sizeByPriority(pq, current->priority);
		current = current->next;
	}
	
	return total;
}
