all: a2_sub.pdf
	make -C q2


.SUFFIXES:
.SUFFIXES: .png .dot

.dot.png:
	dot -Tpng $< -o $@


blockCFG.png: blockCFG.dot
defuseCFG.png: defuseCFG.dot

a2_sub.pdf: a2_sub.tex blockCFG.png defuseCFG.png
	pdflatex a2_sub.tex


clean:
	rm -f *.aux
	rm -f *.log
	rm -f a2_sub.pdf
	make -C q2 clean
