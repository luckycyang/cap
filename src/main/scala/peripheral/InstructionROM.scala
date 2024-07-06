package peripheral


import chisel3._
import settings.Settings

class InstructionROM(instructionFilename: String) extends Module {
  val io = IO(new Bundle {
    val address = Input(UInt(Settings.AddrWidth))
    val data    = Output(UInt(Settings.InstructionWidth))
  })
  io.data := io.address
  val capacity = 128
}

