CROSS_COMPILE=riscv64-elf-
CC=$(CROSS_COMPILE)gcc
CFLAGS=-c -nostdlib -march=rv32i -mabi=ilp32
OBJCOPY=$(CROSS_COMPILE)objcopy
OBJCOPYFLAGS=-O binary -j .text
LD=$(CROSS_COMPILE)ld
LDFLAGS=-melf32lriscv

%.o: %.s
	$(CC) $(CFLAGS) -o $@ $<

%.elf: %.c init.o
	$(CC) $(CFLAGS) -c -o $(@:.elf=.o) $<
	$(CROSS_COMPILE)ld -o $@  $(LDFLAGS) init.o $(@:.elf=.o) 
%.bin: %.elf
	$(OBJCOPY) $(OBJCOPYFLAGS)  $< $@

BINS = \
	fib.bin





bins = fib.bin
all: $(bins)
clean:
	rm -f *.o
	rm -f *.bin
	rm -f *.asm *.hex
