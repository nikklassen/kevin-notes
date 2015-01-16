CXX_FLAGS=-g -Wall -Wextra


.SUFFIXES:
.SUFFIXES: .o .cpp

.cpp.o:
	${CXX} $(CXX_FLAGS) -c $<


all: exec


Border.o: Border.cpp Border.h Decorator.h
Decorator.o: Decorator.cpp Decorator.h Widget.h
Menu.o: Menu.cpp Menu.h Decorator.h
TextArea.o: TextArea.cpp TextArea.h Widget.h


exec: main.cpp Border.o Decorator.o Menu.o TextArea.o
	${CXX} $(CXX_FLAGS) $^ -o $@


clean:
	rm -rf *.dSYM
	rm -f *.o
	rm -f exec
