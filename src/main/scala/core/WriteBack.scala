package core

import chisel3._
import chisel3.util._
import settings.Settings

class WriteBack extends Module {
  val io = IO(new Bundle {
    val instruction_address = Input(UInt(Settings.AddrWidth))
    val alu_result          = Input(UInt(Settings.DataWidth))
    val memory_read_data    = Input(UInt(Settings.DataWidth))
    val regs_write_source   = Input(UInt(2.W))
    val regs_write_data     = Output(UInt(Settings.DataWidth))
  })
  io.regs_write_data := MuxLookup(
    io.regs_write_source,
    io.alu_result
  )(
    IndexedSeq(
      RegWriteSource.Memory                 -> io.memory_read_data,
      RegWriteSource.NextInstructionAddress -> (io.instruction_address + 4.U)
    )
  )
}
