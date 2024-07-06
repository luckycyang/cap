package peripheral

import chisel3._
import chisel3.util._
import settings.Settings

class RAMBundle extends Bundle {
  val address      = Input(UInt(Settings.AddrWidth))
  val write_data   = Input(UInt(Settings.DataWidth))
  val write_enable = Input(Bool())
  val write_strobe = Input(Vec(Settings.WordSize, Bool()))
  val read_data    = Output(UInt(Settings.DataWidth))
}

/**
  * 主Ram块
  *
  * @param capacity
  */
class BlockRAM(capacity: Int) extends Module {
  val io = IO(new Bundle {
    val read_address  = Input(UInt(Settings.AddrWidth))
    val write_address = Input(UInt(Settings.AddrWidth))
    val write_data    = Input(UInt(Settings.DataWidth))
    val write_enable  = Input(Bool())
    val write_strobe  = Input(Vec(Settings.WordSize, Bool()))

    val debug_read_address = Input(UInt(Settings.AddrWidth))

    val read_data       = Output(UInt(Settings.DataWidth))
    val debug_read_data = Output(UInt(Settings.DataWidth))
  })
  val mem = SyncReadMem(capacity, Vec(Settings.WordSize, UInt(Settings.ByteWidth)))
  when(io.write_enable) {
    val write_data_vec = Wire(Vec(Settings.WordSize, UInt(Settings.ByteWidth)))
    for (i <- 0 until Settings.WordSize) {
      write_data_vec(i) := io.write_data((i + 1) * Settings.ByteBits - 1, i * Settings.ByteBits)
    }
    mem.write((io.write_address >> 2.U).asUInt, write_data_vec, io.write_strobe)
  }
  io.read_data       := mem.read((io.read_address >> 2.U).asUInt, true.B).asUInt
  io.debug_read_data := mem.read((io.debug_read_address >> 2.U).asUInt, true.B).asUInt
}
/**
  * 相比 BlockRAM, 这里多了用于调试的读取线
  *
  * @param capacity
  */
class Memory(capacity: Int) extends Module {
  val io = IO(new Bundle {
    val bundle = new RAMBundle

    val instruction         = Output(UInt(Settings.DataWidth))
    val instruction_address = Input(UInt(Settings.AddrWidth))

    val debug_read_address = Input(UInt(Settings.AddrWidth))
    val debug_read_data    = Output(UInt(Settings.DataWidth))
  })

  val mem = SyncReadMem(capacity, Vec(Settings.WordSize, UInt(Settings.ByteWidth)))
  when(io.bundle.write_enable) {
    val write_data_vec = Wire(Vec(Settings.WordSize, UInt(Settings.ByteWidth)))
    for (i <- 0 until Settings.WordSize) {
      write_data_vec(i) := io.bundle.write_data((i + 1) * Settings.ByteBits - 1, i * Settings.ByteBits)
    }
    mem.write((io.bundle.address >> 2.U).asUInt, write_data_vec, io.bundle.write_strobe)
  }
  io.bundle.read_data := mem.read((io.bundle.address >> 2.U).asUInt, true.B).asUInt
  io.debug_read_data  := mem.read((io.debug_read_address >> 2.U).asUInt, true.B).asUInt
  io.instruction      := mem.read((io.instruction_address >> 2.U).asUInt, true.B).asUInt
}
