add $3, $0, $0
bne $2, $0, config
jr $31


config:
add $11, $1, $0			; $11 holds the current element
lis $21
.word 0xffffffff
lis $24
.word 0x00000004
lis $29					; jalr $29 will call our function
.word recurse


recurse:
sw $4, -4($30)
sub $30, $30, $24
sw $5, -4($30)
sub $30, $30, $24

lw $4, 4($11)
lw $5, 8($11)
beq $4, $5, retone


left:
beq $4, $21, endleft

mult $4, $24
mflo $4
add $11, $1, $4

sw $31, -4($30)
sub $30, $30, $24
jalr $29
add $30, $30, $24
lw $31, -4($30)

sub $11, $1, $4
add $4, $3, $0
endleft:

right:
beq $5, $21, endright

mult $5, $24
mflo $5
add $11, $1, $5

sw $31, -4($30)
sub $30, $30, $24
jalr $29
add $30, $30, $24
lw $31, -4($30)

sub $11, $1, $5
add $5, $3, $0
endright:


slt $3, $4, $5
beq $3, $0, copyleft
beq $0, $0, copyright


copyleft:
add $3, $4, $0
beq $0, $0, exit

copyright:
add $3, $5, $0
beq $0, $0, exit

retone:
add $3, $0, $0
beq $0, $0, exit


exit:
sub $3, $3, $21

add $30, $30, $24
lw $5, -4($30)
add $30, $30, $24
lw $4, -4($30)

jr $31

