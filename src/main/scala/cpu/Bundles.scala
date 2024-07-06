package cpu

import chisel3._
import peripheral.RAMBundle
import settings.Settings

/**
  * CPU 线路
  * 这里需要外接 ROM 和RAM, 其中指令有效位由外部控制器控制
  * debug 是用于测试的字段线路，独立与控制信号，随时去除
  */
class CPUBundle extends Bundle {
  val instruction_address = Output(UInt(Settings.AddrWidth))
  val instruction         = Input(UInt(Settings.DataWidth))
  val memory_bundle       = Flipped(new RAMBundle)
  val instruction_valid   = Input(Bool())
  val deviceSelect        = Output(UInt(Settings.SlaveDeviceCountBits.W))
  val debug_read_address  = Input(UInt(Settings.PhysicalRegisterAddrWidth))
  val debug_read_data     = Output(UInt(Settings.DataWidth))
}
