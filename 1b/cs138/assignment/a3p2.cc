#include <iostream>
#include <string>
#include <cassert>

using namespace std;

struct NodeChunk{
    string* val;
    NodeChunk* next;
};

struct Stack{
    int chunkSize;
    int topElt2;
    int topElt;
    NodeChunk* firstChunk;
};

NodeChunk* createNewNodeChunk (int N) {
	NodeChunk* newNode = new NodeChunk;
	newNode->val = new string[N];

	return newNode;
}

void initStack (int chunkSize, Stack& s) {
	s.chunkSize = chunkSize;
	s.topElt2 = 0;
	s.firstChunk = NULL;

	s.topElt = s.topElt2-1; return;
}

bool isEmpty (const Stack& s) {
	return s.firstChunk == NULL;
}

int size (const Stack& s) {
	if(s.firstChunk == NULL) return 0;

	int i = 0;
	NodeChunk* current = s.firstChunk;
	while(current->next != NULL) {
		current = current->next;
		i++;
	}

	return s.topElt2 + s.chunkSize*i;
}

void push (string val, Stack& s) {
	if(s.firstChunk == NULL || s.topElt2 >= s.chunkSize) {
		NodeChunk* temp = s.firstChunk;
		s.firstChunk = createNewNodeChunk(s.chunkSize);
		s.firstChunk->next = temp;
		s.topElt2 = 0;
	}
	s.firstChunk->val[s.topElt2] = val;
	s.topElt2++;

	s.topElt = s.topElt2-1; return;
}

void pop (Stack& s) {
	assert(!isEmpty(s));

	s.topElt2--;
	if(s.topElt2 <= 0) {
		NodeChunk* temp = s.firstChunk;
		s.firstChunk = s.firstChunk->next;
		delete[] temp->val;
		delete temp;
		s.topElt2 = s.chunkSize;
	}

	if(size(s) <= 0) {
		delete s.firstChunk;
		s.firstChunk = NULL;
	}

	s.topElt = s.topElt2-1; return;
}

string top (const Stack& s) {
	assert(!isEmpty(s));

	return s.firstChunk->val[s.topElt2-1];
}

void swap (Stack& s) {
	//assert(s.firstChunk->next != NULL || s.topElt2 >= 2);
	assert(size(s) >= 2);

	string temp1 = s.firstChunk->val[s.topElt2-1];
	pop(s);
	string temp2 = s.firstChunk->val[s.topElt2-1];
	pop(s);
	push(temp1,s);
	push(temp2,s);

	s.topElt = s.topElt2-1; return;
}

void print (const Stack& s) {
	NodeChunk* p=s.firstChunk;
	int size=s.topElt2;
	while(p!=NULL) {
		for(int i=size;i>= 0;i--) {
			cout<<p->val[i]<<endl;
		}
	p=p->next;
	size=s.chunkSize-1;	 
	}
}

int main(void){
	Stack s1;
	Stack s2;
	initStack(5, s1);
	initStack(1, s2);
	cout << "This should say 1: " << isEmpty(s1) << endl;
	push("alpha", s1);
	cout << "This should say alpha 0: " << top(s1) << " " << size(s1) << endl;
	push("beta", s1);
	push("gamma", s1);
	push("delta", s1);
	push("epsilon", s1);
	push("zeta", s1);
	cout << "This should say zeta 6: " << top(s1) << " " << size(s1) << endl;
	push("eta", s1);
	cout << "This should say eta 7: " << top(s1) << " " << size(s1) << endl;
	swap(s1);
	cout << "This should say zeta 7: " << top(s1) << " " << size(s1) << endl;
	cout << "This should print \"zeta eta epsilon delta gamma beta alpha\" over seven lines." << endl;
	while(!isEmpty(s1)){
		cout << top(s1) << endl;
		push(top(s1), s2);
		pop(s1);
	}
	cout << "This should say 7: " << size(s2) << endl;
	cout << "This should print \"alpha beta gamma delta epsilon eta zeta\" over seven lines." << endl;
	while(!isEmpty(s2)){
		cout << top(s2) << endl;
		pop(s2);
	}
	push("bad", s1);
	cout << "This should print 1 and then fail an assert: " << size(s1) << endl;
	swap(s1);

	/*
	Stack s;
	initStack(1,s);
	push("alpha",s);
	push("beta",s);
	print(s);

	top(s);
	swap(s);
	top(s);
	pop(s);
	pop(s);
	swap(s);
	print(s);

	s.topElt = s.topElt2-1; return 0;*/
}