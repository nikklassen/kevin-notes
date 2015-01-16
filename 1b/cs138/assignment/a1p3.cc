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

	while(std::cin >> current) {
		std::cin.ignore();
		vec.push_back(current);
	}

	int content = 0;
	if(vec[0].length() > maxLength) std::cout << vec[0].substr(0,maxLength) << std::endl;
	else {
		std::cout << vec[0];
		content += vec[0].length();
	}

	for(int i = 1; i < vec.size(); i++) {
		if(content + 1 + vec.at(i).length() <= maxLength) {
			if(content != 0) {
				std::cout << ' ';
				content++;
			}
			if(vec.at(i).length() > maxLength) {
				std::cout << vec.at(i).substr(0,maxLength) << std::endl;
				break;
			}
			std::cout << vec.at(i);
			content += vec.at(i).length();
		}
		else {
			if(vec.at(i).length() > maxLength) {
				std::cout << std::endl << vec.at(i).substr(0,maxLength);
				break;
			}
			content = 0;
			std::cout << std::endl;
			i--;
		}
	}
	std::cout << std::endl;

	return 0;
}