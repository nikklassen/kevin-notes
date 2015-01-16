#include <iostream>
#include <string>
#include <cassert>
using namespace std;

struct BST_Node {
    string key;
    string stuff;
    BST_Node* left;
    BST_Node* right;
};

typedef BST_Node* BST;

void BST_init(BST& root) {
    root = NULL;
}

bool BST_isEmpty(const BST root) {
    return root == NULL;
}

bool BST_has(string key, const BST root) {
	if(BST_isEmpty(root)) return false;

	if(root->key == key) return true;
	else if(root->key > key) return BST_has(key, root->left);
	else return BST_has(key, root->right);
}

void BST_insert(string key, BST& root) {
	if (BST_has(key, root)) {
		cerr << "duplicate key: " << key << endl;
		assert(false);
	}

	if(BST_isEmpty(root)) {
		BST newBST = new BST_Node;
		newBST->key = key;
		newBST->left = newBST->right = NULL;
		root = newBST;
	}
	else if(key < root->key) BST_insert(key, root->left);
	else BST_insert(key, root->right);
}

void BST_print(BST curNode) {
	if(BST_isEmpty(curNode)) return;

	BST_print(curNode->left);
	cout << curNode->key << endl;
	BST_print(curNode->right);
}

BST findLargest(BST& root) {
	if(BST_isEmpty(root->right) || root->key > root->right->key) return root;
	else return findLargest(root->right);
}

void BST_delete(string key, BST& root) {
	if(root->key == key) {
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
			root->key = temp->key;
			BST_delete(temp->key, root->left);
		}
	}
	else if(key < root->key) return BST_delete(key, root->left);
	else return BST_delete(key, root->right);
}

int main() {
	BST test;
	BST_init(test);

	BST_insert("6", test);
	BST_insert("2", test);
	BST_insert("9", test);
	BST_insert("1", test);
	BST_insert("13", test);
	BST_insert("82", test);
	BST_insert("7", test);

	BST_print(test);
	cout << "---" << endl;

	BST_delete("1", test);
	BST_print(test);
	cout << "---" << endl;

	BST test2;
	BST_init(test2);

	BST_insert("1", test2);
	BST_insert("2", test2);
	BST_insert("3", test2);
	BST_insert("4", test2);
	BST_insert("5", test2);
	BST_insert("6", test2);
	BST_insert("7", test2);

	BST_print(test2);
	cout << "---" << endl;

	BST test3;
	BST_init(test3);

	BST_insert("f", test3);
	BST_insert("b", test3);
	BST_insert("i", test3);
	BST_insert("a", test3);
	BST_insert("m", test3);
	BST_insert("z", test3);
	BST_insert("g", test3);

	BST_print(test3);
	cout << "---" << endl;

	BST_delete("a", test3);
	BST_print(test3);
	cout << "---" << endl;

	return 0;
}
