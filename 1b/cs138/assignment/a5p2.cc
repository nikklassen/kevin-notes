#include <iostream>
#include <string>
#include <cstdlib>
#include <cassert>
#include <ctime>
#include <map>
using namespace std;

int myrand();

struct SBLnode {
	string name;
	SBLnode *next;
	SBLnode *left, *right;
};

struct Queue {
	SBLnode *first, *last;
};

typedef SBLnode* BST;

struct SBL {
	Queue q;
	BST root;
};

void SBL_init(SBL& sbl) {
	sbl.root = NULL;
	sbl.q.first = sbl.q.last = NULL;
}

int SBL_size(const SBL& sbl) {
	BST current = sbl.q.first;
	int i = 0;

	while(current != NULL) {
		i++;
		current = current->next;
	}

	return i;
}

bool BST_isEmpty(const BST root) {
    return root == NULL;
}

bool BST_has(string name, const BST root) {
	if(BST_isEmpty(root)) return false;

	if(root->name == name) return true;
	else if(root->name > name) return BST_has(name, root->left);
	else return BST_has(name, root->right);
}

BST BST_insert(string name, BST& root) {
	if (BST_has(name, root)) {
		cerr << "duplicate name: " << name << endl;
		assert(false);
	}

	BST newBST = new SBLnode;
	newBST->name = name;
	newBST->left = newBST->right = NULL;
	if(BST_isEmpty(root)) root = newBST;
	else if(name < root->name) BST_insert(name, root->left);
	else BST_insert(name, root->right);
	return newBST;
}

void SBL_arrive(string name, SBL& sbl) {
	BST temp = BST_insert(name, sbl.root);
	if(sbl.q.last != NULL) {
		sbl.q.last->next = temp;
		sbl.q.last = sbl.q.last->next;
	}
	else {
		sbl.q.first = sbl.q.last = temp;
	}
}

BST findLargest(BST& root) {
	if(BST_isEmpty(root->right) || root->name > root->right->name) return root;
	else return findLargest(root->right);
}

void BST_delete(string name, BST& root) {
	if(root->name == name) {
		if(BST_isEmpty(root->left) && BST_isEmpty(root->right)) {
			BST temp = root;
			root = NULL;
			delete temp;
			return;
		}
		else if(BST_isEmpty(root->right)) {
			BST temp = root;
			root = root->left;
			delete temp;
			return;
		}
		else if(BST_isEmpty(root->left)) {
			BST temp = root;
			root = root->right;
			delete temp;
			return;
		}
		else {
			BST temp = findLargest(root->left);
			root->name = temp->name;
			BST_delete(temp->name, root->left);
		}
	}
	else if(name < root->name) return BST_delete(name, root->left);
	else return BST_delete(name, root->right);
}

void SBL_leave(SBL& sbl) {
	BST removeThis = sbl.q.first;

	sbl.q.first = sbl.q.first->next;
	BST_delete(removeThis->name, sbl.root);

	if(SBL_size(sbl) == 0) sbl.q.first = sbl.q.last = NULL;
}

string SBL_first(const SBL& sbl) {
	return sbl.q.first->name;
}

bool SBL_lookup(const SBL& sbl, string name) {
	BST current = sbl.q.first;

	while(current != NULL) {
		if(current->name == name) return true;
		current = current->next;
	}

	return false;
}

void SBL_printInArrivalOrder(const SBL& sbl) {
	BST current = sbl.q.first;

	while(current != NULL) {
		cout << current->name << endl;
		current = current->next;
	}
}

void BST_print(BST curNode) {
	if(BST_isEmpty(curNode)) return;

	BST_print(curNode->left);
	cout << curNode->name << endl;
	BST_print(curNode->right);
}

void SBL_printInAlphabeticalOrder(const SBL& sbl) {
	BST_print(sbl.root);
}

int main(){
    SBL* root = new SBL;
    SBL_init(*root);
    SBL_arrive("gamma", *root);
    SBL_printInArrivalOrder(*root);
    SBL_printInAlphabeticalOrder(*root);  // should print gamma
    cout << "We have gamma: " << SBL_lookup(*root, "gamma") << endl; // should end with 1
    cout<<"size="<<SBL_size(*root)<<endl;
    SBL_arrive("tango", *root);
    SBL_arrive("hotel", *root);
    SBL_arrive("alpha", *root);
    SBL_arrive("beta", *root);
    SBL_arrive("serria", *root);
    SBL_arrive("topher", *root);
    SBL_arrive("athena", *root);
    SBL_arrive("alaric", *root);
    SBL_arrive("xcellent", *root);
    cout << "We have alaric and xcellent: " << SBL_lookup(*root, "alaric") << SBL_lookup(*root, "xcellent") << endl; // should print 11
    SBL_printInArrivalOrder(*root);
    SBL_printInAlphabeticalOrder(*root);
    cout << "deleting stuff" << endl;
    cout << "Deleting " << SBL_first(*root) << endl;
    SBL_leave(*root);
    cout << "deleted gamma" << endl;
    //SBL_printInArrivalOrder(*root);
    //SBL_printInAlphabeticalOrder(*root);
    cout << "Deleting " << SBL_first(*root) << endl;
    SBL_leave(*root);
    cout << "deleted tango" << endl;
    //SBL_printInArrivalOrder(*root);
    //SBL_printInAlphabeticalOrder(*root);
    cout << "Deleting " << SBL_first(*root) << endl;
    SBL_leave(*root);
    cout << "deleted hotel" << endl;
    //SBL_printInArrivalOrder(*root);
    //SBL_printInAlphabeticalOrder(*root);
    cout << "We don't have gamma: " << SBL_lookup(*root, "gamma") << endl;
    cout << "Deleting " << SBL_first(*root) << endl;
    SBL_leave(*root);
    cout << "deleted alpha" << endl;
    SBL_printInArrivalOrder(*root);
    SBL_printInAlphabeticalOrder(*root);
    cout << "Deleting " << SBL_first(*root) << endl;
    SBL_leave(*root);
    cout << "deleted beta" << endl;
    //SBL_printInArrivalOrder(*root);
    //SBL_printInAlphabeticalOrder(*root);
    cout << "Deleting " << SBL_first(*root) << endl;
    SBL_leave(*root);
    cout << "deleted serria" << endl;
    //SBL_printInArrivalOrder(*root);
    //SBL_printInAlphabeticalOrder(*root);
    cout << "Deleting " << SBL_first(*root) << endl;
    SBL_leave(*root);
    cout << "deleted topher" << endl;
    SBL_printInArrivalOrder(*root);
    SBL_printInAlphabeticalOrder(*root);
    cout << "Deleting " << SBL_first(*root) << endl;
    SBL_leave(*root);
    cout << "deleted athena" << endl;
    SBL_printInArrivalOrder(*root);
    SBL_printInAlphabeticalOrder(*root);
    cout << "Deleting " << SBL_first(*root) << endl;
    SBL_leave(*root);
    cout << "deleted alaric" << endl;
    SBL_printInArrivalOrder(*root);
    SBL_printInAlphabeticalOrder(*root);
    cout << "Deleting " << SBL_first(*root) << endl;
    SBL_leave(*root);
    cout << "deleted xcellent" << endl;
    cout << "queue is supposed to be empty" << endl;
    cout<<"Size="<<SBL_size(*root)<<endl;
    cout << root->q.first << ": q.first, " << root->q.last << ": q.last" << endl;
    delete root;
}
