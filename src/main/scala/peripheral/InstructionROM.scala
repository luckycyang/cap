package peripheral


import chisel3._
import settings.Settings
import scala.io.Source
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemoryLoadFileType
class InstructionROM(instructionFilename: String, hexOrBinary: MemoryLoadFileType.FileType = MemoryLoadFileType.Hex) extends Module {
  val io = IO(new Bundle {
    val address = Input(UInt(Settings.AddrWidth))
    val data    = Output(UInt(Settings.InstructionWidth))
  })
    // val capacity = 8096
  val capacity = countLines(instructionFilename)
  // printf("指令数量: %d\n", capacity.U)
  val mem = Mem(capacity, UInt(Settings.InstructionWidth))
  io.data := mem(io.address)

  loadMemoryFromFileInline(mem, instructionFilename, hexOrBinary)

    def countLines(filePath: String): Int = {
    val source = Source.fromFile(filePath)
    try {
      source.getLines.size
    } finally {
      source.close()
    }
  }
}

