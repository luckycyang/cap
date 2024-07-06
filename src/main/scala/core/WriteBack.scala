package core

import chisel3._
import chisel3.util._
import settings.Settings

/**
  * 写回模块
  * 写回寄存器组
  */
class WriteBack extends Module {
  val io = IO(new Bundle {
    /**
      * 指令地址，指令加 机器字节位宽在此模块完成
      */
    val instruction_address = Input(UInt(Settings.AddrWidth))
    val alu_result          = Input(UInt(Settings.DataWidth))
    val memory_read_data    = Input(UInt(Settings.DataWidth))
    /**
      * 指令译码提供， 就是目标寄存器
      *
      * 0 -> 默认寄存器
      * 
      * 1 -> 写内存
      * 
      * 2 -> CSR
      *
      * 3 -> 下一条指令的地址
      */
    val regs_write_source   = Input(UInt(2.W))
    val regs_write_data     = Output(UInt(Settings.DataWidth))
  })
  // 根据写回源
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
