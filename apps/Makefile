CROSS_COMPILE ?= riscv64-elf-

CC := $(CROSS_COMPILE)gcc -c -nostdlib -march=rv32i -mabi=ilp32
OBJCOPY := $(CROSS_COMPILE)objcopy


%.o: %.c
	$(CC) -o $@ $<

%.bin: %.o
	$(OBJCOPY) -O binary -j .text $< $@


bins = fib.bin \


all: $(bins)

clean:
	@rm -rf *.o *.bin
