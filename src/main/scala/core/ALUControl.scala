package core

import chisel3._
import chisel3.util._

/**
  * ALU 控制模块
  * 根据 opcode 和 funct3,funct7 给ALU提供功能字
  */
class ALUControl extends Module {
  val io = IO(new Bundle {
    val opcode = Input(UInt(7.W))
    val funct3 = Input(UInt(3.W))
    val funct7 = Input(UInt(7.W))

    val alu_funct = Output(ALUFunctions())
  })

  /**
    * 默认就是提供 zero
    */
  io.alu_funct := ALUFunctions.zero

  /**
    * 提供枚举opcode判别指令类型, 具有 funct 功能字的指令也就是R I S B 型指令需要枚举功能字段判别
    * 其中 R 型指令根据 funct3, funct7 判断
    * I,S,B型指令通过 funct3 判断
    * U, J 型指令可直接又opcode得出指令类型和指令
    */
  switch(io.opcode) {
    /**
      * I 型指令只通过 funct3 判断具体那条指令
      */
    is(InstructionTypes.I) {
      io.alu_funct := MuxLookup(
        io.funct3,
        ALUFunctions.zero
      )(
        IndexedSeq(
          InstructionsTypeI.addi  -> ALUFunctions.add,
          InstructionsTypeI.slli  -> ALUFunctions.sll,
          InstructionsTypeI.slti  -> ALUFunctions.slt,
          InstructionsTypeI.sltiu -> ALUFunctions.sltu,
          InstructionsTypeI.xori  -> ALUFunctions.xor,
          InstructionsTypeI.ori   -> ALUFunctions.or,
          InstructionsTypeI.andi  -> ALUFunctions.and,
          InstructionsTypeI.sri   -> Mux(io.funct7(5), ALUFunctions.sra, ALUFunctions.srl)
        ),
      )
    }
    is(InstructionTypes.RM) {
      io.alu_funct := MuxLookup(
        io.funct3,
        ALUFunctions.zero
      )(
        IndexedSeq(
          InstructionsTypeR.add_sub -> Mux(io.funct7(5), ALUFunctions.sub, ALUFunctions.add),
          InstructionsTypeR.sll     -> ALUFunctions.sll,
          InstructionsTypeR.slt     -> ALUFunctions.slt,
          InstructionsTypeR.sltu    -> ALUFunctions.sltu,
          InstructionsTypeR.xor     -> ALUFunctions.xor,
          InstructionsTypeR.or      -> ALUFunctions.or,
          InstructionsTypeR.and     -> ALUFunctions.and,
          InstructionsTypeR.sr      -> Mux(io.funct7(5), ALUFunctions.sra, ALUFunctions.srl)
        ),
      )
    }
    is(InstructionTypes.B) {
      io.alu_funct := ALUFunctions.add
    }
    is(InstructionTypes.L) {
      io.alu_funct := ALUFunctions.add
    }
    is(InstructionTypes.S) {
      io.alu_funct := ALUFunctions.add
    }
    is(Instructions.jal) {
      io.alu_funct := ALUFunctions.add
    }
    is(Instructions.jalr) {
      io.alu_funct := ALUFunctions.add
    }
    is(Instructions.lui) {
      io.alu_funct := ALUFunctions.add
    }
    is(Instructions.auipc) {
      io.alu_funct := ALUFunctions.add
    }
  }
}
