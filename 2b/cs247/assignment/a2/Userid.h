#ifndef USERID_H
#define USERID_H

#include <iostream>
#include <set>


class Userid {
    static std::set<std::string> _uids;

    std::string _uid;
    bool _active;
public:
    Userid(std::string uid);
    ~Userid();

    void activate();
    void deactivate();

    bool isActive() const;
private:
    Userid();
    Userid(const Userid&);
    void operator=(const Userid&);
};

#endif
