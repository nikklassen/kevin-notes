#ifndef BORDER_H
#define BORDER_H

#include "Decorator.h"


class Border : public Decorator {
public:
    Border(Widget*);
 
    void print();
};

#endif
