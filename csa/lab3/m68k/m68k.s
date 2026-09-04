.data
.org 0x00
result_buf: .byte '________________________________________________________________'
input_buf: .byte 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
word_store: .byte 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
count_store: .byte 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

.text

_start:
    movea.l 0x0F00, A7
    movea.l 0x88, A4
    movea.l 0xB8, A5

    jsr proc_read               
    cmp.l 0, D0
    bne err_overflow

    jsr proc_parse              
    cmp.l 0, D0
    bne err_domain

    jsr proc_build              
    jsr proc_output             
    halt

proc_read:
    movea.l 0x80, A0
    movea.l 0x40, A1
    clr.l D7

proc_read__loop:
    cmp.l 0x40, D7
    bge proc_read__overflow
    clr.l D0
    move.l (A0), D0
    cmp.l 10, D0
    beq proc_read__ok
    move.b D0, (A1)+
    add.l 1, D7
    jmp proc_read__loop

proc_read__overflow:
    move.l 1, D0
    rts

proc_read__ok:
    clr.l D0
    move.b D0, (A1)
    clr.l D0
    rts

proc_parse:
    movea.l 0x40, A0
    clr.l D1
    clr.l D2
    clr.l D3

proc_parse__loop:
    clr.l D0
    move.b (A0)+, D0
    beq proc_parse__last

    cmp.l 32, D0
    beq proc_parse__sep
    cmp.l 44, D0
    beq proc_parse__sep
    cmp.l 46, D0
    beq proc_parse__sep

    add.l 1, D1
    cmp.l 4, D1
    bge proc_parse__err_long

    move.l D1, D5
    sub.l 1, D5
    asl.l 3, D5
    asl.l D5, D0
    or.l D0, D3
    jmp proc_parse__loop

proc_parse__sep:
    cmp.l 0, D1
    beq proc_parse__loop
    jsr proc_word
    cmp.l 0, D0
    bne proc_parse__err_many
    clr.l D3
    clr.l D1
    jmp proc_parse__loop

proc_parse__last:
    cmp.l 0, D1
    beq proc_parse__ok
    jsr proc_word
    cmp.l 0, D0
    bne proc_parse__err_many

proc_parse__ok:
    clr.l D0
    rts

proc_parse__err_long:
    move.l 1, D0
    rts

proc_parse__err_many:
    move.l 2, D0
    rts

proc_word:
    move.l D4, -(A7)
    move.l D5, -(A7)
    move.l D6, -(A7)

    clr.l D4

proc_word__search:
    cmp.l D2, D4
    bge proc_word__new
    move.l D4, D5
    asl.l 2, D5
    move.l 0(A4,D5), D6
    cmp.l D3, D6
    beq proc_word__found
    add.l 1, D4
    jmp proc_word__search

proc_word__found:
    move.l D4, D5
    asl.l 2, D5
    move.l 0(A5,D5), D6
    add.l 1, D6
    move.l D6, 0(A5,D5)
    clr.l D0
    jmp proc_word__exit

proc_word__new:
    cmp.l 12, D2
    bge proc_word__err
    move.l D2, D5
    asl.l 2, D5
    move.l D3, 0(A4,D5)
    move.l 1, D6
    move.l D6, 0(A5,D5)
    add.l 1, D2
    clr.l D0
    jmp proc_word__exit

proc_word__err:
    move.l 1, D0

proc_word__exit:
    move.l (A7)+, D6
    move.l (A7)+, D5
    move.l (A7)+, D4
    rts

proc_build:
    movea.l 0x00, A3
    clr.l D4

proc_build__loop:
    cmp.l D2, D4
    bge proc_build__done

    cmp.l 0, D4
    beq proc_build__no_sep
    move.l 32, D0
    move.b D0, (A3)+

proc_build__no_sep:
    move.l D4, D5
    asl.l 2, D5
    move.l 0(A5,D5), D0
    jsr proc_tostr

    add.l 1, D4
    jmp proc_build__loop

proc_build__done:
    clr.l D0
    move.b D0, (A3)
    rts

proc_tostr:
    link A6, -4
    move.l D0, -4(A6)

    cmp.l 10, D0
    blt proc_tostr__one

proc_tostr__two:
    move.l 10, D1
    div.l D1, D0
    add.l 48, D0
    move.b D0, (A3)+
    sub.l 48, D0
    move.l 10, D1
    mul.l D1, D0
    move.l -4(A6), D1
    sub.l D0, D1
    add.l 48, D1
    move.b D1, (A3)+
    jmp proc_tostr__exit

proc_tostr__one:
    add.l 48, D0
    move.b D0, (A3)+

proc_tostr__exit:
    unlk A6
    rts

proc_output:
    movea.l 0x00, A0
    movea.l 0x84, A1

proc_output__loop:
    clr.l D0
    move.b (A0)+, D0
    beq proc_output__done
    move.l D0, (A1)
    jmp proc_output__loop

proc_output__done:
    rts

err_domain:
    movea.l 0x84, A1
    move.l 0xFFFFFFFF, D0      ; -1 as 32-bit machine word
    move.l D0, (A1)
    halt

err_overflow:
    movea.l 0x84, A1
    move.l 0xCCCCCCCC, D0     ; overflow sentinel
    move.l D0, (A1)
    halt
