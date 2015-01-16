#define DEBUG
#include "DynListTestHarness.cpp"
#include "gtest/gtest.h"


TEST(DynListTest, SimpleOperations) {
	DynList* dlist = new DynList();
	EXPECT_EQ(0, dlist->size());

	(*dlist)[0] = "zeroeth";
	EXPECT_EQ(1, dlist->size());

	(*dlist)[10] = "tenth";
	EXPECT_EQ(11, dlist->size());

	(*dlist)[5] = "fifth";
	EXPECT_EQ(11, dlist->size());
}

TEST(DynListTest, Copy) {
	DynList* dlist = new DynList();
	DynList* elist = new DynList(*dlist);
	EXPECT_EQ(dlist->size(), elist->size()) << "Failed to copy size from 0 items";

	(*dlist)[0] = "first";
	DynList* flist = new DynList(*dlist);
	EXPECT_EQ(dlist->size(), flist->size()) << "Failed to copy size from 1 item";
	EXPECT_EQ((*dlist)[0], (*flist)[0]) << "Failed to copy values from 1 item";

	(*dlist)[2] = "third";
	DynList* glist = new DynList(*dlist);
	EXPECT_EQ(dlist->size(), glist->size()) << "Failed to copy size from 2 items";
	EXPECT_EQ((*dlist)[0], (*glist)[0]) << "Failed to copy values from 2 items";
	EXPECT_EQ((*dlist)[2], (*glist)[2]) << "Failed to copy values from 2 items";
}

TEST(DynListTest, Equality) {
	DynList* dlist = new DynList();
	DynList* elist = new DynList();
	EXPECT_TRUE(*dlist == *elist) << "Empty DynLists are not equal";

	(*dlist)[0] = "first";
	EXPECT_FALSE(*dlist == *elist) << "DynList with zero items is equal to DynList with one item";
	(*dlist)[2] = "third";
	EXPECT_FALSE(*dlist == *elist) << "DynList with zero items is equal to DynList with multiple items";

	(*elist)[0] = "first";
	EXPECT_FALSE(*dlist == *elist) << "DynList with one item is equal to DynList with multiple items";
	DynList* flist = new DynList();
	(*flist)[0] = "first";
	EXPECT_TRUE(*elist == *flist) << "DynLists with 1 item are not equal";

	(*elist)[2] = "third";
	EXPECT_TRUE(*dlist == *elist) << "DynLists with multiple items are not equal";
}
