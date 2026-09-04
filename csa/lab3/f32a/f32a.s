.data
.org 0x100

input_addr:                  .word 0x80
output_addr:                 .word 0x84
zero_const:                  .word 0
one_const:                   .word 1
result_if_zero:              .word 32

value:                       .word 0
trailing_zeros:              .word 0

.text

_start:
    @p input_addr
    a!
    @
    dup
    !p value
    if zero_case

    @p zero_const
    !p trailing_zeros

loop:
    @p value
    dup
    @p one_const
    and
    if even_case

    drop
    @p trailing_zeros
    @p output_addr
    a!
    !
    end ;

even_case:
    drop
    @p value
    2/
    !p value

    @p trailing_zeros
    @p one_const
    +
    !p trailing_zeros

    loop ;

zero_case:
    @p result_if_zero
    @p output_addr
    a!
    !

end:
    halt