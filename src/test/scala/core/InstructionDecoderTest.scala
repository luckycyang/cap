import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import core.InstructionDecode
import core.{ALUOp1Source, ALUOp2Source}
import core.InstructionTypes
import settings.Settings.InstructionWidth



class InstructionDecoderTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior.of("InstructionDecoder of Single Cycle CPU")
  it should "produce correct control signal" in {
    test(new InstructionDecode).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
      // U
      // lui x7, 114514
      c.io.instruction.poke(0x1bf523b7.U)
      c.clock.step(1)

      
      // R
      // add x27, x0,x7
      c.io.instruction.poke(0x00700db3.U)
      c.clock.step(1)
      // sub x20, x7, x9
      c.io.instruction.poke(0x40938a33.U)
      c.clock.step(1)

      // I
      // lh x15, 114(x0)
      c.io.instruction.poke(0x07201783.U)
      c.clock.step(1)
      
      // S
      // sw x7, 4(x0)
      c.io.instruction.poke(0x00702223.U) // S-type
      c.io.ex_aluop1_source.expect(ALUOp1Source.Register)
      c.io.ex_aluop2_source.expect(ALUOp2Source.Immediate)
      c.io.regs_reg1_read_address.expect(0.U)
      c.io.regs_reg2_read_address.expect(7.U)
      c.clock.step()

      // B
      // beq x0, x7, 14
      c.io.instruction.poke(0x06700963.U)
      c.clock.step(1)

      // J
      // jal x31, 114
      c.io.instruction.poke(0x07200fef.U)
      c.clock.step(1)

     }
  }

}
