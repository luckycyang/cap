#![no_std]
#![no_main]

use panic_halt as _;

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
