all: a3.pdf


dominanceCount: dominanceCount.cpp dominanceRunner.cpp
	${CXX} -std=c++11 -g -Wall $^ -o $@

dominanceCountNaive: dominanceCountNaive.cpp dominanceRunner.cpp
	${CXX} -std=c++11 -g -Wall $^ -o $@

timingTable.tex: dominanceCount dominanceCountNaive
	rm -f test.output
	./generateTiming.py
	cp test.output timingTable.tex

a3.pdf: a3.tex timingTable.tex
	pdflatex a3.tex


clean:
	rm -f a3.aux
	rm -f a3.log
	rm -f a3.pdf
	rm -f a3.toc
	rm -f dominanceCount
	rm -f dominanceCountNaive
	rm -f test.input
	rm -f test.output
	rm -f timingTable.aux
	rm -f timingTable.log
	rm -f timingTable.pdf
	rm -f timingTable.tex
	rm -f timingTable.toc
