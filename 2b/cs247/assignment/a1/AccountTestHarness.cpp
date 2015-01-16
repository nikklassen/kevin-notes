#include <iostream>
#include <map>
#include <string>
#include <stdlib.h>


using namespace std;


//***********************
// Account Number ADT
//***********************

class AccountNo {                       /* Value range: 0001-9999 */
public:
    AccountNo ();                       /* New account number                          */
    explicit AccountNo ( int number );  /* Existing account number (for lookups)       */
    int number () const;                /* Accessor -- integer value of account number */
private:
    int number_;
    static int next_;
    static int const maxVal_ = 9999;
};


// initialization of static data member to the lowest legal value
int AccountNo::next_ = 1;


// constructor -- constructs a new unique account number
AccountNo::AccountNo () {
	if ( next_ > maxVal_ )
        exit (1);
    number_ = next_++;
}

// constructor -- converts an integer into an account number
// REQUIRES:  number corresponds to an existing account number
AccountNo::AccountNo ( int number ) {
	if ( (number > maxVal_) || (number < 1) )
		exit(1);
	number_ = number;
}


// accessor - returns account number value of object
int AccountNo::number() const {
	return number_;
}


// comparison operators
bool operator== (const AccountNo &a, const AccountNo &b) {
	return a.number() == b.number();
}

bool operator!= (const AccountNo &a, const AccountNo &b) {
	return !(a==b);
}

bool operator< (const AccountNo &a, const AccountNo &b) {
	return a.number() < b.number();
}

bool operator<= (const AccountNo &a, const AccountNo &b) {
	return (a<b) || (a==b);
}

bool operator> (const AccountNo &a, const AccountNo &b) {
	return !(a<=b);
}

bool operator>= (const AccountNo &a, const AccountNo &b) {
	return !(a<b);
}


// streaming operators
istream& operator>> ( istream &sin, AccountNo &a ) {
	int number;
	sin >> number;
	a = AccountNo(number);

	return sin;
}

ostream& operator<< ( ostream &sout, const AccountNo &a ) {
	sout.setf(ios::internal);
	sout.width(4);
	sout.fill('0');
	sout << a.number();

	return sout;
}


//*******************
// Account
//*******************

class Account {
public:                                             // PUBLIC interface of Account
	explicit Account ( const AccountNo& );          // constructor
	virtual ~Account() {}                           // destructor
	int balance () const;                           // accessor - returns balance as an integer
	virtual void call ( int duration ) = 0;         // records information about a call (e.g., duration of call in minutes)
	virtual void bill () = 0;                       // decrements balance by monthly fee and the cost of using extra minutes
	void pay (int amount);                          // increments balance by amount paid
	virtual void print() const;                     // prints information about the account (e.g., account number, balance, minutes used ths month


//************************************************************************
//* YOUR IMPLENTATION OF ACCOUNT, CHEAP_ACCOUNT, EXPENSIVE_ACCOUNT HERE
//************************************************************************

private:
    const AccountNo& _accountNumber;

    // current account balance
    int _balance;
    // account type's monthly base fee
    int _monthlyFee;
protected:
    // decrease current balance by monthly fee, with optional minutes
    void chargeAccount(const int minutes = 0);
    // set the monthly fee
    void setFee(const int);
};

// initialize account with account number, empty balance
Account::Account(const AccountNo& an) : _accountNumber(an), _balance(0) { }

// get account balance
int Account::balance() const {
    return _balance;
}

// pay value to account balance
void Account::pay(int amount) {
    _balance += amount;
}

// print common account information
void Account::print() const {
    cout << "  Account Number = " << _accountNumber << endl;
    if(_balance < 0) {
        cout << "  Balance = -$" << abs(_balance) << endl;
    } else {
        cout << "  Balance = $" << _balance << endl;
    }
}

// decrease current balance by monthly fee, with optional minutes
void Account::chargeAccount(const int minutes /* = 0 */) {
    _balance -= _monthlyFee;
    if(minutes > 0) {
        _balance -= minutes;
    }
}

// set the monthly fee
void Account::setFee(const int fee) {
    _monthlyFee = fee;
}


class CheapAccount : public Account {
    // monthly free minutes
    static const int FREE_MINUTES = 200;

    // minutes used in current month
    int _usedMinutes;
public:
    explicit CheapAccount(const AccountNo&);
    ~CheapAccount() {};
    void call(int duration);
    void bill();
    void print() const;
};

// initialize account, set monthly fee, initialize used minutes
CheapAccount::CheapAccount(const AccountNo& an) : Account(an) {
    Account::setFee(30);
    _usedMinutes = -FREE_MINUTES;
}

// record call information, increase used minutes
void CheapAccount::call(int duration) {
    _usedMinutes += duration;
}

// complete monthly pay period: charge account and reset minutes
void CheapAccount::bill() {
    Account::chargeAccount(_usedMinutes);
    _usedMinutes = -FREE_MINUTES;
}

// print all account information
void CheapAccount::print() const {
    cout << "CheapAccount:" << endl;
    Account::print();
    cout << "  Minutes = " << (_usedMinutes + FREE_MINUTES) << endl;
}


class ExpensiveAccount : public Account {
public:
    explicit ExpensiveAccount(const AccountNo&);
    ~ExpensiveAccount() {};
    void call(int duration);
    void bill();
    void print() const;
};

// initialize account, set monthly fee
ExpensiveAccount::ExpensiveAccount(const AccountNo& an) : Account(an) {
    Account::setFee(100);
}

// noop because minutes are not recorded for expensive account
void ExpensiveAccount::call(int duration) {
    (void)duration;
    return;
}

// complete monthly pay period: charge account
void ExpensiveAccount::bill() {
    Account::chargeAccount();
}

// print all account information
void ExpensiveAccount::print() const {
    cout << "ExpensiveAccount:" << endl;
    Account::print();
}

//************************************************************************
//  Helper variables and functions for test harness
//************************************************************************

//  test-harness operators
enum Op { NONE, NewE, NewC, Balance, Call, Bill, Pay, PrintAll };


//  converts a one-character input comment into its corresponding test-harness operator
Op convertOp( string opStr ) {
    switch( opStr[0] ) {
        case 'E': return NewE;
        case 'C': return NewC;
        case 'b': return Balance;
        case 'c': return Call;
        case 'B': return Bill;
        case 'p': return Pay;
        case 'P': return PrintAll;
        default: {
            cout << "Invalid operation " << opStr << endl;
            return NONE;
        }
    }
}


// Reads anumber from cin and finds the corresponding Phone Service account
// REQUIRES: the next word to read from cin is an integer
// RETURNS: a pointer to a found account.  Otherwise, returns NULL
Account* findAccount( map<AccountNo,Account*>  &accounts) {
    int num;
    cin >> num;
    AccountNo act( num );
    map<AccountNo,Account*>::iterator iter;
    iter = accounts.find( act );
    if ( iter == accounts.end() ) {
        cout << "Invalid Account Number!" << endl;
        return NULL;
    }
    return iter->second;
}


//*******************
// main()
//*******************

#ifndef DEBUG
int main () {
    cout << "Test harness for family of phone-service accounts:" << endl << endl;
    map<AccountNo, Account*> accounts;

    cout << "Command: ";
    string command;
    cin >> command;

    Op op = convertOp(command);

    while ( !cin.eof() ) {
        switch ( op ) {
                /* Constructors */
            case NewE: {
                AccountNo act;
                Account* p = new ExpensiveAccount( act );
                accounts.insert( pair<AccountNo,Account*>( act, p ) );
                p->print();
                break;
            }
            case NewC: {
                AccountNo act;
                Account* p = new CheapAccount( act );
                accounts.insert( pair<AccountNo,Account*>( act, p ) );
                p->print();
                break;
            }

                /* Accessors */
            case Balance: {
                Account* p = findAccount( accounts );
                if ( p!= NULL)
                    cout << "Value of balance data member is: " << p->balance() << endl;
                break;
            }

                /* Phone Service Operations */
            case Call: {
                Account* p = findAccount( accounts );
                if ( p != NULL ) {
                    int duration;
                    cin >> duration;
                    p->call( duration );
                }
                break;
            }


            case Bill: {
                map<AccountNo,Account*>::iterator iter;
                map<AccountNo,Account*>::iterator end = accounts.end();
                for ( iter = accounts.begin(); iter != end; iter++ ) {
                    (iter->second)->bill();
                }
                break;
            }


            case Pay: {
                Account* p = findAccount( accounts );
                if (p != NULL ) {
                    int amt;
                    cin >> amt;
                    p->pay(amt);
                }
                break;
            }


                /* Print Accounts */
            case PrintAll: {
                map<AccountNo,Account*>::iterator iter;
                map<AccountNo,Account*>::iterator end = accounts.end();
                for ( iter = accounts.begin(); iter != end; iter++ ) {
                    (iter->second)->print();
                }
                break;
            }
            default: {
                break;
            }
        } // switch

        cout << endl << "Command: ";
        cin >> command;
        op = convertOp(command);

    } // while cin OK

    map<AccountNo,Account*>::iterator iter;
    map<AccountNo,Account*>::iterator end = accounts.end();
    for ( iter = accounts.begin(); iter != end; iter++ )
      delete iter->second;

    return 0;
}
#endif
