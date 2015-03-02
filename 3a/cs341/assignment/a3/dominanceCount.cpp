#include <vector>
#include <iostream>

#include "dominance.h"


Dominance dominanceCount(std::vector<Point> points) {
    std::vector<Point> sorted = std::vector<Point>();
    if (points.size() == 0) {
        sorted.push_back({INFINITY, INFINITY, BLUE});
        return {0, sorted};
    } else if (points.size() == 1) {
        sorted.push_back(points.at(0));
        sorted.push_back({INFINITY, INFINITY, BLUE});
        return {0, sorted};
    }

    std::vector<Point> leftPoints = std::vector<Point>();
    std::vector<Point> rightPoints = std::vector<Point>();
    int cutoff = (points.size() / 2) - 1;
    for (std::vector<Point>::size_type i = 0; i != points.size(); ++i) {
        if (i <= cutoff) {
            leftPoints.push_back(points.at(i));
        } else {
            rightPoints.push_back(points.at(i));
        }
    }

    Dominance left = dominanceCount(leftPoints);
    Dominance right = dominanceCount(rightPoints);

    int i = 0, j = 0;
    int n = 0;
    int c = 0;

    for (unsigned int k = 0; k < points.size(); ++k) {
        if (left.points.at(i).y <= right.points.at(j).y) {
            sorted.push_back(left.points.at(i));
            if (left.points.at(i).color == BLUE) {
                ++n;
            }
            ++i;
        } else {
            sorted.push_back(right.points.at(j));
            if (right.points.at(j).color == RED) {
                c += n;
            }
            ++j;
        }
    }
    sorted.push_back({INFINITY, INFINITY, BLUE});

    return {c + left.c + right.c, sorted};
}
