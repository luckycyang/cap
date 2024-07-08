// mycpu is freely redistributable under the MIT License. See the file
// "LICENSE" for information on usage and redistribution of this file.

package riscv.singlecycle

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import core.ALUOp1Source
import core.ALUOp2Source
import core.Execute
import core.InstructionTypes

class ExecuteTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior.of("Execution of Single Cycle CPU")
  it should "execute correctly" in {
    test(new Execute).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
      // beq test
      c.io.instruction.poke(0x00208163L.U) // pc + 2 if x1 === x2
      c.io.instruction_address.poke(2.U)
      c.io.immediate.poke(2.U)
      c.io.aluop1_source.poke(1.U)
      c.io.aluop2_source.poke(1.U)
      c.clock.step()

      // equ
      c.io.reg1_data.poke(9.U)
      c.io.reg2_data.poke(9.U)
      c.clock.step()
      c.io.if_jump_flag.expect(1.U)
      c.io.if_jump_address.expect(4.U)

      // not equ
      c.io.reg1_data.poke(9.U)
      c.io.reg2_data.poke(19.U)
      c.clock.step()
      c.io.if_jump_flag.expect(0.U)
      c.io.if_jump_address.expect(4.U)

      c.io.aluop1_source.poke(0.U)
      c.io.aluop2_source.poke(0.U)

      // add
      c.io.instruction.poke(0x00000033.U)
      c.io.reg1_data.poke(114)
      c.io.reg2_data.poke(514)
      c.clock.step(1)
      // sub
      c.io.instruction.poke(0x40000033.U)
      c.clock.step(1)
      // xor
      c.io.instruction.poke(0x00004033.U)
      c.io.reg1_data.poke(0.U)
      c.io.reg2_data.poke(1.U)
      c.clock.step(1)

    }
  }
}
