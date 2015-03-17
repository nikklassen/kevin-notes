#include <cmath>
#include <iostream>
#include <queue>
#include <vector>


#define SUCCESS 1
#define FAIL 0

#define DEFAULT_PARTITION -1


struct Disk {
    int x;
    int y;
    int radius;

    int partition;
};

int inRange(const Disk left, const Disk right) {
    double xDist = pow(left.x - right.x, 2);
    double yDist = pow(left.y - right.y, 2);
    double range = pow(left.radius + right.radius, 2);

    return xDist + yDist <= range;
}

int BFS(std::vector<Disk> *disks, int idx) {
    std::queue<int> q;
    q.push(idx);

    disks->at(q.front()).partition = 1;
    while (!q.empty()) {
        int curr = q.front();
        q.pop();

        for (int i = 0; i < disks->size(); ++i) {
            if (i == curr) {
                continue;
            } else if (inRange(disks->at(i), disks->at(curr))) {
                if (disks->at(i).partition == DEFAULT_PARTITION) {
                    disks->at(i).partition = disks->at(curr).partition == 1 ? 2 : 1;
                    q.push(i);
                } else if (disks->at(i).partition == disks->at(curr).partition) {
                    return FAIL;
                }
            }
        }
    }

    return SUCCESS;
}

int partition(std::vector<Disk> *disks) {
    for (int i = 0; i < disks->size(); ++i) {
        if (disks->at(i).partition == DEFAULT_PARTITION) {
            int retValue = BFS(disks, i);
            if (!retValue) {
                return FAIL;
            }
        }
    }

    return SUCCESS;
}

int main(int argc, char *argv[]) {
    int n;

    std::vector<Disk> *disks = new std::vector<Disk>();
    for (std::cin >> n; n > 0; --n) {
        Disk d;

        std::cin >> d.x;
        std::cin >> d.y;
        std::cin >> d.radius;
        d.partition = DEFAULT_PARTITION;

        disks->push_back(d);
    }

    int exists = partition(disks);
    if (!exists) {
        std::cout << "0" << std::endl;
    } else {
        for (int i = 0; i < disks->size(); ++i) {
            std::cout << disks->at(i).partition;
            if (i < disks->size() - 1) {
                std::cout << " ";
            }
        }
        std::cout << std::endl;
    }

    return 0;
}
