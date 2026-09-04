.data
.org 0x00

buffer:               .byte '________________________________', 0, 0, 0

.org 0x90

write_point:          .word 0
count:                .word 0
name_len:             .word 0
name_chars_left:      .word 0
curr_char:            .word 0

const_1:              .word 1
const_8:              .word 8
const_FF:             .word 0xFF
const_newline:        .word 10
keep_high_bytes_m:    .word 0xFFFFFF00

question:             .byte 'What is your name?\n\0'
prefix:               .byte 'Hello, \0'

.text
.org 0x100

_start:
    load_imm question
    store name_len

print_question: 
    load name_len
    load_acc
    and const_FF
    beqz init_prefix
    store_addr 0x84

    load name_len
    add const_1
    store name_len
    jmp print_question

init_prefix:
    load_imm buffer
    add const_1
    store write_point

    load_imm prefix
    store name_len

copy_prefix:
    load name_len
    load_acc
    and const_FF
    beqz prepare_input
    store_ind write_point

    load name_len
    add const_1
    store name_len

    load write_point
    add const_1
    store write_point
    jmp copy_prefix

prepare_input:
    load_imm 0
    store name_len

    load_imm 22
    store name_chars_left

read_name:
    load_addr 0x80
    and const_FF
    store curr_char

    load curr_char
    sub const_newline
    beqz finish_name

    load name_chars_left
    beqz overflow_exit

    load curr_char
    store_ind write_point

    load write_point
    add const_1
    store write_point

    load name_len
    add const_1
    store name_len

    load name_chars_left
    sub const_1
    store name_chars_left

    jmp read_name

finish_name:
    load name_len
    beqz overflow_exit

    load name_len
    add const_8
    store count

    load_imm 33
    store_ind write_point

    load write_point
    add const_1
    store write_point

    load name_chars_left
    add const_1
    store name_len

fill_tail:
    load name_len
    beqz write_length_byte

    load_imm 95
    store_ind write_point

    load write_point
    add const_1
    store write_point

    load name_len
    sub const_1
    store name_len
    jmp fill_tail

write_length_byte:
    load_imm buffer
    load_acc
    and keep_high_bytes_m
    or count
    store_addr buffer

    load_imm buffer
    add const_1
    store write_point

output_greet:
    load count
    beqz end

    load write_point
    load_acc
    and const_FF
    store_addr 0x84

    load write_point
    add const_1
    store write_point

    load count
    sub const_1
    store count
    jmp output_greet

overflow_exit:
    load_imm 0xCCCCCCCC
    store_addr 0x84

end:
    halt