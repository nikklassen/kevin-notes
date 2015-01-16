#ifndef DECORATOR_H
#define DECORATOR_H

#include "Widget.h"


class Decorator : public Widget {
    Widget* widget;
public:
    Decorator(Widget*);
 
    void print();
};

#endif
