#ifndef TEXTAREA_H
#define TEXTAREA_H

#include <string>

#include "Widget.h"


class TextArea : public Widget {
    int height;
    int width;

    std::string contents;
public:
    TextArea(int, int, std::string);
 
    void print();
};

#endif
