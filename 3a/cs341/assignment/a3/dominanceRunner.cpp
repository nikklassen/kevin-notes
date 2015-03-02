#include <iostream>
#include <vector>

#include "dominance.h"


int main(int argc, char *argv[]) {
    int n;

    std::vector<Point> points = std::vector<Point>();
    for (std::cin >> n; n > 0; --n) {
        Point p;

        std::cin >> p.x;
        std::cin >> p.y;
        std::cin >> p.color;

        points.push_back(p);
    }

    Dominance dominance = dominanceCount(points);

    std::cout << dominance.c << std::endl;
    // DEBUGGING
    // for (std::vector<Point>::iterator it = dominance.points.begin(); it != dominance.points.end() - 1; ++it) {
    //     std::cout << (*it).x << ' ' << (*it).y << ' ' << (*it).color << std::endl;
    // }

    return 0;
}
