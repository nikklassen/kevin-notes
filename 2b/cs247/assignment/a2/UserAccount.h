#ifndef USERACCOUNT_H
#define USERACCOUNT_H

#include <memory>

#include "Password.h"
#include "Userid.h"


class UserAccount {
    std::auto_ptr<Password> passwd_;
    std::auto_ptr<Userid> userid_;
public:
    UserAccount();

    void authenticate(int numtries = 3);
    void deactivate();
    void reactivate();

    bool check_deactivated() const;
private:
    UserAccount(const UserAccount&);
    void operator=(const UserAccount&);

    void set(std::string member);
};

#endif
