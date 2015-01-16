#include <ctime>
#include "Date.h"


// namespace for all conversion utilities for months
namespace convert {
    const std::string months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    // converts a int from 1-12 to its corresponding month
    std::string intToMonth(int m) {
        try {
            return months[m-1];
        } catch(...) {
            throw "Invalid month.";
        }
    }

    // converts a month to its corresponding int from 1-12
    int monthToInt(std::string m) {
        for(int i = 0; i < 12; ++i) {
            if(m == months[i]) {
                return i+1;
            }
        }
        throw "Invalid month.";
    }
}


// namespace for all checking utilities for dates
namespace check {
    // get the number of days in a month of a given year
    int daysInMonth(int month, int year) {
        if(month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else if(month == 2) {
            bool isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
            if(isLeapYear) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return 31;
        }
    }

    // determine whether a day is invalid (out-of-bounds)
    bool isInvalidDay(int day, int month, int year) {
        return (1 > day || day > daysInMonth(month, year));
    }

    // ensure the provided date is valid
    void validDate(int day, std::string month, int year) {
        int intMonth = convert::monthToInt(month);
        if(1900 <= year && year <= 2100) {
            if(1 <= intMonth && intMonth <= 12) {
                if(isInvalidDay(day, intMonth, year)) {
                    throw "Invalid day of the month.";
                }
            } else {
                throw "Invalid month.";
            }
        } else {
            throw "Invalid year.";
        }
    }
}


// container for day, month, year
struct Date::Impl {
    int day;
    std::string month;
    int year;
};


// constructor: ensures input date is valid, then sets values
Date::Date(int day, std::string month, int year) {
    check::validDate(day, month, year);

    pimpl = new Impl;
    pimpl->day = day;
    pimpl->month = month;
    pimpl->year = year;
}

// copy contructor: sets local values from argument
Date::Date(const Date& d) {
    copy(d);
}

// destructor: frees memory in instance
Date::~Date() {
    delete pimpl;
}


// returns a date instance containing today's date
Date Date::today() {
    time_t t = time(0);
    struct tm* now = localtime(&t);
    return Date(now->tm_mday, convert::intToMonth(now->tm_mon + 1), now->tm_year + 1900);
}


// accessors
int Date::day() const {
    return pimpl->day;
}

std::string Date::month() const {
    return pimpl->month;
}

int Date::year() const {
    return pimpl->year;
}

// assignment: sets local values from argument
Date& Date::operator=(const Date& d) {
    if(*this != d) {
        delete pimpl;
        copy(d);
    }
    return *this;
}

// helper convenience method for copying argument values to this
void Date::copy(const Date& d) {
    pimpl = new Impl;
    pimpl->day = d.day();
    pimpl->month = d.month();
    pimpl->year = d.year();
}


// incrementors
Date incDays(const Date& d, long amt) {
    int intDay = d.day() + static_cast<int>(amt);
    int intMonth = convert::monthToInt(d.month());
    int intYear = d.year();

    while(check::isInvalidDay(intDay, intMonth, intYear)) {
        intDay -= check::daysInMonth(intMonth, intYear);
        intMonth += 1;
        if(intMonth > 12) {
            intMonth -= 12;
            intYear++;
        }
    }
    return Date(intDay, convert::intToMonth(intMonth), intYear);
}

Date incMonths(const Date& d, int amt) {
    int intDay = d.day();
    int intMonth = convert::monthToInt(d.month()) + amt;
    int intYear = d.year();

    while(intMonth > 12) {
        intMonth -= 12;
        intYear++;
    }
    while(check::isInvalidDay(intDay, intMonth, intYear)) {
        intDay--;
    }
    return Date(intDay, convert::intToMonth(intMonth), intYear);
}

Date incYears(const Date& d, int amt) {
    int intDay = d.day();
    int intMonth = convert::monthToInt(d.month());
    int intYear = d.year() + amt;
    while(check::isInvalidDay(intDay, intMonth, intYear)) {
        intDay--;
    }
    return Date(intDay, d.month(), intYear);
}


// operators
bool operator==(const Date& lhs, const Date& rhs) {
    if(lhs.year() == rhs.year()) {
        if(lhs.month() == rhs.month()) {
            if(lhs.day() == rhs.day()) {
                return true;
            }
        }
    }
    return false;
}

bool operator!=(const Date& lhs, const Date& rhs) {
    return !(lhs == rhs);
}

bool operator<(const Date& lhs, const Date& rhs) {
    if(lhs.year() < rhs.year()) {
        return true;
    } else if(lhs.year() == rhs.year()) {
        if(convert::monthToInt(lhs.month()) < convert::monthToInt(rhs.month())) {
            return true;
        } else if(lhs.month() == rhs.month()) {
            if(lhs.day() < rhs.day()) {
                return true;
            }
        }
    }
    return false;
}

bool operator<=(const Date& lhs, const Date& rhs) {
    return (lhs == rhs || lhs < rhs);
}

bool operator>(const Date& lhs, const Date& rhs) {
    return !(lhs <= rhs);
}

bool operator>=(const Date& lhs, const Date& rhs) {
    return !(lhs < rhs);
}


// streaming operators
std::ostream& operator<<(std::ostream& ost, const Date& d) {
    return ost << d.day() << " " << d.month() << ", " << d.year();
}

std::istream& operator>>(std::istream& ist, Date& d) {
    int day, year;
    std::string month;

    ist >> day;
    ist >> month;
    ist >> year;
    if(!day || !year || (*month.rbegin() != ',') || ist.fail()) {
        ist.setstate(std::ios::failbit);
        throw "Invalid date value.";
    }

    d = Date(day, month.substr(0,month.length()-1), year);

    return ist;
}
