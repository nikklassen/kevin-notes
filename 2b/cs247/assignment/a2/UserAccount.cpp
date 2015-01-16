#include <limits>
#include <stdexcept>

#include "UserAccount.h"
#include "Userid.h"


// constructor: set username and password, print any error messages
// and try again (if there were errors)
UserAccount::UserAccount() {
    UserAccount::set("userid");
    UserAccount::set("password");
}


// set the username of password for this account, print any
// error messages and try again (if there were errors)
void UserAccount::set(std::string member) {
    bool valid = false;
    while(!valid) {
        valid = true;

        std::string sTemp;
        std::cout << "Enter preferred " << member << ": ";
        std::cin >> sTemp;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
        try {
            if(member == "userid") {
                userid_.reset(new Userid(sTemp));
            } else {
                passwd_.reset(new Password(sTemp));
            }
        } catch(const std::runtime_error err) {
            std::cout << err.what();
            valid = false;
        }
    }
}

// attempt to authenticate: given a maximum number of tries, show the
// password prompt. Fail if account is deactivated, deactivate account
// if more than max tries are unsuccessful
void UserAccount::authenticate(int numtries /* = 3 */) {
    if(check_deactivated()) {
        std::cout << "Account has been deactivated." << std::endl;
        return;
    }

    for(int attempt = 1; attempt <= numtries; ++attempt) {
        std::string pwd;

        std::cout << "Enter password: ";
        std::cin >> pwd;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        if(passwd_.get()->authenticate(pwd)) {
            return;
        } else if(attempt < 3) {
            std::cout << "Invalid password. You have " << (3-attempt) << " tries to get it right." << std::endl;
        } else {
            break;
        }
    }

    std::cout << "Imposter!! Account is being deactivated!!" << std::endl;
    deactivate();
}

// deactivate account
void UserAccount::deactivate() {
    userid_.get()->deactivate();
}
// reactivate account, require a new password to be set
void UserAccount::reactivate() {
    userid_.get()->activate();

    UserAccount::set("password");
}

// determine whether an account is deactivated
bool UserAccount::check_deactivated() const {
    return !userid_.get()->isActive();
}
