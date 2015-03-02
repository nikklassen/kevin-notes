#!/usr/bin/env python
import random
import os
import time


inputs = (10, 50, 100, 250, 500, 750, 1000, 2000, 5000)


with open('test.output', 'w') as output:
    output.write("\\begin{table}[ht]\n")
    output.write("\\centering\n")
    output.write("\\caption{Timing data for Dominance Counting algorithm}\n")
    output.write("\\begin{tabular}{|c|c|c|}\n")
    output.write("\\hline\n")
    output.write("Num Points & DivideAndConquer & Brute Force\\\\\\hline")

    for n in inputs:
        with open('test.input', 'w') as f:
            f.write(str(n) + "\n")

            x = sorted(random.sample(range(1, 10000), n))
            y = random.sample(range(1, 10000), n)

            for i in xrange(n):
                f.write(str(x[i]) + " ")
                f.write(str(y[i]) + " ")
                f.write(str(random.randint(0, 1)) + "\n")

        output.write(str(n) + " & ")
        start = time.time()
        os.system("./dominanceCount < test.input")
        end = time.time()
        output.write(str(end - start))

        output.write(" & ")
        outputtart = time.time()
        os.system("./dominanceCountNaive < test.input")
        end = time.time()
        output.write(str(end - start))
        output.write("\\\\\n")

    output.write("\\hline\n")
    output.write("\\end{tabular}\n")
    output.write("\\end{table}\n")
