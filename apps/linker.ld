 MEMORY
{
  FLASH (rx) : ORIGIN = 0x00001000, LENGTH = 1K
  RAM (rx) : ORIGIN = 0x00000000, LENGTH = 4K
}
 
SECTIONS
{
 .text :
 {
 KEEP(*(.isr_vector))
 *(.text)
 } >FLASH
}
