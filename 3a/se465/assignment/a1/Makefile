all: a1_sub.pdf
	make -C q3


a1_sub.pdf: a1_sub.tex cfg.png
	pdflatex a1_sub.tex

cfg.png: cfg.dot
	dot -Tpng cfg.dot -o cfg.png


clean:
	rm -f a1_sub.pdf
	rm -f cfg.png
	make -C q3 clean
