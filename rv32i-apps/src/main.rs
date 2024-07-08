#![no_std]
#![no_main]
#![feature(lang_items)]

#[lang = "eh_personality"]
extern "C" fn eh_personality() {}

use core::panic::PanicInfo;

#[panic_handler]
fn panic(_panic: &PanicInfo<'_>) -> ! {
    loop {}
}

#[no_mangle]
pub extern "C" fn _start() -> ! {
    let x = 8u32;
    let _y = fbn(x);
    loop {}
}

fn fbn(n: u32) -> u32 {
    if n == 0 || n == 1 {
        1
    } else {
        fbn(n - 1) + fbn(n - 2)
    }
}
