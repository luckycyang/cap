import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import peripheral._
import chisel3.util.Fill
import firrtl.annotations.MemoryLoadFileType
class CPUBasic extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "TopCPU Basic Test"
  it should "TopCPU" in {
    test(new top.TestTopModule("target/apps/test.hex")).withAnnotations(Seq(WriteVcdAnnotation)) {
      dut => {
         dut.io.mem_debug_read_address.poke(1000.U)
         dut.io.regs_debug_read_address.poke(11.U)
        dut.clock.step(128)
      }
    }
  }
}

