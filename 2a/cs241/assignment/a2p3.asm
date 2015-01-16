bne $2, $0, 3
lis $3
.word 0xffffffff
jr $31
lis $3
.word 0x00000004
mult $2, $3
mflo $3
add $3, $3, $1
lw $3, -4($3)
jr $31
