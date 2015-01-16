#ifndef MENU_H
#define MENU_H

#include "Decorator.h"


class Menu : public Decorator {
public:
    Menu(Widget*);
 
    void print();
};

#endif
