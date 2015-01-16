#include <iostream>

#include "Border.h"


Border::Border(Widget* w) : Decorator(w) { }

void Border::print() {
    Decorator::print();
    std::cout << "  I have a border!" << std::endl;
}
