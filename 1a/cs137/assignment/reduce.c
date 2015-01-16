#include <stdio.h>

int main(void){
  int num,denom;
  scanf("%d\n%d", &num, &denom);

  if(!denom) {
    printf("Divide by zero!\n");
    return 0;
  }

  char negative = (num < 0 || denom < 0) && !(num < 0 && denom < 0);
  if(num < 0) num *= -1;
  if(denom < 0) denom *= -1;

  int base = num / denom;
  num %= denom;

  if(!num) {
    if(negative) printf("-");
    printf("%d", base);
    return 0;
  }

  int a = denom;
  int b = num;
  int r = a % b;
  while(r != 0) {
    a = b;
    b = r;
    r = a % b;
  }
  num /= b;
  denom /= b;

  if(negative) printf("-");
  if(base > 0) printf("%d ", base);

  printf("%d/%d\n", num, denom);
  return 0;
}