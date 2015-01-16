#include <stdio.h>
#include <string.h>

struct card {
    int value; // 1 to 13 inclusive
    char suit; // c, d, h, or s
};

void quickSort(struct card hand[], int n) {
 	int pivotI; // Pivot number
 	char pivotC; // Pivot char
 	int beg[300], end[300]; // Max left-right
 	int L, R; // Current left-right
 	int swap; // Swap number

 	beg[0] = 0;
 	end[0] = n;

 	int i = 0;
	while (i >= 0) {
		L = beg[i];
		R = end[i] - 1;
    	if(L < R) {
    		pivotI = hand[L].value;
    		pivotC = hand[L].suit;
    		while(L < R) {
        		while(hand[R].value >= pivotI && L < R) R--;
        		if(L < R) {
        			hand[L].value = hand[R].value;
        			hand[L++].suit = hand[R].suit;
        		}
        		while(hand[L].value <= pivotI && L < R) L++;
        		if(L < R) {
        			hand[R].value = hand[L].value;
        			hand[R--].suit = hand[L].suit;
        		}
        	}
      		hand[L].value = pivotI;
      		hand[L].suit = pivotC;
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
    }
}

int checkFlush(struct card hand[], int n) {
	int count = 0, temp;
	int k = 300000;
	for(int i = 0; i < n; i++) {
		count++;
		switch(hand[i].suit) {
			case 'c':
				for(int j = i+1; j < n; j++) {
					if(hand[j].suit == 'c' && hand[j].value == hand[i].value + count) {
						count++;
						if(count == 4 && j == n-1) {
							for(int m = 0; m < n; m++) {
								if(hand[m].value == 1 && hand[0].suit == 'c') {
									return 1;
								}
							}
						}
						if(count >= 5) return 1;
						continue;
					}
					else {
						if(count >= 5) return 1;
						for(k = j+1; k < n; k++) {
							if(hand[k].suit == 'c' && hand[k].value == hand[i].value + count) {
								goto saveMeC;
							}
						}
						count = 0;
						break;
					}
					saveMeC:
						if(k != 300000) {
							j = k-1;
							k = 300000;
							continue;
						}
					if(count >= 5) return 1;
				}
				count = 0;
				break;
			case 'd':
				for(int j = i+1; j < n; j++) {
					if(hand[j].suit == 'd' && hand[j].value == hand[i].value + count) {
						count++;
						if(count == 4 && j == n-1) {
							for(int m = 0; m < n; m++) {
								if(hand[m].value == 1 && hand[0].suit == 'd') {
									return 1;
								}
							}
						}
						if(count >= 5) return 1;
						continue;
					}
					else {
						if(count >= 5) return 1;
						for(k = j+1; k < n; k++) {
							if(hand[k].suit == 'd' && hand[k].value == hand[i].value + count) {
								goto saveMeD;
							}
						}
						count = 0;
						break;
					}
					saveMeD:
						if(k != 300000) {
							j = k-1;
							k = 300000;
							continue;
						}
					if(count >= 5) return 1;
				}
				count = 0;
				break;
			case 'h':
				for(int j = i+1; j < n; j++) {
					if(hand[j].suit == 'h' && hand[j].value == hand[i].value + count) {
						count++;
						if(count == 4 && j == n-1) {
							for(int m = 0; m < n; m++) {
								if(hand[m].value == 1 && hand[0].suit == 'h') {
									return 1;
								}
							}
						}
						if(count >= 5) return 1;
						continue;
					}
					else {
						if(count >= 5) return 1;
						for(k = j+1; k < n; k++) {
							if(hand[k].suit == 'h' && hand[k].value == hand[i].value + count) {
								goto saveMeH;
							}
						}
						count = 0;
						break;
					}
					saveMeH:
						if(k != 300000) {
							j = k-1;
							k = 300000;
							continue;
						}
					if(count >= 5) return 1;
				}
				count = 0;
				break;
			case 's':
				for(int j = i+1; j < n; j++) {
					if(hand[j].suit == 's' && hand[j].value == hand[i].value + count) {
						count++;
						if(count == 4 && j == n-1) {
							for(int m = 0; m < n; m++) {
								if(hand[m].value == 1 && hand[0].suit == 's') {
									return 1;
								}
							}
						}
						if(count >= 5) return 1;
						continue;
					}
					else {
						if(count >= 5) return 1;
						for(k = j+1; k < n; k++) {
							if(hand[k].suit == 's' && hand[k].value == hand[i].value + count) {
								temp = count;
								goto saveMeS;
							}
						}
						count = 0;
						break;
					}
					saveMeS:
						if(k != 300000) {
							j = k-1;
							k = 300000;
							count = temp;
							continue;
						}
					if(count >= 5) return 1;
				}
				count = 0;
				break;
		}
	}

	return 0;
}

int straightflush(struct card hand[], int n) { // Return 1 if a straight flush is found. n <= 15000
	quickSort(hand, n);
	return checkFlush(hand, n);
}/*

int main(){
    struct card hand1[] = {{4,'s'}, {9,'s'},{12,'c'},{11,'s'},{8,'s'},
                           {6,'d'}, {3,'d'},{7,'s'},{10,'s'},{12,'d'}};
    struct card hand2[] = {{8,'c'}, {2,'h'},{5,'s'},{6,'c'},{1,'s'},
                           {5,'c'}, {4,'d'},{6,'h'},{13,'d'},{1,'d'}};
    struct card hand3[] = {{8,'c'}, {10,'c'}, {7, 'c'}, {11, 'c'}, {9, 'c'}, {9, 's'}};

    printf ("%d\n", straightflush(hand1, 10));
    printf ("%d\n", straightflush(hand2, 10));
    printf ("%d\n", straightflush(hand3, 6));

    return 0;
}*/