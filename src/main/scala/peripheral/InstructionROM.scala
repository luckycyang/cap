package peripheral


import chisel3._
import settings.Settings
import chisel3.util.experimental.loadMemoryFromFileInline

class InstructionROM(instructionFilename: String) extends Module {
  val io = IO(new Bundle {
    val address = Input(UInt(Settings.AddrWidth))
    val data    = Output(UInt(Settings.InstructionWidth))
  })
  val mem = Mem(1024, UInt(Settings.InstructionWidth))
  io.data := mem(io.address)
  val capacity = 1024
  loadMemoryFromFileInline(mem, instructionFilename)
}

