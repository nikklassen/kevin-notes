#include <iostream>
#include <string>
#include <vector>
#include <cassert>
using namespace std;


class Animal {
private:
	string name;

protected:
	Animal(string name): name(name) {};

	virtual string getName() const {
		return this->name;
	}

public:
	virtual ~Animal() {};
	virtual void speak() = 0;
};

class Dog : public Animal {
public:
	Dog(string name): Animal(name) {};

	~Dog() {};

	void speak() {
		cout << "    Dog " << this->getName() << " says \"woof\"." << endl;
	}
};

class Sheep : public Animal {
public:
	Sheep(string name): Animal(name) {};

	~Sheep() {};

	void speak() {
		cout << "    Sheep " << this->getName() << " says \"baaa\"." << endl;
	}
};

class Flock {
private:
	Dog *dog;
	vector<Sheep*> sheepList;

public:
	Flock(string dogName) {
		this->dog = new Dog(dogName);
	}

	~Flock() {
		for(int i = 0; i < (int)sheepList.size(); i++) {
			delete this->sheepList.at(i);
		}
		delete this->dog;
	}

	void addSheep(string name) {
		this->sheepList.push_back(new Sheep(name));
	}
	
	void soundOff() {
		cout << "The flock of " << this->sheepList.size() << " sheep speaks!" << endl;
		
		this->dog->speak();

		for(int i = 0; i < sheepList.size(); i++) {
			this->sheepList.at(i)->speak();
		}

		cout << endl;
	}
};

int main (int argc, char* argv[]) {
	Flock *myFlock = new Flock ("Spot");
	myFlock->soundOff();
	myFlock->addSheep ("Daisy");
	myFlock->addSheep ("Clover");
	myFlock->addSheep ("Estelle");
	myFlock->soundOff();
	delete myFlock;
	myFlock = new Flock ("Rover");
	myFlock->addSheep ("Butch");
	myFlock->addSheep ("Jonno");
	myFlock->soundOff();
	// myFlock will die anyway when the program ends, but it’s
	// still good form to delete all objects you create via "new"
	delete myFlock;
}