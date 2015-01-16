#include <stdio.h>

int main() {
  int num;
  
  scanf("%d", &num);
  
  if(num == 0) {
    printf("0");
  }
  
  if(num < 0) {
    printf("-");
    num *= -1;
  }
  
  while(num > 0) {
    printf("%d", num%10);
    num /= 10;
  }
  
  printf("\n");
  return 0;
}