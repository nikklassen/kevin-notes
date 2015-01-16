lis $20
.word 0xffff000c		; printing address
lis $21
.word 0x0000000a		; divide by 10, also print newline

slt $3, $1, $0			; check for negative
beq $3, $0, positive

lis $3
.word 0x0000002d
sw $3, 0($20)			; print a dash

lis $3
.word 0x80000000
beq $1, $3, smallest	; -2147483648 must be handled differently (we can not store +2147483648)

sub $1, $0, $1			; abs($1)

positive:
add $29, $30, $0		; copy stack pointer for later comparison
add $3, $1, $0			; copy input so we don't destroy it

lis $22
.word 0x00000030		; ascii offset
lis $23
.word 0x00000004		; stack offset

pushloop:
div $3, $21				; divide by ten
mfhi $4					; get last digit
mflo $3					; get remaining digits
sub $29, $29, $23		; move stack pointer
add $4, $4, $22			; to ASCII
sw $4, 0($29)			; push to stack
bne $3, $0, pushloop

pop:
lw $4, 0($29)			; get top of stack
sw $4, 0($20)			; print digit
add $29, $29, $23		; move stack pointer
bne $29, $30, pop

end:
sw $21, 0($20)			; print newline
jr $31

; -2147483648
smallest:
lis $3
.word 0x00000032
sw $3, 0($20)
lis $3
.word 0x00000031
sw $3, 0($20)
lis $3
.word 0x00000034
sw $3, 0($20)
lis $3
.word 0x00000037
sw $3, 0($20)
lis $3
.word 0x00000034
sw $3, 0($20)
lis $3
.word 0x00000038
sw $3, 0($20)
lis $3
.word 0x00000033
sw $3, 0($20)
lis $3
.word 0x00000036
sw $3, 0($20)
lis $3
.word 0x00000034
sw $3, 0($20)
lis $3
.word 0x00000038
sw $3, 0($20)

beq $0, $0, end

