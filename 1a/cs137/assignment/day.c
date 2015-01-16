#include <stdio.h>

int isLeapYear(int year) {
  if(year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) return 1;
  return 0;
}

int dayOfYear(int day, int month, int year) {
  // Error checking
  if(year < 1583) return -1;
  if(month > 12 || month <= 0) return -1;
  if(((day > 28 && month == 2) && isLeapYear(year) == 0) || ((day > 29 && month == 2) && isLeapYear(year) == 1)) return -1;
  if(day > 31 && (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)) return -1;
  if(day > 30 && (month == 4 || month == 6 || month == 9 || month == 11)) return -1;
  if(day <= 0) return -1;
  
  // Days in each month, 0-index for leap year february
  int monthtoday[13] = {29,31,28,31,30,31,30,31,31,30,31,30,31};
  
  int total = 0;
  // Add days within each month to total
  for(int i = 1; i < month; i++) {
    if(i != 2) total += monthtoday[i];
    else if(isLeapYear(year)) total += monthtoday[0];
    else total += monthtoday[2];
  }
  
  // Add days with days to total
  total += day;
  
  return total;
}/*



void testDayOfYear(int day, int month, int year)
{
  printf ("%d/%d/%d => %d\n", day, month, year, dayOfYear(day, month, year));
}

int main (void)
{
  testDayOfYear (1, 1, 2008);
  testDayOfYear (29, 2, 2008);
  testDayOfYear (29, 2, 2009);
  testDayOfYear (26, 9, 2008);
  testDayOfYear (31, 12, 2008);
  testDayOfYear (31, 12, 2009);
  testDayOfYear (100, 1, 2008);
  testDayOfYear (1, 100, 2008);
  testDayOfYear (1, 1, 100);
}*/