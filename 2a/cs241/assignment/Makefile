all: a1 a2 a3 a5 a6 a7 a8 a9 a10 a11

wlpp: WLPPScan.class WLPPParse.class WLPPGen.class

.PHONY: wlpp_test
wlpp_test: wlpp
	`java WLPPScan < WLPP.test > WLPPScan.mips`
	`java WLPPParse < WLPPScan.mips > WLPPParse.mips`
	`java WLPPGen < WLPPParse.mips > WLPPGen.mips`
	`java cs241.linkasm < WLPPGen.mips > WLPPLink.mips`
	`java cs241.linkasm < print.cs.asm > print.merl`
	`java cs241.linkasm < alloc.cs.asm > alloc.merl`
	`java cs241.linker WLPPLink.mips print.merl alloc.merl > WLPP.mips`
	`java mips.twoints WLPP.mips`


.SUFFIXES:
.SUFFIXES: .merl .asm
.SUFFIXES: .mips .asm
.SUFFIXES: .mips .hex
.SUFFIXES: .mips .wlpp
.SUFFIXES: .class .java

.asm.merl:
	`java cs241.wordasm < $^ > $@`

.asm.mips:
	`java cs241.binasm < $^ > $@`

.hex.mips:
	`java cs241.wordasm < $^ > $@`

.wlpp.mips:
	`java cs241.wlppc < $^ > $@`

.java.class:
	`javac $^`


a1: helloworld.mips a1p2.mips a1p3.mips a1p4.mips a1p5.mips a1p6.mips

a2: a2p1.mips a2p2.mips a2p3.mips a2p4.mips a2p5.mips a2p6.mips a2p7.mips a2p8.mips a2bonus.mips

a2p7.mips: print.asm a2p7.asm
	`java cs241.binasm < print.asm a2p7.asm > a2p7.mips`

a3: Twice.class Asm.class RelAsm.class

a5: a5p1.merl gcd.mips sum.mips

a6: WLPPScan.class Galaxy.class

a7: LR.class WLPPParse.class

a8: WLPPGen.class

a9: WLPPGen.class

a10: WLPPGen.class

a11: WLPPGen.class


clean:
	rm -f *.class
	rm -f *.merl
	rm -f *.mips
