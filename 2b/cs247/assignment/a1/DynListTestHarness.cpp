#include <iostream>

using namespace std;


//************************************************************************
//  DynList ADT
//************************************************************************

class DynList {
public:
    DynList();                                  // default constructor
    DynList(const DynList&);                    // copy constructor
    ~DynList();                                 // destructor
    DynList& operator=( const DynList& );       // assignment
    bool operator==( const DynList& ) const;    // equality
    string& operator[]( int i );				// accessor
    int size() const;                           // accessor
private:
    class Node;
    Node *list_;
    Node *last_;
    int size_;

    // copy rhs into lhs
    DynList& copy(DynList& lhs, const DynList& rhs);
    // free all memory assoiated with dl
    void free(DynList& dl);
};


class DynList::Node {
    string value;
    Node *next;

    friend string& DynList::operator[] (int i);
public:
    // get the current node value
    string getValue() const;
    // get the next node in series
    Node* getNext() const;
};

// accessor -- operator[]
string& DynList::operator[] (int i) {
    // check whether array needs to grow
    if (0 <= i && i < size_) {

        // locate ith node and return contents
        Node *curr;
        int index;
        for (curr = list_, index = 0; index < i; curr = curr->next, index+=1);
        return curr->value;
    }

    // otherwise, need to grow the list. New elements have empty strings
    else {
        if (size_ == 0) {
            Node *node = new Node;
            node->value = "";
            node->next = 0;
            list_ = node;
            last_ = node;
            size_ = 1;
        }
        for (int index = size_; index <= i; index+=1) {
            Node *node = new Node;
            node->value = "";
            node->next = 0;
            last_->next = node;
            last_ = node;
            size_ += 1;
        }

        return last_->value;
    }
}

// accessor - size of list
int DynList::size() const {
    return size_;
}

// get the current node value
string DynList::Node::getValue() const {
    return value;
}

// get the next node in series
DynList::Node* DynList::Node::getNext() const {
    return next;
}

// create empty list
DynList::DynList() : size_(0) { }

// create empty list, copy in copied list
DynList::DynList(const DynList& dl) : size_(0) {
    DynList::copy(*this, dl);
}

// free all associated memory
DynList::~DynList() {
    DynList::free(*this);
}

// create deep copy on DynList assignment
DynList& DynList::operator=(const DynList& dl) {
    DynList::free(*this);

    return DynList::copy(*this, dl);
}

// define comparison operator to ensure all node string values are equal
bool DynList::operator==(const DynList& dl) const {
    if(size() != dl.size()) {
        return false;
    }

    DynList::Node* lhs = this->list_;
    DynList::Node* rhs = dl.list_;
    for(int index = 0; index < dl.size(); ++index) {
        if(lhs->getValue() != rhs->getValue()) {
            return false;
        }
        lhs = lhs->getNext();
        rhs = rhs->getNext();
    }
    return true;
}

// copy rhs list into lhs list
DynList& DynList::copy(DynList& lhs, const DynList& rhs) {
    if(lhs == rhs) {
        return lhs;
    }

    DynList::Node* curr = rhs.list_;
    for(int index = 0; index < rhs.size(); ++index, curr = curr->getNext()) {
        lhs[index] = curr->getValue();
    }
    return lhs;
}

// free all associated memory
void DynList::free(DynList& dl) {
    DynList::Node *curr, *temp;
    curr = dl.list_;
    for(int index = 0; index < dl.size(); ++index) {
        temp = curr->getNext();
        delete curr;
        curr = temp;
    }
    size_ = 0;
}

//************************************************************************
//  Helper variables and functions for test harness
//************************************************************************

//  test harness commands
enum Op {NONE, Con, Des, Size, Copy, Read, Mut, Assign, Eq};


// parse input command
Op convertOp(string opStr) {
    switch (opStr[0]) {
        case 'x': return Con;
        case 'd': return Des;
        case 's': return Size;
        case 'c': return Copy;
        case 'r': return Read;
        case 'm': return Mut;
        case 'a': return Assign;
        case 'e': return Eq;
        default: return NONE;
    }
}

// parse name of dynamic list that is to be operated on
int readName() {
    int index = -1;
    cin >> index;
    if ( index >= 0 && index <= 9 ) return index;

    cout << "Invalid name of dynamic list!" << endl;

    // try to fix cin
    cin.clear();
    string junk;
    getline( cin, junk );

    return -1;
}


//*******************
// main()
//*******************

#ifndef DEBUG
int main() {
    cout << "Test harness for DynList ADT:" << endl << endl;


    // create a collection of dynamic lists to manipulate
	DynList *lists[10] = {0};

    // get input command
    cout << "Command: ";
    string command;
    cin >> command;

    Op op = convertOp(command);

    while ( !cin.eof() ) {
        switch (op) {
            // default construction of a new dynamic list
            case Con: {
                int name = readName();
                if ( name != -1 ) {
                    delete lists[name];
                    lists[name] = new DynList;
                }
                break;
            }

            // destroy a dynamic list
            case Des: {
                int name = readName();
                if ( name != -1 ) {
                    if ( lists[name] == 0 ) {
                        cout << "List " << name << " is not yet defined." << endl;
                        break;
                    }
                    delete lists[name];
                    lists[name] = 0;
                }
                break;
            }

                // use operator[] to read/access element in a list
            case Read: {
                int name = readName();
                if ( name != -1 ) {
                    if ( lists[name] == 0 ) {
                        cout << "List " << name << " is not yet defined." << endl;
                        break;
                    }
                    int index;
                    cin >> index;
                    if ( index < 0 ) {
                        cout << "Invalid index." << endl;
                        break;
                    }
                    cout << "Value of the " << index << "th element of " << name << "th list  == \"" << ( *lists[name] )[index] << "\"" << endl;;
                }
                break;
            }

            // change the value of an element in a list, by assigning to the result of operator[]
            case Mut: {
                int name = readName();
                if ( name != -1 ) {
                    if (lists[name] == 0) {
                        cout << "List " << name << " is not yet defined." << endl;
                        break;
                    }
                    int index;
                    cin >> index;
                    if ( index < 0 ) {
                        cout << "Invalid index." << endl;
                        break;
                    }
                    string newValue;
                    cin >> newValue;
                    ( *lists[name] )[index] = newValue;
                    cout << "Value of the " << index << "th element of " << name << "th list == \"" << ( *lists[name] )[index] << "\"" << endl;;
                }
                break;
            }

            // query the size of a list
            case Size: {
                int name = readName();
                if ( name != -1 ) {
                    if (lists[name] == 0) {
                        cout << "List " << name << " is not yet defined." << endl;
                        break;
                    }
                    cout << "Size of the " << name << "th list  = " << lists[name]->size() << endl;
                }
                break;
            }

            // test whether two lists are equal copies of each other
            case Eq: {
                int name = readName();
                if ( name != -1 ) {
                    if ( lists[name] == 0 ) {
                        cout << "List " << name << " is not yet defined." << endl;
                        break;
                    }

                    int name2 = readName();
                    if ( name2 != -1 ) {
                        if ( lists[name2] == 0 ) {
                            cout << "List " << name2 << " is not yet defined." << endl;
                            break;
                        }

                        string eq = (*lists[name] == *lists[name2]) ? " " : " not ";
                        cout << "Lists " << name << " and " << name2 << " are" << eq << "equal." << endl;
                    }
                }
                break;
            }

            // assign one dynamic list to another.  Print contents of both lists, to check results
            case Assign: {
                int name = readName();
                if ( name != -1 ) {
                    if ( lists[name] == 0 ) {
                        cout << "List " << name << " is not yet defined." << endl;
                        break;
                    }

                    int name2 = readName();
                    if ( name2 != -1 ) {
                        if (lists[name2] == 0) {
                            cout << "List " << name2 << " is not yet defined." << endl;
                            break;
                        }

                        *lists[name] = *lists[name2];

                        cout << "Size of " << name << "th list = " << lists[name]->size() << endl;
                        cout << "Value of " << name << "th list = [";
                        for (int i=0; i < (lists[name]->size()); i+=1) {
                            cout << (*lists[name])[i] << ",";
                        }
                        cout << "]" << endl;
                        cout << "Size of " << name2 << "th list = " << lists[name2]->size() << endl;
                        cout << "Value of " << name2 << "th list = [";
                        for (int i=0; i < (lists[name2]->size()); i+=1) {
                            cout << (*lists[name2])[i] << ",";
                        }
                        cout << "]" << endl;
                    }
                }
                break;
            }

            // use copy constructor to create a new list, initializing from an existing list
            // print the contents of both lists, to check results
            case Copy: {
                int name = readName();
                if ( name != -1 ) {
                    if (lists[name] == 0) {
                        cout << "List " << name << " is not yet defined." << endl;
                        break;
                    }

                    DynList copy(*lists[name]);

                    cout << "Size of " << name << "th list = " << lists[name]->size() << endl;
                    cout << "Value of " << name << "th list = [";
                    for (int i=0; i < (lists[name]->size()); i+=1) {
                        cout << (*lists[name])[i] << ",";
                    }
                    cout << "]" << endl;

                    cout << "Size of copy" << " = " << copy.size() << endl;
                    cout << "Value of copy" << " = [";
                    for (int i=0; i < (copy.size()); i+=1) {
                        cout << copy[i] << ",";
                    }
                    cout << "]" << endl;
                }
                break;
            }
            default: {
                cout << "Invalid command." << endl;
            }
        } // switch command

        cout << endl << "Command: ";
        cin >> command;
        op = convertOp(command);


    } // while cin OK

    for (int i = 0; i < 10; i++) {
        delete lists[i];
    }
}
#endif
