
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import peripheral._
import chisel3.util.Fill
import firrtl.annotations.MemoryLoadFileType
class BasiCPU extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "CPU Basic Test"
  it should "CPU" in {
    test(new core.CPU).withAnnotations(Seq(WriteVcdAnnotation)) {
      dut => {
        dut.clock.step(32)
      }
    }
  }
}

