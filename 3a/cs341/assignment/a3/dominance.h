#ifndef DOMINANCE_H
#define DOMINANCE_H

#include <limits>
#include <vector>

#define INFINITY std::numeric_limits<int>::max()

#define RED 0
#define BLUE 1


struct Point {
    int x;
    int y;
    int color;
};

struct Dominance {
    int c;
    std::vector<Point> points;
};

Dominance dominanceCount(std::vector<Point> points);

#endif
