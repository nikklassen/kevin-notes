#include <iostream>
#include <vector>


int main() {
	int maxLength;
	std::string current;
	std::vector<std::string> vec;

	std::cin >> maxLength;
	std::cin.ignore();

	if(maxLength <= 0) {
		std::cerr << "Error, line length must be positive." << std::endl;
		return -1;
	}

	std::cout << std::endl; // Weird whitespace between input and output

	while(getline(std::cin, current)) {
		vec.push_back(current);
	}

	for(unsigned int i = 0; i < vec.size(); ++i) {
		if(vec[i].length() <= maxLength) std::cout << vec[i];
		else std::cout << vec[i].substr(0,maxLength);
		std::cout << std::endl;
	}

	return 0;
}
