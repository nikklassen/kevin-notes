#include <stdio.h>
#include <math.h>

int isPrime(int number) {
  int squareroot = sqrt(number);
  if(number <= 1) return 0;
  for(int i = 2; i <= squareroot; i++) {
    if(number % i == 0) return 0;
  }
  return 1;
}

int isTwin(int number) {
  if(isPrime(number) == 0) return 0;

  int numberplus = number + 2;
  int numberminus = number - 2;
  /*
  if(isPrime(numberplus) == 1) printf("plus is yes\n");
  if(isPrime(numberminus) == 1) printf("minus is yes\n");
  */
  if(isPrime(numberplus) == 1 || isPrime(numberminus) == 1) return 1;
  if(isPrime(numberplus) == 0 && isPrime(numberminus) == 0) return 0;
  return 0;
}/*

void testTwin(int number) { printf("%d is%s a twin prime\n", number, isTwin(number) ? "" : " not"); }

int main(void) {
  testTwin (7);
  testTwin (13);
  testTwin (823);
  testTwin (2);
  testTwin (23);
  testTwin (100);
  testTwin (-3);
}*/
