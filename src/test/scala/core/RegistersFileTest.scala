
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RegistersFileTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "RegisterFiles Basic Test"
  it should "RegisterFiles" in {
    test(new core.RegisterFile).withAnnotations(Seq(WriteVcdAnnotation)) {
      dut => {
        // 调试 x7
        dut.io.debug_read_address.poke(7.U)
        dut.clock.step(1)
        // x7 <- 114514 ok
        dut.io.write_address.poke(7.U)
        dut.io.write_data.poke(114.U)
        dut.io.write_data.poke(114514.U)
        dut.io.write_enable.poke(true.B)
        dut.clock.step(1)
        // x7 <- 1919417 no ok
        dut.io.write_data.poke(1919417.U)
        dut.io.write_enable.poke(false.B)
        dut.clock.step(1)
        // reset
        dut.reset.poke(true.B)
        dut.clock.step(1)
        dut.clock.step(1)
        
      }
    }
  }
}

