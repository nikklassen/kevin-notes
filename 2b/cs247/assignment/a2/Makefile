CXX_FLAGS=-g -Wall -Wextra

GTEST_DIR=~/coding/gtest
GTEST_FLAGS=-isystem $(GTEST_DIR)/include -pthread
GTEST_LIBS=$(GTEST_DIR)/lib/*


.SUFFIXES:
.SUFFIXES: .o .cpp

.cpp.o:
	${CXX} $(CXX_FLAGS) -c $<


all: a2q1 a2q2

test: a2q1_test a2q2_test


a2q1: DateTestHarness.cpp Date.o
	${CXX} $(CXX_FLAGS) $^ -o $@

a2q1_test: a2q1_test.cpp Date.o
	${CXX} $(GTEST_FLAGS) $(CXX_FLAGS) $^ $(GTEST_LIBS) -o $@;
	./$@

a2q2: TestHarness.cpp Password.o UserAccount.o Userid.o
	${CXX} $(CXX_FLAGS) $^ -o $@

a2q2_test: a2q2_test.cpp Password.o UserAccount.o Userid.o
	${CXX} $(GTEST_FLAGS) $(CXX_FLAGS) $^ $(GTEST_LIBS) -o $@;
	./$@


clean:
	rm -rf *.dSYM
	rm -f *.o
	rm -f a2q1 a2q1_test
	rm -f a2q2 a2q2_test
