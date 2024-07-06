package peripheral

import chisel3._
import settings.Settings

// A dummy master that never initiates reads or writes
class Dummy extends Module {
  val io = IO(new Bundle {
    val bundle = Flipped(new RAMBundle)
  })
  io.bundle.write_strobe := VecInit(Seq.fill(Settings.WordSize)(false.B))
  io.bundle.write_data   := 0.U
  io.bundle.write_enable := false.B
  io.bundle.address      := 0.U
}
