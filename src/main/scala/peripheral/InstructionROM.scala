package peripheral


import chisel3._
import settings.Settings
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemoryLoadFileType
class InstructionROM(instructionFilename: String, hexOrBinary: MemoryLoadFileType.FileType = MemoryLoadFileType.Hex) extends Module {
  val io = IO(new Bundle {
    val address = Input(UInt(Settings.AddrWidth))
    val data    = Output(UInt(Settings.InstructionWidth))
  })
  val mem = Mem(8096, UInt(Settings.InstructionWidth))
  io.data := mem(io.address)
  val capacity = 8096
  loadMemoryFromFileInline(mem, instructionFilename, hexOrBinary)
}

