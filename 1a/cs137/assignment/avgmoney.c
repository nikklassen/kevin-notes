#include <stdio.h>

void printAverageAmount(double money[], int n) {
  double average = 0.00;
  for(int i = 0; i < n; i++) {
    money[i] *= 1000000.0;
    average += money[i];
  }
  average /= n;
  
  int dollars = average/1000000.0;
  int cents = (average-(dollars*1000000.0))/10000.0;
  
  printf("Everyone gets %d dollar(s) and %d cent(s).\n", dollars, cents);
}/*

int main (void) {
  double a[5] = {100.00, 250.00, 320.00, 120.00, 1500.00};
  double b[3] = {8.00, 1.00, 1.00};
  double c[3] = {8.00, 1.50, 1.50};
  
  printAverageAmount(a, 5);
  printAverageAmount(b, 3);
  printAverageAmount(c, 3);
  
  return 0;
}*/