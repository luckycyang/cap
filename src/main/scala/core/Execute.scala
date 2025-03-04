package core

import chisel3._
import chisel3.util.Cat
import chisel3.util.MuxLookup
import settings.Settings

/**
  * 执行模块，为组合逻辑
  *
  */
class Execute extends Module {
  val io = IO(new Bundle {
    val instruction         = Input(UInt(Settings.InstructionWidth))
    val instruction_address = Input(UInt(Settings.AddrWidth))
    val reg1_data           = Input(UInt(Settings.DataWidth))
    val reg2_data           = Input(UInt(Settings.DataWidth))
    val immediate           = Input(UInt(Settings.DataWidth))
    val aluop1_source       = Input(UInt(1.W))
    val aluop2_source       = Input(UInt(1.W))

    val mem_alu_result  = Output(UInt(Settings.DataWidth))
    val if_jump_flag    = Output(Bool())
    val if_jump_address = Output(UInt(Settings.DataWidth))
  })

  val opcode = io.instruction(6, 0)
  val funct3 = io.instruction(14, 12)
  val funct7 = io.instruction(31, 25)
  val rd     = io.instruction(11, 7)
  val uimm   = io.instruction(19, 15)

  val alu      = Module(new ALU)
  val alu_ctrl = Module(new ALUControl)

  alu_ctrl.io.opcode := opcode
  alu_ctrl.io.funct3 := funct3
  alu_ctrl.io.funct7 := funct7

  // lab3(Execute) begin
  alu.io.func <> alu_ctrl.io.alu_funct
  alu.io.op1 := Mux(io.aluop1_source === 0.U, io.reg1_data, io.instruction_address)
  alu.io.op2 := Mux(io.aluop2_source === 0.U, io.reg2_data, io.immediate)
  // lab3(Execute) end

  io.mem_alu_result := alu.io.result
  io.if_jump_flag := opcode === Instructions.jal ||
    (opcode === Instructions.jalr) ||
    (opcode === InstructionTypes.B) && MuxLookup(
      funct3,
      false.B
    )(
      IndexedSeq(
        InstructionsTypeB.beq  -> (io.reg1_data === io.reg2_data),
        InstructionsTypeB.bne  -> (io.reg1_data =/= io.reg2_data),
        InstructionsTypeB.blt  -> (io.reg1_data.asSInt < io.reg2_data.asSInt),
        InstructionsTypeB.bge  -> (io.reg1_data.asSInt >= io.reg2_data.asSInt),
        InstructionsTypeB.bltu -> (io.reg1_data.asUInt < io.reg2_data.asUInt),
        InstructionsTypeB.bgeu -> (io.reg1_data.asUInt >= io.reg2_data.asUInt)
      )
    )
  io.if_jump_address := io.immediate + Mux(opcode === Instructions.jalr, io.reg1_data, io.instruction_address)
}
