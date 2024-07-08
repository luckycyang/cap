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
    val jump_flag_id          = Input(Bool())
    val jump_address_id       = Input(UInt(Settings.AddrWidth))
    val instruction_read_data = Input(UInt(Settings.DataWidth))
    val instruction_valid     = Input(Bool())

    val instruction_address = Output(UInt(Settings.AddrWidth))
    val instruction         = Output(UInt(Settings.InstructionWidth))
  })
  val pc = RegInit(ProgramCounter.EntryAddress)
  printf("当前的PC: %d", pc)

  when(io.instruction_valid) {
    io.instruction := io.instruction_read_data
    // lab3(InstructionFetch) begin
    pc := Mux(io.jump_flag_id, io.jump_address_id,io.instruction_address + Settings.WordSize.U)

    // lab3(InstructionFetch) end
  }.otherwise {
    pc := pc
    io.instruction := 0x0000_0013.U
  }
  io.instruction_address := pc
}
