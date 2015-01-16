#include <iostream>
#include <string>
#include <cassert>

using namespace std;

struct Node {
    string val;
    Node* next;
    Node* prev;
};

struct Stew {
    Node* first;
    Node* last;
};

Stew initStew() { 
	Stew s;
	s.first = s.last = NULL;
	return s;
}

bool isEmpty(Stew s) { 
	return s.first == NULL; 
}

Stew push(string element, Stew s) {
	Node* newNode = new Node;
	newNode->val = element;
	newNode->prev = NULL;

	if(isEmpty(s)) {
		newNode->next = NULL;
		s.first = s.last = newNode;
	}
	else {
		newNode->next = s.first;
		s.first->prev = newNode;
		s.first = newNode;
	}

	return s;
}

Stew pop(Stew s) {
	assert(!isEmpty(s));

	Node* temp = s.first;

	if(s.first == s.last) {
		s.first = s.last = NULL;
	}
	else {
		s.first = s.first->next;
		s.first->prev = NULL;
	}
	delete temp;

	return s;
}

string top(Stew s) {
	assert(!isEmpty(s));

	return s.first->val;
}

Stew enter(string element, Stew s) {
	Node* newNode = new Node;
	newNode->val = element;
	newNode->next = NULL;

	if(isEmpty(s)) {
		newNode->prev = NULL;
		s.first = s.last = newNode;
	}
	else {
		newNode->prev = s.last;
		s.last->next = newNode;
		s.last = newNode;
	}

	return s;
}

Stew leave(Stew s) {
	assert(!isEmpty(s));

	Node* temp = s.last;

	if(s.first == s.last) {
		s.first = s.last = NULL;
	}
	else {
		s.last = s.last->prev;
		s.last->next = NULL;
	}
	delete temp;

	return s;
}

string first(Stew s) {
	assert(!isEmpty(s));

	return s.last->val;
}

void print(Stew s, char direction) {
	if(isEmpty(s)) return;
	Node* current = new Node;
	
	switch(direction) {
		case 'f':
			current = s.first;
			while(current != s.last) {
				cout << current->val << " ";
				current = current->next;
			}
			cout << current->val << endl;
			break;
		case 'r':
			current = s.last;
			while(current != s.first) {
				cout << current-> val << " ";
				current = current->prev;
			}
			cout << current->val << endl;
			break;
		default:
			cerr << "Illegal direction: '" << direction << "'" << endl;
			assert(false);
			break;
	}

	return;
}

Stew nuke(Stew s) {
	if(isEmpty(s)) return s;
	Node* current = s.last;

	while(current != s.first) {
		current = current->prev;
		delete current->next;
	}
	delete current;
	s.first = s.last = NULL;

	return s;
}

Stew reverse(Stew s) {
	if(isEmpty(s) || s.first == s.last) return s;

	Stew end = initStew();
	Node* current = s.first;

	while(current != s.last) {
		end = push(current->val, end);
		current = current->next;
	}
	end = push(current->val, end);

	return end;
}

int main (int argc, char* argv[]) {
	Stew s1 = initStew();
	s1 = push("alpha", s1);
	s1 = push("beta", s1);
	s1 = push("gamma", s1);
	s1 = enter("delta", s1);
	// This prints "gamma beta alpha delta"
	print (s1, 'f');
	// This prints "delta alpha beta gamma"
	print (s1, 'r');
	s1 = reverse(s1);
	// This prints "delta alpha beta gamma"
	print(s1, 'f');
	s1 = pop (s1);
	s1 = leave (s1);
	// This prints "alpha beta"
	cout << top (s1) << " " << first (s1) << endl;
	// This has no output, but is good form to call when done.
	s1 = nuke (s1);
	// This should now succeed.
	assert(isEmpty (s1));
}
