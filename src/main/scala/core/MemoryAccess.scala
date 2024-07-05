package core

import chisel3._
import chisel3.util._
import peripheral.RAMBundle
import settings.Settings

class MemoryAccess extends Module {
  val io = IO(new Bundle() {
    val alu_result          = Input(UInt(Settings.DataWidth))
    val reg2_data           = Input(UInt(Settings.DataWidth))
    val memory_read_enable  = Input(Bool())
    val memory_write_enable = Input(Bool())
    val funct3              = Input(UInt(3.W))

    val wb_memory_read_data = Output(UInt(Settings.DataWidth))

    val memory_bundle = Flipped(new RAMBundle)
  })
  val mem_address_index = io.alu_result(log2Up(Settings.WordSize) - 1, 0).asUInt

  io.memory_bundle.write_enable := false.B
  io.memory_bundle.write_data   := 0.U
  io.memory_bundle.address      := io.alu_result
  io.memory_bundle.write_strobe := VecInit(Seq.fill(Settings.WordSize)(false.B))
  io.wb_memory_read_data        := 0.U

  when(io.memory_read_enable) {
    val data = io.memory_bundle.read_data
    io.wb_memory_read_data := MuxLookup(
      io.funct3,
      0.U
    )(
      IndexedSeq(
        InstructionsTypeL.lb -> MuxLookup(
          mem_address_index,
          Cat(Fill(24, data(31)), data(31, 24))
        )(
          IndexedSeq(
            0.U -> Cat(Fill(24, data(7)), data(7, 0)),
            1.U -> Cat(Fill(24, data(15)), data(15, 8)),
            2.U -> Cat(Fill(24, data(23)), data(23, 16))
          )
        ),
        InstructionsTypeL.lbu -> MuxLookup(
          mem_address_index,
          Cat(Fill(24, 0.U), data(31, 24))
        )(
          IndexedSeq(
            0.U -> Cat(Fill(24, 0.U), data(7, 0)),
            1.U -> Cat(Fill(24, 0.U), data(15, 8)),
            2.U -> Cat(Fill(24, 0.U), data(23, 16))
          )
        ),
        InstructionsTypeL.lh -> Mux(
          mem_address_index === 0.U,
          Cat(Fill(16, data(15)), data(15, 0)),
          Cat(Fill(16, data(31)), data(31, 16))
        ),
        InstructionsTypeL.lhu -> Mux(
          mem_address_index === 0.U,
          Cat(Fill(16, 0.U), data(15, 0)),
          Cat(Fill(16, 0.U), data(31, 16))
        ),
        InstructionsTypeL.lw -> data
      )
    )
  }.elsewhen(io.memory_write_enable) {
    io.memory_bundle.write_data   := io.reg2_data
    io.memory_bundle.write_enable := true.B
    io.memory_bundle.write_strobe := VecInit(Seq.fill(Settings.WordSize)(false.B))
    when(io.funct3 === InstructionsTypeS.sb) {
      io.memory_bundle.write_strobe(mem_address_index) := true.B
      io.memory_bundle.write_data := io.reg2_data(Settings.ByteBits, 0) << (mem_address_index << log2Up(
        Settings.ByteBits
      ).U)
    }.elsewhen(io.funct3 === InstructionsTypeS.sh) {
      when(mem_address_index === 0.U) {
        for (i <- 0 until Settings.WordSize / 2) {
          io.memory_bundle.write_strobe(i) := true.B
        }
        io.memory_bundle.write_data := io.reg2_data(Settings.WordSize / 2 * Settings.ByteBits, 0)
      }.otherwise {
        for (i <- Settings.WordSize / 2 until Settings.WordSize) {
          io.memory_bundle.write_strobe(i) := true.B
        }
        io.memory_bundle.write_data := io.reg2_data(
          Settings.WordSize / 2 * Settings.ByteBits,
          0
        ) << (Settings.WordSize / 2 * Settings.ByteBits)
      }
    }.elsewhen(io.funct3 === InstructionsTypeS.sw) {
      for (i <- 0 until Settings.WordSize) {
        io.memory_bundle.write_strobe(i) := true.B
      }
    }
  }
}
