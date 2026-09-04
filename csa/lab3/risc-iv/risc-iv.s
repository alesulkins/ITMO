.data
.org 0x0000

in_addr:    .word 0x80
out_addr:   .word 0x84

int_max:    .word 0x7FFFFFFF
err_ovf:    .word 0xCCCCCCCC
err_neg:    .word -1

.text
.org 0x0100

_start:
    addi  sp, zero, 0x700

    lui   t0, %hi(in_addr)
    addi  t0, t0, %lo(in_addr)
    lw    t0, 0(t0)
    lw    a0, 0(t0)

    ble   a0, zero, ret_neg

    andi  t1, a0, 1
    add   t2, a0, t1
    srai  t2, t2, 1

    lui   t3, %hi(int_max)
    addi  t3, t3, %lo(int_max)
    lw    t3, 0(t3)

    div   t4, t3, t2
    bgt   t2, t4, overflow

    add   a0, t2, zero
    jal   ra, square_rec
    j     write

ret_neg:
    lui   t1, %hi(err_neg)
    addi  t1, t1, %lo(err_neg)
    lw    a0, 0(t1)
    j     write

overflow:
    lui   t1, %hi(err_ovf)
    addi  t1, t1, %lo(err_ovf)
    lw    a0, 0(t1)

write:
    lui   t0, %hi(out_addr)
    addi  t0, t0, %lo(out_addr)
    lw    t0, 0(t0)

    sw    a0, 0(t0)
    halt

square_rec:
    addi  sp, sp, -12
    sw    ra, 8(sp)

    beqz  a0, square_base

    andi  t0, a0, 1
    srai  t1, a0, 1

    sw    t0, 0(sp)
    sw    t1, 4(sp)

    add   a0, t1, zero
    jal   ra, square_rec

    slli  a0, a0, 2

    lw    t0, 0(sp)
    beqz  t0, square_end

    lw    t1, 4(sp)
    slli  t1, t1, 2
    add   a0, a0, t1
    addi  a0, a0, 1

    j     square_end

square_base:
    addi  a0, zero, 0

square_end:
    lw    ra, 8(sp)
    addi  sp, sp, 12
    jr    ra