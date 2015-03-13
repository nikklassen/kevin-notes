CC_FLAGS=-O0 -g


all: buggy fixed


buggy: sll_buggy.c
	${CC} $(CC_FLAGS) $^ -o $@

fixed: sll_fixed.c
	${CC} $(CC_FLAGS) $^ -o $@


clean:
	rm -f buggy
	rm -f fixed
