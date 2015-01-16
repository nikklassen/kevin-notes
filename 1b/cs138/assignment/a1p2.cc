#include <iostream>
#include <vector>


int main() {
	int maxLength;
	char type;
	std::string current;
	std::vector<std::string> vec;

	std::cin >> maxLength;
	std::cin.ignore();
	std::cin >> type;
	std::cin.ignore();

	if(maxLength <= 0) {
		std::cerr << "Error, line length must be positive." << std::endl;
		return -1;
	}

	while(getline(std::cin, current)) {
		vec.push_back(current);
	}

	if(type == 'f') {
		for(unsigned int i = 0; i < vec.size(); i++) {
			if(vec.at(i).length() <= maxLength) std::cout << vec.at(i);
			else std::cout << vec.at(i).substr(0,maxLength);
			std::cout << std::endl;
		}
	}
	else if(type == 'r') {
		for(int i = vec.size() - 1; i >= 0; i--) {
			if(vec.at(i).length() <= maxLength) std::cout << vec.at(i);
			else std::cout << vec.at(i).substr(0,maxLength);
			std::cout << std::endl;
		}
	}
	else if(type == 'g') {
		for(unsigned int i = 0; i < vec.size(); i++) {
			if(vec.at(i).find("fnord") != std::string::npos) {
				if(vec.at(i).length() <= maxLength) std::cout << vec.at(i);
				else std::cout << vec[i].substr(0,maxLength);
				std::cout << std::endl;
			}
		}
	}
	else {
		std::cerr << "Error, illegal command." << std::endl;
		return -2;
	}

	return 0;
}
