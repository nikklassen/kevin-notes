#include <iostream>
#include <string>

#include "TextArea.h"


TextArea::TextArea(int h, int w, std::string c) : height(h), width(w), contents(c) { }

void TextArea::print() {
	std::cout << "TextArea with dimensions " << height << "x" << width << " says '" << contents << "'." << std::endl;
}
