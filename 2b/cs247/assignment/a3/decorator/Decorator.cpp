#include "Decorator.h"


Decorator::Decorator(Widget* w) : widget(w) { }

void Decorator::print() {
    widget->print();
}
