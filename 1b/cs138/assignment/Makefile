CXX_FLAGS=-g -Wall


.SUFFIXES:
.SUFFIXES: .o .cc

.cc.o:
	${CXX} $(CXX_FLAGS) -c $<


all: a1 a2 a3 a4.o a5 a6.o


a1: a1p1.o a1p2.o a1p3.o a1p4.o a1p5.o

a2: a2p2.o

a3: a3p1.o a3p2.o

a5: a5p1.o a5p2.o


clean:
	rm -rf *.dSYM
	rm -f *.o
