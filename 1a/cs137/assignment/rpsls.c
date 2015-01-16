#include <stdio.h>
#include <string.h>

int rpsls(const char *player1, const char *player2) {
    if(player1 == NULL || player2 == NULL) return 0;

    if(strcmp(player1,"rock") == 0 || strcmp(player1,"paper") == 0 || strcmp(player1,"scissors") == 0 || strcmp(player1,"lizard") == 0 || strcmp(player1,"Spock") == 0) {
        if(strcmp(player2,"rock") == 0 || strcmp(player2,"paper") == 0 || strcmp(player2,"scissors") == 0 || strcmp(player2,"lizard") == 0 || strcmp(player2,"Spock") == 0) {
            switch(player1[0]) {
                case 'r':
                    switch(player2[0]) {
                        case 'r':
                            return 0;
                        case 'p':
                            return -1;
                        case 's':
                            return 1;
                        case 'l':
                            return 1;
                        case 'S':
                            return -1;
                    }
                case 'p':
                    switch(player2[0]) {
                        case 'r':
                            return 1;
                        case 'p':
                            return 0;
                        case 's':
                            return -1;
                        case 'l':
                            return -1;
                        case 'S':
                            return 1;
                    }
                case 's':
                    switch(player2[0]) {
                        case 'r':
                            return -1;
                        case 'p':
                            return 1;
                        case 's':
                            return 0;
                        case 'l':
                            return 1;
                        case 'S':
                            return -1;
                    }
                case 'l':
                    switch(player2[0]) {
                        case 'r':
                            return -1;
                        case 'p':
                            return 1;
                        case 's':
                            return -1;
                        case 'l':
                            return 0;
                        case 'S':
                            return 1;
                    }
                case 'S':
                    switch(player2[0]) {
                        case 'r':
                            return 1;
                        case 'p':
                            return -1;
                        case 's':
                            return 1;
                        case 'l':
                            return -1;
                        case 'S':
                            return 0;
                }
            }
        }
    }
    return 0;
}/*

int main (void) {
    printf ("%d\n", rpsls("rock","paper"));
    printf ("%d\n", rpsls("rock","rock"));
    printf ("%d\n", rpsls("paper","rock"));
    printf ("%d\n", rpsls("lizard",(char*)0));
    printf ("\n");
    printf ("%d\n", rpsls("paper","rock"));
    printf ("\n");
    printf ("%d\n", rpsls("rock","paper"));
    printf ("%d\n", rpsls("rock","scissors"));
    printf ("%d\n", rpsls("rock","rock"));
    printf ("%d\n", rpsls("rock","lizard"));
    printf ("%d\n", rpsls("rock","Spock"));

    return 0;
}*/
