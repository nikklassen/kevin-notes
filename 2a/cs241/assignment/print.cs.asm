.export print
; Register usage
; $1 - store the number to be printed
; $2 - not used, likely used by the driver
; $3 - store 0xffff000c for printing
; $4 - store 10 into $4
; $5 - store 4 into $5
; $6 - store the remainder for values
; $7 - store the value for branching and store the value for ascii 0
; $8 - store the register to be printed
; $9 - store the address of the current memory location
; $10 - store the value to be saved to memory for later printing

; save all registers used
print:
    sw $1, -4($30)
    sw $2, -8($30)
    sw $3, -12($30)
    sw $4, -16($30)
    sw $5, -20($30)
    sw $6, -24($30)
    sw $7, -28($30)
    sw $8, -32($30)
    sw $9, -36($30)
    sw $10, -40($30)
    lis $3
    .word -40
    add $30, $30, $3            ; allocate space for the saved registers

    lis $3
    .word 0xffff000c        ; $3 = 0xffff000c
    lis $4
    .word 10                ; $4 = 10
    lis $5
    .word 4                 ; $5 = 4
    add $6, $1, $0          ; $6 = $1
    slt $7, $1, $0          ; $7 = 1 iff $1 < 0, else $7 = 0
    beq $7, $0, IfDone
    lis $8
    .word 0x0000002d        ; load - (ascii 2D) into $8
    sw $8, 0($3)            ; print - from $8
    sub $6, $0, $6          ; $6 = 0 - $6
IfDone:
    add $9, $30, $0         ; $9 = $30

; calculate all the digits and save them onto the stack
; they are calculated in reverse order.  Popping them off the stack
; to print will put them in the forward order.
Loop:
    divu $6, $4             ; $6/ $4 (unsigned)
    mfhi $10                ; $10 = $6 % $4
    sw $10, -4($9)          ; mem[$9] = $10
    mflo $6                 ; $6 = $6 / $4
    sub $9, $9, $5          ; $9 = $9 - 4
    slt $10, $0, $6         ; $10 = 1 iff ($6 > 0) otherwise $10 = 0
    bne $10, $0, Loop       ; continue the loop until done

; use second Loop to print the digits in the right order
    lis $7
    .word 48                ; load character 0 (ascii 48) into $7
Loop2:
    lw $8, 0($9)            ; $8 = mem[$9]
    add $8, $8, $7         ; calculate the ascii value of the digit
    sw $8, 0($3)            ; print the character in $8
    add $9, $9, $5          ; $9 = $9 + 4
    bne $9, $30, Loop2      ; jump the loop
    sw $4, 0($3)            ; print character '\\n' (ascii 10)

    ; restore saved registers
    lis $3
    .word 40
    add $30, $30, $3        ; restore the stack pointer
    lw $1, -4($30)
    lw $2, -8($30)
    lw $3, -12($30)
    lw $4, -16($30)
    lw $5, -20($30)
    lw $6, -24($30)
    lw $7, -28($30)
    lw $8, -32($30)
    lw $9, -36($30)
    lw $10, -40($30)

    jr $31                  ; return control to the caller