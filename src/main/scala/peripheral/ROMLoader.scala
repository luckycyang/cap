package peripheral

import chisel3._
import settings.Settings

class ROMLoader(capacity: Int) extends Module {
  val io = IO(new Bundle {
    val bundle = Flipped(new RAMBundle)

    val rom_address = Output(UInt(Settings.AddrWidth))
    val rom_data    = Input(UInt(Settings.InstructionWidth))

    val load_address  = Input(UInt(Settings.AddrWidth))
    val load_finished = Output(Bool())
  })

  val address = RegInit(0.U(32.W))
  val valid   = RegInit(false.B)

  io.bundle.write_strobe := VecInit(Seq.fill(Settings.WordSize)(false.B))
  io.bundle.address      := 0.U
  io.bundle.write_data   := 0.U
  io.bundle.write_enable := false.B
  when(address <= (capacity - 1).U) {
    io.bundle.write_enable := true.B
    io.bundle.write_data   := io.rom_data
    io.bundle.address      := (address << 2.U).asUInt + io.load_address
    io.bundle.write_strobe := VecInit(Seq.fill(Settings.WordSize)(true.B))
    address                := address + 1.U
    when(address === (capacity - 1).U) {
      valid := true.B
    }
  }
  io.load_finished := valid
  io.rom_address   := address
}
