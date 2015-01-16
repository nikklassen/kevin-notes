bne $2, $0, 1
jr $31

lis $3
.word 0xffff000c
lis $21
.word 0x00000001
lis $24
.word 0x00000004
lis $20
.word 0x00000020

add $11, $1, $0
add $12, $2, $0

loop:
lw $4, 0($11)
beq $4, $0, skip
add $4, $4, $20
skip:
add $4, $4, $20
sw $4, 0($3)
add $11, $11, $24
sub $12, $12, $21
bne $12, $0, loop
jr $31
