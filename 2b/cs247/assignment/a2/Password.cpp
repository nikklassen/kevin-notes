#include <cstring>
#include <stdexcept>

#include "Password.h"


class PasswordError : public std::runtime_error {
public:
    PasswordError(const std::string& err) : std::runtime_error(err) {}
};


// constructor: ensure password validity, store password
Password::Password(std::string pwd) {
    std::string errMsg = "Password :\n";
    if(pwd.length() < 8) {
        errMsg = errMsg + "    - must be at least 8 characters\n";
    }

    int capitalCount = 0, digitCount = 0, symbolCount = 0;
    for(unsigned int i = 0; i < pwd.length(); ++i) {
        if(isupper(pwd[i])) {
            ++capitalCount;
        } else if(isdigit(pwd[i])) {
            ++digitCount;
        } else if(strchr("~!@#$%^&*()_-+={}[]:;<,>.?/", pwd[i])) {
            ++symbolCount;
        }
    }
    if(capitalCount < 2) {
        errMsg = errMsg + "    - must include at least 2 capital letters\n";
    }
    if(digitCount < 2) {
        errMsg = errMsg + "    - must include at least 2 digits\n";
    }
    if(symbolCount < 1) {
        errMsg = errMsg + "    - must include at least 1 symbol\n";
    }

    if(errMsg != "Password :\n") {
        throw PasswordError(errMsg);
    }
    _pwd = pwd;
}


// determine whether authentication is successful
bool Password::authenticate(std::string pwd) const {
    return _pwd == pwd;
}
