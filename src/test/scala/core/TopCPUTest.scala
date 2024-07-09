import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import peripheral._
import chisel3.util.Fill
import firrtl.annotations.MemoryLoadFileType
class CPUBasic extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "TopCPU Basic Test"
  it should "TopCPU" in {
    test(new top.TestTopModule("apps/fib.hex")).withAnnotations(Seq(WriteVcdAnnotation)) {
      dut => {
        dut.clock.step(1)
        dut.reset.poke(true.B)
        dut.clock.step()
        dut.reset.poke(false.B)
        dut.io.mem_debug_read_address.poke(0x1504.U)
        dut.clock.step(1024)
      }
    }
  }
}

