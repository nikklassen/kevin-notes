struct fraction {
  int num;
  int denom;
};

struct fraction fractionCreate(int a, int b);
struct fraction fractionAdd(struct fraction a, struct fraction b);
struct fraction fractionSubtract(struct fraction a, struct fraction b);
struct fraction fractionMultiply(struct fraction a, struct fraction b);
struct fraction fractionDivide(struct fraction a, struct fraction b);
void fractionPrint(struct fraction a);