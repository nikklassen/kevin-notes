#include <stdexcept>

#include "Userid.h"


class UseridError : public std::runtime_error {
public:
    UseridError(const std::string& err) : std::runtime_error(err) {}
};


std::set<std::string> Userid::_uids;


// constructor: ensure id is unique, add it to static list, active account
Userid::Userid(std::string uid) {
    if(!_uids.insert(uid).second) {
        throw UseridError("Userid \"" + uid + "\" already exists.  Please try again.\n");
    }
    _uid = uid;

    Userid::activate();
}


// desctructor: remove uid from static list
Userid::~Userid() {
    _uids.erase(_uid);
}


// activate account
void Userid::activate() {
    _active = true;
}

// deactivate account
void Userid::deactivate() {
    _active = false;
}

// determine if account is active
bool Userid::isActive() const {
    return _active;
}
