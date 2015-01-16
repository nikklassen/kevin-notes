#include <iostream>
#include <string>
#include <cassert>

using namespace std;

struct Node {
	string val;
	Node *next;
};

Node* makeList() {
	Node* base = new Node;
	Node* current = base;
	string input;

	cin >> input;
	current->val = input;
	current->next = 0;

	if(current != 0) {
		while(cin >> input) {
			Node* temp = new Node;
			temp->val = input;
			current->next = temp;
			current = current->next;
		}
	}
	current->next = NULL;

	return base;
}

void printList(Node* p) {
	assert(p);

	Node* current = p;
	if(current != 0) {
		while(current->next != 0) {
			cout << current->val << endl;
			current = current->next;
		}
	}
	cout << current->val << endl;

	return;
}

void printPairInOrder(Node* p1, Node* p2) {
	assert(p1);
	assert(p2);

	if(p1->val <= p2->val) {
		cout << p1->val << endl;
		cout << p2->val << endl;
	}
	else {
		cout << p2->val << endl;
		cout << p1->val << endl;
	}

	return;
}

Node* sortPair(Node* p1, Node* p2) {
	assert(p1);
	assert(p2);

	Node* sorted = new Node;
	if(p1->val <= p2->val) {
		p1->next = p2;
		p2->next = NULL;
		sorted = p1;
	}
	else {
		p2->next = p1;
		p1->next = NULL;
		sorted = p2;
	}

	return sorted;
}

Node* makePairList(string s1, string s2) {
	Node* sorted = new Node;
	Node* temp = new Node;
	if(s1 <= s2) {
		sorted->val = s1;
		temp->val = s2;
	}
	else {
		sorted->val = s2;
		temp->val = s1;
	}
	temp->next = NULL;
	sorted->next = temp;

	return sorted;
}

void printReverseRecursive() {
	string input;

	if(cin >> input) {
		printReverseRecursive();
	}

	if(input != "\n" && input != "") cout << input << endl;
	return;
}

int main() {
	printReverseRecursive();

	return 0;
}