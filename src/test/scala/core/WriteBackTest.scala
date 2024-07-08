import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class WriteBackTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Write Basic Test"
  it should "WriteBack" in {
    test(new core.WriteBack).withAnnotations(Seq(WriteVcdAnnotation)) {
      dut => {
        dut.io.alu_result.poke(114514.U)
        dut.io.instruction_address.poke(1919.U)
        dut.io.memory_read_data.poke(417.U)
        for ( i <- 0 until 4) {
          dut.io.regs_write_source.poke(i.U)
          dut.clock.step()
        }
      }
    }
  }
}


