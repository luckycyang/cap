import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import core.InstructionFetch
import scala.util.Random
import chisel3._

class InstructionFetchTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior.of("InstructionFetch of Single Cycle CPU")
  it should "fetch instruction" in {
    test(new InstructionFetch).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
      val entry = 0x1000
      var pre   = entry
      var cur   = pre
      c.io.instructionValid.poke(true.B)
      var x = 0
      for (x <- 0 to 100) {
        Random.nextInt(2) match {
          case 0 => // no jump
            cur = pre + 4
            c.io.jmpFlag.poke(false.B)
            c.clock.step()
            c.io.instructionAddr.expect(cur)
            pre = pre + 4
          case 1 => // jump
            c.io.jmpFlag.poke(true.B)
            c.io.jmpAddr.poke(entry)
            c.clock.step()
            c.io.instructionAddr.expect(entry)
            pre = entry
        }
      }

    }
  }
}
