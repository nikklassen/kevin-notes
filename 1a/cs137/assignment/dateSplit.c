#include <stdio.h>

int isLeapYear(int year) {
    if(year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) return 1;
    return 0;
}

int dateSplit(int dayOfYear, int year, int *day, int *month) {
    if(year < 1583) return 0;
    if(dayOfYear <= 0) return 0;
    if(isLeapYear(year) && dayOfYear > 366) return 0;
    if(!isLeapYear(year) && dayOfYear > 365) return 0;

    int monthtoday[13] = {29,31,28,31,30,31,30,31,31,30,31,30,31};
    *month = 1;
    int flag = 0;

    for(int i = 1; i < 13; i++) {
        if(i == 1 && flag == 1) {
            i = 3;
        }
        if(i == 2 && isLeapYear(year) == 1) {
            i = 0;
            flag = 1;
        }
        if(dayOfYear > monthtoday[i]) {
            *month += 1;
            dayOfYear -= monthtoday[i];
        }
        else {
            *day = dayOfYear;
            return 1;
        }
    }
    return 0;
}/*

void testDateSplit(int dayOfYear, int year) {
    int day, month;

    if(dateSplit(dayOfYear, year, &day, &month)) printf("%d,%d => %d,%d\n", dayOfYear, year, day, month);
    else printf("%d,%d => invalid\n", dayOfYear, year);
}

int main (void)
{
    testDateSplit (100, 2007);
    testDateSplit (100, 2008);
    testDateSplit (1, 2007);
    testDateSplit (1, 2008);
    testDateSplit (365, 2007);
    testDateSplit (366, 2007);
    testDateSplit (366, 2008);
    testDateSplit (-1, -1);
}*/
