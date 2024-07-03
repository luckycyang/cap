package core
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class BasicTestSpec extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "MyModule"
  // test class body here
  it should "pass in this simple env" in {
    println("\u001B[32m\n**************\n基本环境测试通过......\n**************")
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
}
