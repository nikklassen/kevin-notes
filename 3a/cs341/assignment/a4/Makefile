all: a4.pdf

test: bipartiteDisks


a4.pdf: a4.tex
	pdflatex $^

bipartiteDisks: bipartiteDisks.cpp
	${CXX} -std=c++11 -g -Wall $^ -o $@


clean:
	rm -f *.aux
	rm -f *.log
	rm -f *.pdf
	rm -f *.toc
	rm -f bipartiteDisks
