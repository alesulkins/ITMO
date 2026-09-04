.data
in_port_ptr:    .word 0x80
out_port_ptr:   .word 0x84
fnv32_prime:    .word 0x01000193
fnv32_offset:   .word 0x811C9DC5

.text
.org 0x88
_start:
    lui t0, %hi(in_port_ptr)      / lui t1, %hi(out_port_ptr)      / nop          / nop
    addi t0, t0, %lo(in_port_ptr) / addi t1, t1, %lo(out_port_ptr) / nop          / nop
    lui t2, %hi(fnv32_prime)      / lui s1, %hi(fnv32_offset)      / lw t0, 0(t0) / nop
    addi t2, t2, %lo(fnv32_prime) / addi s1, s1, %lo(fnv32_offset) / lw t1, 0(t1) / nop
    nop                           / nop                            / lw t2, 0(t2) / nop
    nop                           / nop                            / lw s1, 0(s1) / nop
    nop                           / nop                            / lw t4, 0(t0) / nop

loop:
    mul t5, s1, t2                / nop                            / nop          / beqz t4, end
    xor s1, t5, t4                / nop                            / lw t6, 0(t0) / nop
    mv t4, t6                     / nop                            / nop          / j loop

end:
    nop                           / nop                            / sw s1, 0(t1) / nop
    nop                           / nop                            / nop          / halt