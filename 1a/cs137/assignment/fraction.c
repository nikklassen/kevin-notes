#include <stdio.h>
#include "fraction.h"

struct fraction fractionReduce(struct fraction frac) {
  if(frac.denom == 0) {
    frac.num = 0;
    return frac;
  }
  
  char negative = (frac.num < 0 || frac.denom < 0) && !(frac.num < 0 && frac.denom < 0);
  if(frac.num < 0) frac.num *= -1;
  if(frac.denom < 0) frac.denom *= -1;

  int base = frac.num / frac.denom;
  frac.num %= frac.denom;

  if(!frac.num) {
    frac.num = base;
    frac.denom = 1;
    return frac;
  }

  int a = frac.denom;
  int b = frac.num;
  int r = a % b;
  while(r != 0) {
    a = b;
    b = r;
    r = a % b;
  }
  frac.num /= b;
  frac.denom /= b;
  
  frac.num += base*frac.denom;
  
  if(negative) frac.num *= -1;
  
  return frac;
}

struct fraction fractionCreate(int numerator, int denominator) {
  struct fraction result = {numerator,denominator};
  result = fractionReduce(result);
  
  return result;
}

struct fraction fractionAdd(struct fraction first, struct fraction second) {
  if(!first.denom || !second.denom) {
    struct fraction undef = {0,0};
    return undef;
  }
  
  struct fraction result = {first.num*second.denom + first.denom*second.num, first.denom*second.denom};
  result = fractionReduce(result);
  
  return result;
}

struct fraction fractionSubtract(struct fraction first, struct fraction second) {
  if(!first.denom || !second.denom) {
    struct fraction undef = {0,0};
    return undef;
  }
  
  struct fraction result = {first.num*second.denom - first.denom*second.num, first.denom*second.denom};
  result = fractionReduce(result);
  
  return result;
}

struct fraction fractionMultiply(struct fraction first, struct fraction second) {
  if(!first.denom || !second.denom) {
    struct fraction undef = {0,0};
    return undef;
  }
  
  struct fraction result = {first.num*second.num, first.denom*second.denom};
  result = fractionReduce(result);
  
  return result;
}

struct fraction fractionDivide(struct fraction first, struct fraction second) {
  if(!first.denom || !second.denom || !second.denom) {
    struct fraction undef = {0,0};
    return undef;
  }
  
  struct fraction result = {first.num*second.denom, first.denom*second.num};
  result = fractionReduce(result);
  
  return result;
}

void fractionPrint(struct fraction frac) {
  if(!frac.denom) {
    printf("Divide by zero!\n");
    return;
  }

  char negative = (frac.num < 0 || frac.denom < 0) && !(frac.num < 0 && frac.denom < 0);
  if(frac.num < 0) frac.num *= -1;
  if(frac.denom < 0) frac.denom *= -1;

  int base = frac.num / frac.denom;
  frac.num %= frac.denom;

  if(!frac.num) {
    if(negative) printf("-");
    printf("%d\n", base);
    return;
  }

  int a = frac.denom;
  int b = frac.num;
  int r = a % b;
  while(r != 0) {
    a = b;
    b = r;
    r = a % b;
  }
  frac.num /= b;
  frac.denom /= b;

  if(negative) printf("-");
  if(base > 0) printf("%d ", base);

  printf("%d/%d\n", frac.num, frac.denom);
}/*

int main (void)
{
  struct fraction a, b, c, d, r, bad, asdf;

  a = fractionCreate (30, 6);
  b = fractionCreate (9, 9);
  c = fractionCreate (80, 16);
  d = fractionCreate (45, 15);
  bad = fractionCreate (8, 2);
  asdf = fractionCreate (3, 1);

  fractionPrint(fractionAdd(a,b));
  fractionPrint(fractionSubtract(c,d));
  fractionPrint(fractionMultiply(a,b));
  fractionPrint(fractionDivide(c,d));

  r = fractionAdd(a,bad);
  fractionPrint(r);
  fractionPrint(fractionAdd(b,r));
  
  fractionPrint(asdf);

  return 0;
}*/