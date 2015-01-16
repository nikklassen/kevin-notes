#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char *alphabetic (const char *s) {
  if(s == (char)0 || s == NULL || s == '\0' || strcmp(s, "") == 0) return NULL;
  int size = 0, i, j;

  i = 0;
  while(s[i] != '\0') {
    if((s[i] >= 'A' && s[i] <= 'Z') || (s[i] >= 'a' && s[i] <= 'z')) size++;
    i++;
  }

  char *alpha = malloc(sizeof(char) * size);

  i = 0;
  j = 0;
  while(s[i] != '\0') {
    if((s[i] >= 'A' && s[i] <= 'Z') || (s[i] >= 'a' && s[i] <= 'z')) {
      alpha[j] = s[i];
      j++;
    }
    i++;
  }

  return alpha;
}/*

int main (void)
{
  char *a, *b, *c, *d;
  a = alphabetic ("\0");
  b = alphabetic ("--A-sc-nhwefEQWFrhrg");
  c = alphabetic ("*a*b*c*");

  printf ("%s\n", a);
  printf ("%s\n", b);
  printf ("%s\n", c);

  free(a);
  free(b);
  free(c);
  free(d);

  return 0;
}*/
