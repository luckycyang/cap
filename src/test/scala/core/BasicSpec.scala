package core
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class BasicTestSpec extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "MyModule"
  // test class body here
  it should "pass in this simple env" in {
    test(new OperationTestSpec).withAnnotations(Seq(WriteVcdAnnotation)) {
      dut => {
        step(1)
        for (i <- Seq(4, 8); j <- Seq(8, 6)) {
          dut.in0.poke(i.U)
          dut.in1.poke(j.U)
          dut.sel.poke(true.B)
          step(1)
          dut.sel.poke(false.B)
          step(1)
        }
      }
    }
  }
  it should "generate some timing" in {
    test(new TimingGeneration).withAnnotations(Seq(WriteVcdAnnotation))  {
      dut =>
      {
        TimingGenerationTestSpec(dut)
      }
    }
  }
  
  def TimingGenerationTestSpec(dut: TimingGeneration) {
    step(1)
    for (i <- 0 until 16) {
      if (i == 6) {
        dut.reset.poke(true.B)
        step()
        dut.reset.poke(false.B)
      }
      step(1)
    }
  }
}
