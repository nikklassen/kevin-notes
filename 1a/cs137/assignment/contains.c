#include <stdio.h>
#include <string.h>

int contains(char *s, char *t) {
  if(s == (char)0 || s == NULL || s == '\0' || strcmp(s, "") == 0 || t == (char)0 || t == NULL || t == '\0' || strcmp(t, "") == 0) return 0;

  int count = 0;

  for(int i = 0; s[i] != '\0'; i++) {
    for(int j = 0; t[j] != '\0'; j++) {
      if(s[i+j] != t[j]) break;
      else if(t[j+1] == '\0') {
	count++;
	break;
      }
    }
  }

 return count;
}/*

int main(void) {
  printf ("%d\n", contains ("I wanna shoot something!", "thing"));
  printf ("%d\n", contains ("Let's get in range!", "ge"));
  printf ("%d\n", contains ("Wanna see the fireworks?", "wanna"));
  printf ("%d\n", contains ("Look at the pretty explosions!", " "));
  printf ("%d\n", contains ("Kaboom!", ""));

  printf("\n");

  printf ("%d\n", contains ((char *) 0, "aaa"));
  printf ("%d\n", contains ("aaa", (char *) 0));
  printf ("%d\n", contains ((char *) 0, (char *) 0));

  printf("\n");

  printf ("%d\n", contains ("aaa", "aaa"));
  printf ("%d\n", contains ("hihihi", "hi"));
  printf ("%d\n", contains ("", ""));
  printf ("%d\n", contains ((char *) 0, (char *) 0));

  return 0;
}*/
