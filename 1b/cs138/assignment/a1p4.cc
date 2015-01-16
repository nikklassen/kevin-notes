#include <iostream>
#include <vector>

int main() {
	int maxLength;
	std::string current;
	std::vector<std::string> vec;
	std::vector<std::string> line;
	std::vector<std::string> last;

	std::cin >> maxLength;
	std::cin.ignore();

	if(maxLength <= 0) {
		std::cerr << "Error, line length must be positive." << std::endl;
		return -1;
	}

	while(std::cin >> current) {
		std::cin.ignore();
		vec.push_back(current);
	}

	int content = 0;
	int remain = 0;
	int allSpace = 0;
	int frontSpace = 0;
	int curIndex = 0;

	for(unsigned int i = 0; i < vec.size() - 1; i++) {
		content += vec.at(i).length();
		line.push_back(vec.at(i));
		if(content + 1 + vec.at(i+1).length() <= maxLength) content += 1;
		else {
			remain = maxLength - content;
			if(line.size() != 1) {
				allSpace = remain / (line.size() - 1);
				frontSpace = remain % (line.size() - 1);
				if(frontSpace >= line.size()) {
					frontSpace -= (line.size() - 1);
					allSpace += 1;
				}
			}

			for(unsigned int j = 0; j <= i - curIndex; j++) {
				if(line.at(j).length() <= maxLength) std::cout << line.at(j);
				else std::cout << line.at(j).substr(0,maxLength);
				if(j != i - curIndex) std::cout << ' ';
				if(frontSpace && j != i - curIndex) {
					std::cout << ' ';
					frontSpace--;
				}
				for(unsigned int k = 0; k < allSpace; k++) {
					if(j != i - curIndex) std::cout << ' ';
				}
			}

			if(line.size() == 1) {
				for(int q = 0; q < remain; q++) {
					std::cout << ' ';
				}
			}

			curIndex = i + 1;
			std::cout << std::endl;
			content = remain = allSpace = frontSpace = 0;

			line.resize(0);
		}
	}

	// Disgusting hack!
	content = 0;
	for(int z = 0; z < line.size(); z++) {
		last.push_back(line.at(z));
		content += line.at(z).length() + 1;
	}
	last.push_back(vec.at(vec.size() - 1));
	content += vec.at(vec.size() - 1).length();

	remain = maxLength - content;
	if(last.size() != 1) {
		allSpace = remain / (last.size() - 1);
		frontSpace = remain % (last.size() - 1);
	}

	if(frontSpace >= last.size()) {
		frontSpace -= (last.size() - 1);
		allSpace += 1;
	}

	for(int y = 0; y < last.size(); y++) {
		if(last.at(y).length() <= maxLength) std::cout << last.at(y);
		else std::cout << last.at(y).substr(0,maxLength);
		if(y != last.size() - 1) std::cout << ' ';
		if(frontSpace && y != last.size() - 1) {
			std::cout << ' ';
			frontSpace--;
		}
		for(int x = 0; x < allSpace; x++) {
			if(y != last.size() - 1) std::cout << ' ';
		}
	}
	if(last.size() == 1) {
		for(int q = 0; q < remain; q++) {
			std::cout << ' ';
		}
	}

	std::cout << std::endl;

	return 0;
}
