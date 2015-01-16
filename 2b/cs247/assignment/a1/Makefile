CXX_FLAGS=-g -Wall -Wextra

GTEST_DIR=~/coding/gtest
GTEST_FLAGS=-isystem $(GTEST_DIR)/include -pthread
GTEST_LIBS=$(GTEST_DIR)/lib/*


all: a1q1 a1q2


a1q1: AccountTestHarness.cpp
	${CXX} $(CXX_FLAGS) $^ -o $@

a1q2: DynListTestHarness.cpp
	${CXX} $(CXX_FLAGS) $^ -o $@

a1q2_test: a1q2_test.cpp
	${CXX} $(GTEST_FLAGS) $(CXX_FLAGS) $^ $(GTEST_LIBS) -o $@;
	./$@


clean:
	rm -rf *.dSYM
	rm -f *.o
	rm -f a1q1
	rm -f a1q2 a1q2_test
