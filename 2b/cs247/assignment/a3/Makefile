CXX_FLAGS=-g -Wall -Wextra


all: hangman
	make -C decorator


hangman: hangman.cpp
	${CXX} $(CXX_FLAGS) $^ -o $@


clean:
	rm -rf *.dSYM
	rm -f *.o
	rm -f hangman
	rm -f gamewords
	make -C decorator clean
