#include "Border.h"
#include "Menu.h"
#include "TextArea.h"
#include "Widget.h"


int main() {
	// Let's create a widget!
	Widget* widget = new TextArea(100, 40, "Hello!");
	widget->print();

	// That was boring... let's give it a border. No: two borders!
	widget = new Border(new Border(widget));
	widget->print();

	// I think it needs a menu, too!
	widget = new Menu(widget);
	widget->print();

	// And... even more borders!
	widget = new Border(new Border(widget));
	widget->print();
}
