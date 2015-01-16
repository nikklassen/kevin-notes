#ifndef DATE_H
#define DATE_H

#include <iostream>
#include <string>


class Date {
    struct Impl;
    Impl* pimpl;

    void copy(const Date&);
public:
    Date(int day, std::string month, int year);    // constructor -- NO DEFAULT CONSTRUCTOR
    Date(const Date&);                             // copy constructor
    ~Date();                                       // destructor

    static Date today();                           // returns new Date value = today

    int day() const;                               // accessor
    std::string month() const;                     // accessor
    int year() const;                              // accessor

    Date& operator=(const Date&);                  // assignment
};

Date incDays(const Date&, long);  // increment Date by num days
Date incMonths(const Date&, int); // increment Date by num months - round down if invalid, return new Date
Date incYears(const Date&, int);  // increment Date by num years - round down if invalid, return new Date

bool operator==(const Date&, const Date&);
bool operator!=(const Date&, const Date&);
bool operator<(const Date&, const Date&);
bool operator<=(const Date&, const Date&);
bool operator>(const Date&, const Date&);
bool operator>=(const Date&, const Date&);

std::ostream& operator<<(std::ostream&, const Date&);
std::istream& operator>>(std::istream&, Date&);

#endif
