#include <stdio.h>
#include <string.h>

int max(int array[], int n) {
  if(n == 0) return 0;
  int temp = array[0];
  for(int i = 1; i < n; i++) {
    if(array[i] > temp) temp = array[i];
  }
  return temp;
}

int countValue(int array[], int n, int value) {
  int temp = 0;
  for(int i = 0; i < n; i++) {
    if(array[i] == value) temp += 1;
  }
  return temp;
}

void absolute(int array[], int n) {
  for(int i = 0; i < n; i++) {
    if(array[i] < 0) array[i] *= -1;
  }
}

int isSorted(int array[], int n) {
  if(n == 0) return 1;
  int temp = 1;
  for(int i = 1; i < n; i++) {
    if(array[i] < array[i-1]) temp = 0;
  }
  return temp;
}

int isPermutation(int array[], int n) {
  if(n == 0) return 1;
  int testarray[n+1];
  memset(testarray, 0, (n+1)*sizeof(int));
  int temp = 0;
  for(int i = 1; i <= n; i++) {
    for(int j = 0; j < n; j++) {
      if(array[j] == i && testarray[i] == 0) {
	temp += 1;
        testarray[i] = 1;
      }
    }
  }
  if(temp == n) return 1;
  return 0;
}