#include <vector>

#include "dominance.h"


Dominance dominanceCount(std::vector<Point> points) {
    int n = 0;
    for (std::vector<Point>::size_type i = 0; i != points.size(); ++i) {
        for (std::vector<Point>::size_type j = i; j != points.size(); ++j) {
            if (points.at(j).y > points.at(i).y && points.at(i).color == BLUE && points.at(j).color == RED) {
                ++n;
            }
        }
    }

    return {n, points};
}
