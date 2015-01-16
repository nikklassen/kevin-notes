#define DEBUG

#include "gtest/gtest.h"
#include "Date.h"


#define EXPECT_THROW_MSG(MSG, BLOCK) try BLOCK catch(const char* err) { EXPECT_EQ(std::string(MSG), err); }


TEST(DateTest, CreateValidDate) {
    Date lowest(1, "January", 1900);
    Date highest(31, "December", 2100);

    Date leapYear(29, "February", 2000);
}

TEST(DateTest, InvalidYear) {
    EXPECT_THROW_MSG("Invalid year.", {Date d(31, "December", 1899);});
    EXPECT_THROW_MSG("Invalid year.", {Date d(1, "January", 2101);});
}

TEST(DateTest, InvalidMonth) {
    EXPECT_THROW_MSG("Invalid month.", {Date d(1, "january", 2000);});
}

TEST(DateTest, InvalidDay) {
    EXPECT_THROW_MSG("Invalid day of the month.", {Date d(0, "January", 2000);});
    EXPECT_THROW_MSG("Invalid day of the month.", {Date d(32, "January", 2000);});
    EXPECT_THROW_MSG("Invalid day of the month.", {Date d(29, "February", 2001);});
}

TEST(DateTest, Accessors) {
    Date d(1, "January", 2000);

    EXPECT_EQ(1, d.day());
    EXPECT_EQ("January", d.month());
    EXPECT_EQ(2000, d.year());
}

TEST(DateTest, Comparison) {
    Date d(10, "February", 2000);
    Date same(10, "February", 2000);

    Date upDay(11, "February", 2000);
    Date upMonth(10, "March", 2000);
    Date upYear(10, "February", 2001);

    Date downDay(9, "February", 2000);
    Date downMonth(10, "January", 2000);
    Date downYear(10, "February", 1999);

    EXPECT_TRUE(d == d);
    EXPECT_TRUE(d == same);
    EXPECT_FALSE(d == upDay);
    EXPECT_FALSE(d == upMonth);
    EXPECT_FALSE(d == upYear);
    EXPECT_FALSE(d == downDay);
    EXPECT_FALSE(d == downMonth);
    EXPECT_FALSE(d == downYear);

    EXPECT_FALSE(d != d);
    EXPECT_FALSE(d != same);
    EXPECT_TRUE(d != upDay);
    EXPECT_TRUE(d != upMonth);
    EXPECT_TRUE(d != upYear);
    EXPECT_TRUE(d != downDay);
    EXPECT_TRUE(d != downMonth);
    EXPECT_TRUE(d != downYear);

    EXPECT_FALSE(d < d);
    EXPECT_FALSE(d < same);
    EXPECT_TRUE(d < upDay);
    EXPECT_TRUE(d < upMonth);
    EXPECT_TRUE(d < upYear);
    EXPECT_FALSE(d < downDay);
    EXPECT_FALSE(d < downMonth);
    EXPECT_FALSE(d < downYear);

    EXPECT_TRUE(d <= d);
    EXPECT_TRUE(d <= same);
    EXPECT_TRUE(d <= upDay);
    EXPECT_TRUE(d <= upMonth);
    EXPECT_TRUE(d <= upYear);
    EXPECT_FALSE(d <= downDay);
    EXPECT_FALSE(d <= downMonth);
    EXPECT_FALSE(d <= downYear);

    EXPECT_TRUE(d >= d);
    EXPECT_TRUE(d >= same);
    EXPECT_FALSE(d >= upDay);
    EXPECT_FALSE(d >= upMonth);
    EXPECT_FALSE(d >= upYear);
    EXPECT_TRUE(d >= downDay);
    EXPECT_TRUE(d >= downMonth);
    EXPECT_TRUE(d >= downYear);

    EXPECT_FALSE(d > d);
    EXPECT_FALSE(d > same);
    EXPECT_FALSE(d > upDay);
    EXPECT_FALSE(d > upMonth);
    EXPECT_FALSE(d > upYear);
    EXPECT_TRUE(d > downDay);
    EXPECT_TRUE(d > downMonth);
    EXPECT_TRUE(d > downYear);
}

TEST(DateTest, Assignment) {
    Date orig(1, "January", 2000);
    Date copy = orig;
    EXPECT_EQ(orig, copy);

    Date override(2, "February", 2001);
    override = orig;
    EXPECT_EQ(orig, override);
}

TEST(DateTest, Copy) {
    Date orig(1, "January", 2000);
    Date copy(orig);
    EXPECT_EQ(orig, copy);
}

TEST(DateTest, IncYear) {
    Date orig(29, "February", 2000);
    Date sec = incYears(orig, 4);
    Date secActual(29, "February", 2004);
    EXPECT_EQ(sec, secActual);

    Date tri = incYears(sec, 1);
    Date triActual(28, "February", 2005);
    EXPECT_EQ(tri, triActual);
}

TEST(DateTest, IncMonth) {
    Date orig(31, "January", 2000);
    Date sec = incMonths(orig, 2);
    Date secActual(31, "March", 2000);
    EXPECT_EQ(sec, secActual);

    Date tri = incMonths(sec, 1);
    Date triActual(30, "April", 2000);
    EXPECT_EQ(tri, triActual);

    Date four = incMonths(tri, 11);
    Date fourActual(30, "March", 2001);
    EXPECT_EQ(four, fourActual);
}

TEST(DateTest, IncDay) {
    Date orig(28, "December", 2003);
    Date sec = incDays(orig, 1L);
    Date secActual(29, "December", 2003);
    EXPECT_EQ(sec, secActual);

    Date tri = incDays(sec, 3L);
    Date triActual(1, "January", 2004);
    EXPECT_EQ(tri, triActual);

    Date four = incDays(sec, 450L);
    Date fourActual(23, "March", 2005);
    EXPECT_EQ(four, fourActual);
}

TEST(DateTest, Today) {
    time_t t = time(0);
    struct tm* now = localtime(&t);
    char buffer[10];
    strftime(buffer, 10, "%B", now);

    Date today = Date::today();

    EXPECT_EQ(now->tm_mday, today.day());
    EXPECT_EQ(std::string(buffer), today.month());
    EXPECT_EQ(now->tm_year + 1900, today.year());
}


TEST(DateTestStreaming, Print) {
    std::stringstream output;

    Date d(1, "February", 2000);

    output << d;
    EXPECT_EQ("1 February, 2000", output.str());
}

TEST(DateTestStreaming, Read) {
    Date expected(2, "February", 2000);

    std::stringstream ss("2 February, 2000");
    Date d(1, "January", 1900);
    ss >> d;

    EXPECT_EQ(d, expected);
}

TEST(DateTestStreaming, NoComma) {
    std::stringstream ss;
    Date d(1, "January", 1900);
    EXPECT_THROW_MSG("Invalid date value.", {
        ss.str("2 February 2000");
        ss >> d;
    });

    EXPECT_TRUE(ss.fail());
}

TEST(DateTestStreaming, WrongTypes) {
    std::stringstream ss;
    Date d(1, "January", 1900);
    EXPECT_THROW_MSG("Invalid date value.", {
        ss.str("February 2, 2000");
        ss >> d;
    });

    EXPECT_TRUE(ss.fail());
}

TEST(DateTestStreaming, MissingField) {
    std::stringstream ss0;
    Date d0(1, "January", 1900);
    EXPECT_THROW_MSG("Invalid date value.", {
        ss0.str("2 February,");
        ss0 >> d0;
    });

    std::stringstream ss1;
    Date d1(1, "January", 1900);
    EXPECT_THROW_MSG("Invalid date value.", {
        ss1.str("2");
        ss1 >> d1;
    });

    EXPECT_TRUE(ss0.fail());
    EXPECT_TRUE(ss1.fail());
}

TEST(DateTestStreaming, InvalidField) {
    std::stringstream ss0;
    Date d0(1, "January", 1900);
    EXPECT_THROW_MSG("Invalid day of the month.", {
        ss0.str("40 February, 2000");
        ss0 >> d0;
    });

    std::stringstream ss1;
    Date d1(1, "January", 1900);
    EXPECT_THROW_MSG("Invalid month.", {
        ss1.str("2 february, 2000");
        ss1 >> d1;
    });

    std::stringstream ss2;
    Date d2(1, "January", 1900);
    EXPECT_THROW_MSG("Invalid year.", {
        ss2.str("2 February, 2200");
        ss2 >> d2;
    });
}
