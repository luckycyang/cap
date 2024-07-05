package cpu

import chisel3._
import peripheral.RAMBundle
import settings.Settings

// CPUBundle serves as the communication interface for data exchange between
// the CPU and peripheral devices, such as memory.
class CPUBundle extends Bundle {
  val instruction_address = Output(UInt(Settings.AddrWidth))
  val instruction         = Input(UInt(Settings.DataWidth))
  val memory_bundle       = Flipped(new RAMBundle)
  val instruction_valid   = Input(Bool())
  val deviceSelect        = Output(UInt(Settings.SlaveDeviceCountBits.W))
  val debug_read_address  = Input(UInt(Settings.PhysicalRegisterAddrWidth))
  val debug_read_data     = Output(UInt(Settings.DataWidth))
}
