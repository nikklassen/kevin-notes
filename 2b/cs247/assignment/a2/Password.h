#ifndef PASSWORD_H
#define PASSWORD_H

#include <iostream>


class Password {
    std::string _pwd;
public:
    Password(std::string pwd);
    bool authenticate(std::string pwd) const;
private:
    Password();
};

#endif
