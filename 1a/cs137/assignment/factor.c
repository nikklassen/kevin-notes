#include <stdio.h>
#include <math.h>

void factor(int number) {
  printf("%d = ", number);
  int storenumber = number;
  int tempnumber = sqrt(number)*2;
  for(int i = 2; i <= tempnumber;) {
    if(number % i == 0) {
      printf("%d%s", i, number != i ? "*" : "");
      number /= i;
    }
    else if(number == i) {
      printf("%d", i);
    }
    else i++;
  }
  if(number == storenumber) printf("%d", number);
  printf("\n");
}/*

int main (void)
{
  factor (100);
  factor (21);
  factor (11381621);
  factor (1505774987);
  return 0;
}*/