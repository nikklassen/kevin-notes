; $1 is list
; $2 is n
; $3 is ans
; $4 is k


; The median element is the floor($2/2) + 1th element (k)
lis $3
.word 2
div $2, $3
mflo $4			; $4 is floor($2/2)

lis $3
.word 1
add $4, $4, $3	; $4 is floor($2/2) + 1


; $5 is i
; $6 is minIndex
; $7 is minValue
; $8 is j
; $9 is list[j]
; $20 is temp
lis $28
.word 4
lis $29
.word 1


; Find the k smallest elements, sort them
;
; based on:
;
; for i from 1 to k {
;    minIndex = i
;    minValue = list[i]
;    for j from i+1 to n {
;       if list[j] < minValue
;          minIndex = j
;          minValue = list[j]
;    }
;    swap list[i] and list[minIndex]
;  }
;  return list[k]
;


add $5, $0, $29		; i = 1
iloop:				; for i from 1 to k
add $6, $0, $5		; minIndex = i

mult $5, $28
mflo $20
add $20, $20, $1
lw $7, 0($20)		; minValue = list[i]


add $8, $5, $29		; j = i + 1
jloop:
mult $8, $28
mflo $20
add $20, $20, $1
lw $9, 0($20)		; $9 = list[j]

slt $20, $9, $7
bne $20, $29, skipj	; iff list[j] < minValue
add $6, $0, $8		; minIndex = j
add $7, $0, $9		; minValue = list[j]
skipj:

add $8, $8, $29		; j++
slt $20, $2, $8		;
bne $20, $29, jloop	; loop iff j <= n


mult $29, $5
mflo $20
add $21, $1, $20	; $21 = i address
mult $29, $6
mflo $20
add $22, $1, $20	; $22 = minIndex addess

lw $21, -4($30)		; swap $21 and $22
lw $22, -8($30)
sw $21, -8($30)
sw $22, -4($30)

add $5, $5, $29		; for i from 1 to k
slt $20, $4, $5
bne $20, $29, iloop	; loop iff i <= k


lis $25
.word 0x41
lis $26
.word 0xffff000c
sw $25, 0($26)		; print a


mult $29, $4
mflo $20
add $20, $20, $1
lw $3, 0($20)		; return list[k]