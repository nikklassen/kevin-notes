;; Memory allocation module for WLPP
;; 
;; Author:   Brad Lushman, June 8, 2011
;;
;; This module provides heap allocation/deallocation functionality, via the
;; binary buddy system.  Heap memory is allocated from the beginning of free
;; RAM, and its size is capped at 4K (though this can be changed).  Minimum
;; size of allocation units is 4 words, and block sizes are powers of 2.
;; One word of each block is reserved for deallocation info, so a request for
;; 3 words returns a 4-word block, and a request for 4 words returns an 8-word
;; block.
;;
;; This module must be linked at the *end* of any program that uses it.
;; Requires external function "print" to be linked in.
;;
;; init -- initializes allocator.  Must call this function once before using
;;         new and delete.
;;      -- When calling within mips.array, pass in $2 the length of the array;
;;         when calling within mips.twoints, pass in $2 = 0.
;; new  -- allocates $1 words of memory; returns address in $3
;;      -- returns 0 in $3 if allocation fails
;; delete -- deallocates memory at address $1

.export init
.export new
.export delete
.export printFreeList
.import print

init:
   sw $1, -4($30)
   sw $2, -8($30)
   sw $3, -12($30)
   sw $4, -16($30)
   sw $5, -20($30)
   sw $6, -24($30)
   sw $7, -28($30)
   sw $8, -32($30)

   lis $4
   .word 32
   sub $30, $30, $4

   lis $1
   .word end
   lis $3
   .word 1024       ; space for free list (way more than necessary)

   lis $6
   .word 16         ; size of bookkeeping region at end of program

   lis $7
   .word 4096       ; size of heap

   lis $8
   .word 1
   add $2, $2, $2   ; Convert array length to words (*4)
   add $2, $2, $2
   add $2, $2, $6   ; Size of "OS" added by loader

   add $5, $1, $6   ; end of program + length of bookkeeping
   add $5, $5, $2   ; + length of incoming array
   add $5, $5, $3   ; + length of free list

   sw $5, 0($1)     ; store address of heap at Mem[end]
   add $5, $5, $7   ; store end of heap at Mem[end+4]
   sw $5, 4($1)
   sw $8, 8($1)     ; store initial size of free list (1) at Mem[end+8]

   add $5, $1, $6
   add $5, $5, $2
   sw $5, 12($1)   ; store location of free list at Mem[end+12]
   sw $8, 0($5)    ; store initial contents of free list (1) at Mem[end+12]
   sw $0, 4($5)    ; zero-terminate the free list

   add $30, $30, $4

   lw $1, -4($30)
   lw $2, -8($30)
   lw $3, -12($30)
   lw $4, -16($30)
   lw $5, -20($30)
   lw $6, -24($30)
   lw $7, -28($30)
   lw $8, -32($30)
   jr $31

;; new -- allocates memory (in 16-byte blocks)
;; $1 -- requested size in words
;; $3 -- address of allocated memory (0 if none available)  OUTPUT
new:
   sw $1, -4($30)
   sw $2, -8($30)
   sw $4, -12($30)
   sw $5, -16($30)
   sw $6, -20($30)
   sw $7, -24($30)
   sw $8, -28($30)
   sw $9, -32($30)
   sw $10, -36($30)
   sw $11, -40($30)
   sw $12, -44($30)

   lis $10
   .word 44
   sub $30, $30, $10

   ;; Make sure requested size > 0 ; if not, bail out.
   slt $3, $0, $1
   beq $3, $0, cleanupN

   lis $11   ; $11 = 1
   .word 1

   add $1, $1, $11 ; One extra word to store deallocation info
   add $1, $1, $1  ; Convert $1 from words to bytes
   add $1, $1, $1

   add $2, $11, $11  ; $2 = 2
   add $4, $0, $0  ; $4 = counter, to accumulate ceil(log($1))

   ;; Repeatedly dividing $1 by 2 and counting the divisions gives
   ;; floor (log($1)).  To get ceil(log($1)), evaluate floor(log($1-1))+1
   sub $1, $1, $11  ; So subtract 1 from $1

 topN:  ; Repeatedly divide $1 by 2, and count iterations
   beq $1, $0, endloopN
   div $1, $2      ; $1 /= 2
   mflo $1
   add $4, $4, $11  ; $4++

   beq $0, $0, topN
 endloopN:

   add $1, $1, $11  ; Now add 1 to $1 to restore its value after previous sub
   add $4, $4, $11  ; And add 1 to $4 to complete ceil calculation (see above)

   ;; An address' allocation code will consist of $14-$4 bits
   lis $5     ; $5 = 14
   .word 14  

   sub $4, $5, $4  ; $4 <- 14 - $4  

   ;; Cap the number of bits in an allocation code at 9 (so we don't allocate
   ;; blocks smaller than 4 words at a time).
   lis $5
   .word 9

   slt $6, $5, $4 
   beq $6, $0, doNotFixN
   add $4, $5, $0

 doNotFixN:
   ; Make sure requested size is not too big, i.e., $4>0
   slt $3, $0, $4
   beq $3, $0, cleanupN

   ; Now search for a word in the free list with that many bits or fewer
   ; (Fewer bits = larger block size)
   ; Compute largest possible $4-bit number, store in $7
   add $6, $4, $0    ; countdown from $4 to 0
   add $7, $11, $0   ; accumulates result by doubling $4 times
 top2N:
   add $7, $7, $7    ; double $7
   sub $6, $6, $11   ; $6--
   bne $6, $0, top2N

   sub $7, $7, $11  ; At the end of the loop, $7 = 2^$4 - 1

   ; Find largest word in freelist <= $7
   lis $8
   .word findWord
   sw $31, -4($30)
   lis $31
   .word 4
   sub $30, $30, $31
   jalr $8          ; call findWord
   lis $31
   .word 4
   add $30, $30, $31
   lw $31, -4($30)

   ; If no match found, cleanup and abort

   beq $3, $0, cleanupN  ; if allocation fails, clean up and return 0
   
     ; Compute minimum code for exact match  (($7+1)/2)
   add $7, $7, $11
   div $7, $2
   mflo $7
   ; If exact match found, remove it from the free list
 exactN:
   slt $6, $3, $7
   bne $6, $0, largerN

   beq $0, $0, convertN

   ; If larger match found, split into smaller buddies
 largerN:  ;; buddies are 2$3 and 2$3+1
   add $3, $3, $3 ;; double $3
   ; add 2$3+1 to free list; evaluate 2$3 as possible candidate
   lis $6   ;; $6 = address of address of free list
   .word free
   lw $8, -4($6)  ;; $8 = length of free list
   lw $6, 0($6)   ;; $6 = address of free list
   add $8, $8, $8 ;; convert to words (*4)
   add $8, $8, $8
   add $6, $6, $8 ;; address of next spot in free list
   add $8, $3, $11 ;; $8 = buddy
   sw $8, 0($6)   ;; add to end of list
   sw $0, 4($6)
   ;; increment length of free list
   lis $6
   .word free
   lw $8, -4($6)
   add $8, $8, $11
   sw $8, -4($6)

   ; now go back to exact with new value of $3, and re-evaluate
   beq $0, $0, exactN

   ; Convert number to address
 convertN:
   add $12, $3, $0  ; retain original freelist word
   add $7, $0, $0 ;; offset into heap
   lis $8
   .word end
   lw $9, 4($8)  ;; end of heap
   lw $8, 0($8)  ;; beginning of heap
   sub $9, $9, $8 ;; size of heap (bytes)
 top5N:
   beq $3, $11, doneconvertN
   div $3, $2
   mflo $3    ;; $3/2
   mfhi $10   ;; $3%2
   beq $10, $0, evenN
   add $7, $7, $9   ;; add size of heap to offset
 evenN:
   div $7, $2       ;; divide offset by 2
   mflo $7
   beq $0, $0, top5N

 doneconvertN:
   add $3, $8, $7  ;; add start of heap to offset to get address
   lis $4
   .word 4
   add $3, $3, $4  ;; advance one byte for deallocation info
   sw $12, -4($3)  ;; store deallocation info

 cleanupN:
   lis $10
   .word 44
   add $30, $30, $10

   lw $1, -4($30)
   lw $2, -8($30)
   lw $4, -12($30)
   lw $5, -16($30)
   lw $6, -20($30)
   lw $7, -24($30)
   lw $8, -28($30)
   lw $9, -32($30)
   lw $10, -36($30)
   lw $11, -40($30)
   lw $12, -44($30)
   jr $31

;; delete -- frees allocated memory
;; $1 -- address to be deleted
delete:
   sw $1, -4($30)
   sw $2, -8($30)
   sw $3, -12($30)
   sw $4, -16($30)
   sw $5, -20($30)
   sw $6, -24($30)
   sw $11, -28($30)
   sw $12, -32($30)
   sw $14, -36($30)

   lis $6
   .word 36
   sub $30, $30, $6

   lis $11
   .word 1

   lis $12
   .word 2

   lis $14
   .word 4

   lw $2, -4($1) ;; buddy code for the allocated block

 nextBuddyD:
   beq $2, $11, notFoundD  ;; if there is no buddy (i.e. buddy code=1), bail out
   ;; compute buddy's buddy code  (i.e, add 1 if code is even, sub 1 if odd)
   add $3, $2, $0
   div $3, $12   ; $4 = $3 % 2
   mfhi $4

   beq $4, $0, evenD
   sub $3, $3, $11
   beq $0, $0, doneParityD
 evenD:
   add $3, $3, $11
 doneParityD:

   ;; Now search free list for the buddy; if found, remove, and divide the
   ;; buddy code by 2; if not found, add current buddy code to the free list.
   lis $5
   .word findAndRemove
   sw $31, -4($30)
   sub $30, $30, $14
   add $1, $3, $0
   jalr $5
   add $30, $30, $14
   lw $31, -4($30)

   ;; If the procedure succeeded in finding the buddy, $3 will be 1; else it
   ;; will be 0.
   beq $3, $0, notFoundD
   div $2, $12
   mflo $2
   beq $0, $0, nextBuddyD

  notFoundD:
   lis $4   ;; address of address of free list
   .word free
   lw $5, -4($4) ; length of the free list
   lw $4, 0($4)  ;; address of the free list

   add $5, $5, $5  ; convert to offset
   add $5, $5, $5
   add $5, $4, $5  ; address of next spot in free list
   sw $2, 0($5)    ; put code back into free list
   sw $0, 4($5)    ; keep free list 0-terminated

   ; update size of free list
   lis $4
   .word free
   lw $5, -4($4)
   add $5, $5, $11
   sw $5, -4($4)

   lis $6
   .word 36
   add $30, $30, $6

   lw $1, -4($30)
   lw $2, -8($30)
   lw $3, -12($30)
   lw $4, -16($30)
   lw $5, -20($30)
   lw $6, -24($30)
   lw $11, -28($30)
   lw $12, -32($30)
   lw $14, -36($30)
   jr $31

;; findWord -- find and remove largest word from free list <= given limit
;;             return 0 if not possible
;; Registers:
;;   $7 -- limit
;;   $3 -- output
findWord:
    sw $1, -4($30)
    sw $2, -8($30)
    sw $4, -12($30)
    sw $5, -16($30)
    sw $6, -20($30)
    sw $7, -24($30)
    sw $8, -28($30)
    sw $9, -32($30)
    sw $10, -36($30)
    lis $1
    .word 36
    sub $30, $30, $1
    
    ;; $1 = start of free list
    ;; $2 = length of free list
    lis $1  ;; address of address of the free list
    .word free
    lw $2, -4($1)
    lw $1, 0($1) ;; address of the free list
    lis $4   ; $4 = 4 (for looping increments over memory)
    .word 4
    lis $9   ; $9 = 1 (for loop decrements)
    .word 1

    add $3, $0, $0  ;; initialize output to 0 (not found)
    add $10, $0, $0 ;; for address of max word
    beq $2, $0, cleanupFW  ;; skip if no free memory
    add $5, $2, $0  ;; loop countdown to 0
 topFW:
    lw $6, 0($1)
    slt $8, $7, $6  ;; limit < current item (i.e. item ineligible?)
    bne $8, $0, ineligibleFW
    slt $8, $3, $6  ;; max < current item?
    beq $8, $0, ineligibleFW  ; if not, skip to ineligible
    add $3, $6, $0  ;; replace max with current
    add $10, $1, $0 ;; address of current
 ineligibleFW:
    add $1, $1, $4  ;; increment address
    sub $5, $5, $9  ;; decrement loop counter
    bne $5, $0, topFW     ;; if items left, continue looping

 ;; if candidate not found, bail out (if not found, $3 will still be 0)
    beq $3, $0, cleanupFW

 ;; now loop from $10 to end, moving up array elements
 top2FW:
    lw $6, 4($10)  ;; grab next element in array
    sw $6, 0($10)  ;; store in current position
    add $10, $10, $4 ;; increment address
    bne $6, $0, top2FW  ;; continue while elements nonzero

 ;; decrement length of free list
    lis $2
    .word end
    lw $4, 8($2)
    sub $4, $4, $9  ; $9 still 1
    sw $4, 8($2)

 cleanupFW:

    lis $1
    .word 36
    add $30, $30, $1
    lw $1, -4($30)
    lw $2, -8($30)
    lw $4, -12($30)
    lw $5, -16($30)
    lw $6, -20($30)
    lw $7, -24($30)
    lw $8, -28($30)
    lw $9, -32($30)
    lw $10, -36($30)
    jr $31

;; findAndRemove -- find and remove given word from free list
;;             return 1 for success, 0 for failure
;; Registers:
;;   $1 -- word to remove
;;   $3 -- output (1 = success, 0 = failure)
findAndRemove:
   sw $1, -4($30)
   sw $2, -8($30)
   sw $4, -12($30)
   sw $5, -16($30)
   sw $6, -20($30)
   sw $7, -24($30)
   sw $8, -28($30)
   sw $9, -32($30)
   sw $11, -36($30)
   sw $14, -40($30)

   lis $9
   .word 40
   sub $30, $30, $9

   lis $11
   .word 1

   lis $14
   .word 4

   lis $2     ;; address of address of the free list
   .word free
   lw $4, -4($2) ;; length of the free list
   lw $2, 0($2)  ;; address of the free list


   add $3, $0, $0 ; success code
   add $6, $0, $0 ; address of found code
   add $7, $0, $0 ; loop counter

 topFaR:  ; loop through free list, looking for the code
   beq $4, $0, cleanupFaR
   lw $5, 0($2) ; next code in list
   bne $5, $1, notEqualFaR  ;; compare with input
   add $6, $6, $2  ; if code found, save its address
   beq $0, $0, removeFaR

 notEqualFaR:  ; current item not the one we're looking for; update counters
   add $2, $2, $14
   add $7, $7, $11
   bne $7, $4, topFaR

 removeFaR:
   beq $6, $0, cleanupFaR  ;; if code not found, bail out

 top2FaR:  ; now loop through the rest of the free list, moving each item one
           ; slot up
   lw $8, 4($2)
   sw $8, 0($2)
   add $2, $2, $14  ; add 4 to current address
   add $7, $7, $11  ; add 1 to loop counter
   bne $7, $4, top2FaR
   add $3, $11, $0  ;; set success code

   ;; decrement size
   lis $2
   .word free
   lw $5, -4($2)
   sub $5, $5, $11
   sw $5, -4($2)

 cleanupFaR:
   lis $9
   .word 40
   add $30, $30, $9

   lw $1, -4($30)
   lw $2, -8($30)
   lw $4, -12($30)
   lw $5, -16($30)
   lw $6, -20($30)
   lw $7, -24($30)
   lw $8, -28($30)
   lw $9, -32($30)
   lw $11, -36($30)
   lw $14, -40($30)
   jr $31

;; printFreeList -- prints the contents of the free list, for testing and
;;  debugging purposes.  Requires a print routine for $1 to be linked in.
;;  Registers:
;;    Input -- none
;;    Output -- none
printFreeList:
   sw $1, -4($30)
   sw $2, -8($30)
   sw $3, -12($30)
   sw $4, -16($30)
   sw $5, -20($30)
   sw $6, -24($30)
   sw $7, -28($30)
   sw $8, -32($30)
   lis $6
   .word 32
   sub $30, $30, $6

   lis $3   ; address of address of the start of the free list
   .word free
   lis $4
   .word 4
   lis $5   ; external print procedure
   .word print
   lis $6
   .word 1

   lw $2, -4($3) ; $2 = length of free list; countdown to 0 for looping
   lw $3, 0($3) ; $3 = address of the start of the free list

   ;; loop through the free list, and print each element
 topPFL:
   beq $2, $0, endPFL  ;; skip if free list empty

   lw $1, 0($3)     ; store in $1 the item to be printed
   sw $31, -4($30)
   sub $30, $30, $4
   jalr $5          ; call external print procedure
   add $30, $30, $4
   lw $31, -4($30)
   add $3, $3, $4   ; update current address and loop counter
   sub $2, $2, $6
   bne $2, $0, topPFL

 endPFL:
   ;; add an extra newline at the end, so that if this procedure is called
   ;; multiple times, we can distinguish where one call ends and the next
   ;; begins
   lis $6
   .word 0xffff000c
   lis $5
   .word 10
   sw $5, 0($6)

   lis $6
   .word 32
   add $30, $30, $6
   lw $1, -4($30)
   lw $2, -8($30)
   lw $3, -12($30)
   lw $4, -16($30)
   lw $5, -20($30)
   lw $6, -24($30)
   lw $7, -28($30)
   lw $8, -32($30)
   jr $31
end:
   .word 0 ;; beginnning of heap
   .word 0 ;; end of heap
   .word 0 ;; length of free list
free: .word 0 ;; beginning of free list

