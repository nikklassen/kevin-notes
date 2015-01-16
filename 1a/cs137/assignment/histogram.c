#include <stdio.h>
#include <stdlib.h>

int *histogram (const int *a, int n, int *m) {
  *m = 0;
  
  for(int i = 0; i < n; i++) {
    if(a[i] > *m) *m = a[i];
  }
  
  if(*m == 0) return NULL;
  *m += 1;
  
  int *histo = malloc(sizeof(int) * *m);
  for(int k = 0; k < *m; k++) {
    histo[k] = 0;
  }
  
  for(int j = 0; j < n; j++) {
    if(a[j] >= 0) {
      histo[a[j]] += 1;
    }
  }
  
  return histo;
}/*

int main (void) {
    int a[] = {1, 2, 3, 3, 3, 2, 1, 4, 5, 6, 0, -100};
    int *h, m, i;

    h = histogram (a, sizeof(a)/sizeof(a[0]), &m);

    if (h)
    {
        for (i = 0; i < m; i++)
            printf ("%d\n", h[i]);
        free (h);
    }

    return 0;
}*/