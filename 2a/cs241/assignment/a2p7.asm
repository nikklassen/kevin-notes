bne $2, $0, nonzero
jr $31

nonzero:
add $11, $1, $0
add $21, $1, $0
add $12, $2, $0

lis $20
.word print
lis $21
.word 0x00000001
lis $24
.word 0x00000004

savejr:
sw $31, -4($30)
lis $31
.word -4
add $30, $30, $31

printall:
lw $1, 0($11)
jalr $20

sub $12, $12, $21
add $11, $11, $24
bne $0, $12, printall

loadjr:
lis $31
.word 4
add $30, $30, $31
lw $31, -4($30)

cleanup:
add $1, $21, $0
add $11, $0, $0
add $12, $0, $0
add $20, $0, $0
add $21, $0, $0
add $24, $0, $0

jr $31

