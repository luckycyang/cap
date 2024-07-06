package core

import chisel3._
import chisel3.util._
import settings.Settings

/**
  * Alu 功能字的枚举
  */
object ALUFunctions extends ChiselEnum {
  val zero, add, sub, sll, slt, xor, or, and, srl, sra, sltu = Value
}


class ALU extends Module {
  val io = IO(new Bundle {
    val func = Input(ALUFunctions())

    val op1 = Input(UInt(Settings.DataWidth))
    val op2 = Input(UInt(Settings.DataWidth))

    val result = Output(UInt(Settings.DataWidth))
  })

  io.result := 0.U
  switch(io.func) {
    is(ALUFunctions.add) {
      io.result := io.op1 + io.op2
    }
    is(ALUFunctions.sub) {
      io.result := io.op1 - io.op2
    }
    is(ALUFunctions.sll) {
      io.result := io.op1 << io.op2(4, 0)
    }
    is(ALUFunctions.slt) {
      io.result := io.op1.asSInt < io.op2.asSInt
    }
    is(ALUFunctions.xor) {
      io.result := io.op1 ^ io.op2
    }
    is(ALUFunctions.or) {
      io.result := io.op1 | io.op2
    }
    is(ALUFunctions.and) {
      io.result := io.op1 & io.op2
    }
    is(ALUFunctions.srl) {
      io.result := io.op1 >> io.op2(4, 0)
    }
    is(ALUFunctions.sra) {
      io.result := (io.op1.asSInt >> io.op2(4, 0)).asUInt
    }
    is(ALUFunctions.sltu) {
      io.result := io.op1 < io.op2
    }
  }

}
