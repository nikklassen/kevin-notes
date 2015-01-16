#include <math.h>
#include <stdio.h>
#include <stdlib.h>

#include "poly.h"


struct poly *polyCreate() { // Creates empty poly
    struct poly *p = malloc(sizeof(struct poly));
    p->a = malloc(sizeof(double));
    p->size = 1;
    p->length = 0;
    return p;
}

struct poly *polyDelete(struct poly *p) { // Frees pointer p and subpointer a, returns NULL pointer.
    free(p->a);
    free(p);
    
    return (struct poly *) 0;
}

struct poly *polySetCoefficient(struct poly *p, int i, double value) { // Sets p->a[i] = value. Manages p->size and p->length. Assumes i >= 0
    if(i >= p->size) {
        while(i >= p->size) {
            p->size *= 2;
        }
        p->a = realloc((void *)p->a, p->size*sizeof(double));
    }
    
    while(i >= p->length) {
        p->a[p->length] = 0;
        p->length++;
    }
    
    p->a[i] = value;
    
    return p;
}

double polyGetCoefficient(struct poly *p, int i) { // Assumes i >= 0
    if(!p || i > p->length) return 0;
    return p->a[i];
}

int polyDegree(struct poly *p) { // Returns degree = length - 1
    if(!p || p->length == 0) return 0;
    return p->length - 1;
}

void polyPrint(struct poly *p) { // Prints the polynomial
    double temp = 0;
    for(int j = 0; j < p->length; j++) {
        if(polyGetCoefficient(p,j) != 0) temp++;
    }
    if(temp == 0 || !p) { // If the polynomial contains only zeros, just print 0 and quit
        printf("0\n");
        return;
    }
    
    int j;
    for(j = p->length-1; j >= 0; j--) { // Finds the first term
        if(polyGetCoefficient(p,j) != 0) break;
    }
    
    if(p->a[j] < 0.0) printf("-"); // Prints "-" if the first term is negative
    for(int i = j; i >= 0; i--) { // Iterates from the last term of the array, in decreasing exponential order
        if(fabs(polyGetCoefficient(p,i)) != 1 || (fabs(polyGetCoefficient(p,i)) == 1 && i == 0)) printf("%g", fabs(polyGetCoefficient(p,i))); // Prints non-1 coefficients
        if(i > 0) printf("x"); // Prints "x" if it should
        if(i > 1) printf("^%d", i); // Prints the exponent of x, if there is one
        for(int k = 1; k <= i; k++) { // Finds the next term
            if(polyGetCoefficient(p,i-k) != 0) {
                if(polyGetCoefficient(p,i-k) > 0) printf(" + "); // Prints "+" or "-"
                if(polyGetCoefficient(p,i-k) < 0) printf(" - ");
                i -= (k-1); // Goes to the next term
                break;
            }
        }
    }
    printf("\n");
}

struct poly *polyCopy(struct poly *p) { // Creates a new poly z with same coefficients as p
    if(!p) return (struct poly *) 0;
    struct poly *z = polyCreate();
    
    int i = p->length;
    while(i--) {
        polySetCoefficient(z,i,polyGetCoefficient(p,i));
    }
    
    return z;
}

struct poly *polyAdd(struct poly *p0, struct poly *p1) { // Creates z as a copy of p0, simple adds p1 within size and length of p0, else uses polySetCoefficient
    if(!p0) return p1;
    if(!p1) return p0;
    struct poly *z = polyCopy(p0);
    
    for(int i = 0; i < z->length; i++) {
        z->a[i] += polyGetCoefficient(p1,i);
    }
    for(int j = z->length; j < p1->length; j++) {
        polySetCoefficient(z,j,polyGetCoefficient(p1,j));
    }
    
    return z;
}

struct poly *polyMultiply(struct poly *p0, struct poly *p1) { // Returns a new polynomial with value p0 * p1
    if(!p0 || !p1) return (struct poly *) 0;
    
    struct poly *z = polyCreate();
    
    for(int i = p0->length-1; i >= 0; i--) {
        for(int j = p1->length-1; j >= 0; j--) {
            if(polyGetCoefficient(z,i+j) == 0) polySetCoefficient(z,i+j,polyGetCoefficient(p0,i)*polyGetCoefficient(p1,j));
            else z->a[i+j] += polyGetCoefficient(p0,i)*polyGetCoefficient(p1,j);
        }
    }
    
    return z;
}

struct poly *polyPrime(struct poly *p) { // Returns a new poly which is the derivative of p
    if(!p) return (struct poly *) 0;
    struct poly *z = polyCreate();
    
    for(int i = 1; i < p->length; i++) {
        polySetCoefficient(z,i-1,polyGetCoefficient(p,i)*i);
    }
    
    return z;
}

double polyEval(struct poly *p, double x) { // Evaluates the polynomial by substituting x
    if(!p) return 0;
    double answer = p->a[0];
    for(int i = 1; i < p->length; i++) {
        answer += pow(x,i) * polyGetCoefficient(p,i);
    }
    
    return answer;
}