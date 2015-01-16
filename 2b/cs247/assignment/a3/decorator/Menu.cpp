#include <iostream>

#include "Menu.h"


Menu::Menu(Widget* w) : Decorator(w) { }

void Menu::print() {
    Decorator::print();
    std::cout << "  I have a menu!" << std::endl;
}
