import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import peripheral._ 
import chisel3.util.Fill
import firrtl.annotations.MemoryLoadFileType
class BaseTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Peripheral Test"
  it should "BlockRam" in {
    test(new BlockRAM(8192)).withAnnotations(Seq(WriteVcdAnnotation)) {
      dut => {
        // todo
        for (i <- 0 to 100 by 4) {
          dut.io.write_enable.poke(true.B)
          dut.io.write_address.poke(i.U)
          dut.io.write_data.poke(i.U)
          dut.io.write_strobe(0).poke(true.B)
          dut.io.write_strobe(1).poke(true.B)
          dut.io.write_strobe(2).poke(true.B)
          dut.io.write_strobe(3).poke(true.B)
          dut.clock.step(1)
        }
        dut.io.write_enable.poke(false.B)
        for (i <- 0 to 100 by 4) {
          dut.io.read_address.poke(i.U)
          dut.clock.step(1)
        }
      }
    }
  }

  it should "InstructionROM" in {
    // rv32i-apps/target/app
    test(new InstructionROM("target/apps/hello.hex")).withAnnotations(Seq(WriteVcdAnnotation)) {
      dut => {
        for (i <- 0 to 64) {
          dut.io.address.poke(i.U)
          step(1)
        }
      }
    }
  }

}
