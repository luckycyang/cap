package core
import chisel3._
import chisel3.util._
import settings.Settings

object ProgramCounter {
  def EntryAddress = Settings.EntryAddress
}

/**
  * 取指模块
  * 目前只有顺序和跳转
  * 顺序就是默认 +4， 否则就根据传入的值存入PC
  * Input:
  * - jmpFlag 跳转标记
  * - jmpAddr 跳转的地址
  * - instructionNext
  * - instructionValid PC使能位
  * - instructionAddr 指令地址
  * - instruction 取得的指令
  * - 
  */
class InstructionFetch extends Module {
  val io = IO(new Bundle {
    val jmpFlag = Input(Bool())
    val jmpAddr = Input(UInt(Settings.AddrWidth))
    val instructionNext = Input(UInt(Settings.DataWidth))
    val instructionValid = Input(Bool())
    val instructionAddr = Output(UInt(Settings.AddrWidth))
    val instruction = Output(UInt(Settings.InstructionWidth))
  })
  val pc = RegInit(ProgramCounter.EntryAddress)

  when(io.instructionValid) {
    io.instruction := io.instructionNext
    // lab3(InstructionFetch) begin
    pc := Mux(io.jmpFlag, io.jmpAddr,io.instructionAddr + Settings.WordSize.U)

    // lab3(InstructionFetch) end
  }.otherwise {
    pc := pc
    io.instruction := 0x0000_0013.U
  }
  io.instructionAddr := pc
}
