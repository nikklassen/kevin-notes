all: test

.PHONY: test
test: TestM.class
	java -cp .:/usr/share/java/junit4.jar org.junit.runner.JUnitCore TestM


TestM.class: TestM.java
	javac -cp .:/usr/share/java/junit4.jar TestM.java 


clean:
	rm -f *.class
