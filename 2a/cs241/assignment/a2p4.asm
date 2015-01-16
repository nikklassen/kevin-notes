lis $21
.word 0x00000001
lis $24
.word 0x00000004

add $11, $1, $0
add $12, $2, $0

loop:
lw $4, 0($11)
slt $5, $3, $4
beq $0, $5, 1
add $3, $4, $0
sub $12, $12, $21
add $11, $11, $24
bne $0, $12, loop
jr $31

