package core
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class BasicTestSpec extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "MyModule"
  // test class body here
  it should "pass in this simple env" in {
    println("\u001B[32m\n**************\n基本环境测试通过......\n**************")
    test(new BasicModule).withAnnotations(Seq(WriteVcdAnnotation)) {
      dut => {
        step(1)
        dut.in.poke(true.B)
        step(1)
        dut.in.poke(false.B)
        
      }
    }
  }
}
