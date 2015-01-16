#include <stdio.h>
#include <string.h>

int sudoku(int grid[9][9]) {
  // Test for 9 of each digit 1-9
  int counter = 0;
  for(int i = 1; i <= 9; i++) {
    for(int j = 0; j < 9; j++) {
      for(int k = 0; k < 9; k++) {
	if(grid[j][k] == i) counter += 1;
      }
    }
    if(counter != 9) return 0;
    counter = 0;
  }
  
  int arrayofones[9] = {[0 ... 8] = 1};
  int testarray[9] = {[0 ... 8] = 0};
  
  // Test for 1 to 9 in each row
  for(int i = 0; i < 9; i++) {
    for(int j = 1; j <= 9; j++) {
      for(int k = 0; k < 9; k++) {
	if(grid[i][k] == j && testarray[j-1] == 0) testarray[j-1] = 1;
	else if(grid[i][k] == j && testarray[j-1] == 1) return 0;
      }
    }
    if(memcmp(testarray, arrayofones, 9) != 0) return 0;
    for(int z = 0; z < 9; z++) {
      testarray[z] = 0;
    }
  }
  
  // Test for 1 to 9 in each column
  for(int i = 0; i < 9; i++) {
    for(int j = 1; j <= 9; j++) {
      for(int k = 0; k < 9; k++) {
	if(grid[k][i] == j && testarray[j-1] == 0) testarray[j-1] = 1;
	else if(grid[k][i] == j && testarray[j-1] == 1) return 0;
      }
    }
    if(memcmp(testarray, arrayofones, 9) != 0) return 0;
    for(int z = 0; z < 9; z++) {
      testarray[z] = 0;
    }
  }
  
  // Test for 1 to 9 in each block
  for(int extrarows = 0; extrarows < 7; extrarows += 3) {
    for(int extracolumns = 0; extracolumns < 7; extracolumns += 3) {
      for(int i = 0+extrarows; i < 3+extrarows; i++) {
	for(int j = 0+extracolumns; j < 3+extracolumns; j++) {
	  for(int k = 1; k <= 9; k++) {
	    if(grid[i][j] == k && testarray[k-1] == 0) testarray[k-1] = 1;
	    else if(grid[i][j] == k && testarray[k-1] == 1) return 0;
	  }
	}
      }
      if(memcmp(testarray, arrayofones, 9) != 0) return 0;
      for(int z = 0; z < 9; z++) {
	testarray[z] = 0;
      }
    }
  }
  
  // If all else fails
  return 1;
}