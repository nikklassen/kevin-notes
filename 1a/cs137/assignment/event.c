#include <stdio.h>
#include "event.h"


/*
int checkSorted(struct event schedule[], int n) {
	for(int i = 1; i < n; i++) {
		if(schedule[i].start.hour < schedule[i-1].end.hour || (schedule[i].start.hour == schedule[i-1].end.hour && schedule[i].start.minute < schedule[i-1].end.minute)) {
			return 0;
		}
	}

	return 1;
}*/

void sortSchedule(struct event schedule[], int n) {
	int j, increment, tempH, tempM, tempHE, tempME;

	increment = 3;
	while(increment > 0) {
		for(int i = 0; i < n; i++) {
			j = i;
			tempH = schedule[i].start.hour;
			tempM = schedule[i].start.minute;
			tempHE = schedule[i].end.hour;
			tempME = schedule[i].end.minute;
			while(j >= increment && (schedule[j-increment].start.hour > tempH || (schedule[j-increment].start.minute > tempM && schedule[j-increment].start.hour == tempH))) {
				schedule[j].start.hour = schedule[j - increment].start.hour;
				schedule[j].start.minute = schedule[j - increment].start.minute;
				schedule[j].end.hour = schedule[j - increment].end.hour;
				schedule[j].end.minute = schedule[j - increment].end.minute;
				j -= increment;
			}
			schedule[j].start.hour = tempH;
			schedule[j].start.minute = tempM;
			schedule[j].end.hour = tempHE;
			schedule[j].end.minute = tempME;
		}
		if(increment/2 != 0) increment /= 2;
		else if(increment == 1) increment = 0;
		else increment = 1;
	}

	return;
}/*


void quickSort (struct event schedule[], int n) {
	for(int z = 0; z < 100; z++) {
		if(checkSorted(schedule,n) == 1) return;

	 	int pivot1, pivot2, pivot3, pivot4; // Pivot numbers
	 	int beg[10], end[10]; // Max left-right
	 	int L, R; // Current left-right
	 	int swap; // Swap number

	 	beg[0] = 0;
	 	end[0] = n;

	 	int i = 0;
		while (i >= 0) {
			L = beg[i];
			R = end[i] - 1;
	    	if(L < R) {
	    		pivot1 = schedule[L].start.hour;
	    		pivot2 = schedule[L].start.minute;
	    		pivot3 = schedule[L].end.hour;
	    		pivot4 = schedule[L].end.minute;
	    		while(L < R) {
	        		while(schedule[R].start.hour >= pivot1 && schedule[R].start.minute >= pivot2 && L < R) R--;
	        		if(L < R) {
	        			schedule[L].start.hour = schedule[R].start.hour;
	        			schedule[L].start.minute = schedule[R].start.minute;
	        			schedule[L].end.hour = schedule[R].end.hour;
	        			schedule[L++].end.minute = schedule[R].end.minute;
	        		}
	        		while(schedule[L].start.hour <= pivot1 && schedule[R].start.minute <= pivot2 && L < R) L++;
	        		if(L < R) {
	        			schedule[R].start.hour = schedule[L].start.hour;
	        			schedule[R].start.minute = schedule[L].start.minute;
	        			schedule[R].end.hour = schedule[L].end.hour;
	        			schedule[R--].end.minute = schedule[L].end.minute;
	        		}
	        	}
	      		schedule[L].start.hour = pivot1;
	      		schedule[L].start.minute = pivot2;
	      		schedule[L].end.hour = pivot3;
	      		schedule[L].end.minute = pivot4;
	      		beg[i+1] = L + 1;
	      		end[i+1] = end[i];
	      		end[i++] = L;
	    		if(end[i] - beg[i] > end[i-1] - beg[i-1]) {
	        		swap = beg[i];
	        		beg[i] = beg[i-1];
	        		beg[i-1] = swap;
	        		swap = end[i];
	        		end[i] = end[i-1];
	        		end[i-1] = swap;
	        	}
	        }
	    	else {
	    		i--;
	    	}
	    	if(checkSorted(schedule,n) == 1) return;
	    }
	}

	return;
}*/

int available (struct event schedule[], int n, struct event e) {
	sortSchedule(schedule, n);

	for(int i = 0; i < n; i++) {
		if(e.start.hour > schedule[i].end.hour || (e.start.hour == schedule[i].end.hour && e.start.minute >= schedule[i].end.minute)) {
			if(e.end.hour < schedule[i+1].start.hour || (e.end.hour == schedule[i+1].start.hour && e.end.minute <= schedule[i+1].start.minute)) {
				return 1;
			}
		}
	}

	return 0;
}/*

int main (void)
{
  struct event schedule[] = {
   {{15,0},{16,30}},
   {{9,0},{9,15}},
   {{13,0},{14,20}},
   {{17,15},{18,0}},
   {{9,45},{9,55}},
  };

  int i;
  struct event event0 = {{10,0},{10,30}};
  struct event event1 = {{14,20},{15,0}};
  struct event event2 = {{17,0},{17,30}};
  struct event event3 = {{15,30},{16,0}};

  printf ("%d\n", available (schedule, sizeof(schedule)/sizeof(schedule[0]), event0));
  printf ("%d\n", available (schedule, sizeof(schedule)/sizeof(schedule[0]), event1));
  printf ("%d\n", available (schedule, sizeof(schedule)/sizeof(schedule[0]), event2));
  printf ("%d\n", available (schedule, sizeof(schedule)/sizeof(schedule[0]), event3));

  shellSort (schedule, sizeof(schedule)/sizeof(schedule[0]));

  for (i = 0; i < sizeof(schedule)/sizeof(schedule[0]); i++)
    printf ("%02d:%02d - %02d:%02d\n", schedule[i].start.hour, 
    schedule[i].start.minute, schedule[i].end.hour, schedule[i].end.minute);

  printf("---\n");

  struct event schedule1[] = {
   {{0,0},{0,1}},
   {{1,0},{23,59}},
   {{0,1},{1,0}},
  };

  shellSort (schedule1, sizeof(schedule1)/sizeof(schedule1[0]));

  for (i = 0; i < sizeof(schedule1)/sizeof(schedule1[0]); i++)
    printf ("%02d:%02d - %02d:%02d\n", schedule1[i].start.hour, 
    schedule1[i].start.minute, schedule1[i].end.hour, schedule1[i].end.minute);

  printf("---\n");

  struct event schedule2[] = {
   {{1,0},{23,59}},
   {{0,1},{1,0}},
   {{0,0},{0,1}},
  };

  shellSort (schedule2, sizeof(schedule2)/sizeof(schedule2[0]));

  for (i = 0; i < sizeof(schedule2)/sizeof(schedule2[0]); i++)
    printf ("%02d:%02d - %02d:%02d\n", schedule2[i].start.hour, 
    schedule2[i].start.minute, schedule2[i].end.hour, schedule2[i].end.minute);

  return 0;
}*/