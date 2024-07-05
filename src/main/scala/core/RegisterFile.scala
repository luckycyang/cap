package core

import chisel3._
import settings.Settings

object Registers extends Enumeration {
  type Register = Value
  val zero, ra, sp, gp, tp, t0, t1, t2, fp, s1, a0, a1, a2, a3, a4, a5, a6, a7, s2, s3, s4, s5, s6, s7, s8, s9, s10,
      s11, t3, t4, t5, t6 = Value
}

class RegisterFile extends Module {
  val io = IO(new Bundle {
    val write_enable  = Input(Bool())
    val write_address = Input(UInt(Settings.PhysicalRegisterAddrWidth))
    val write_data    = Input(UInt(Settings.DataWidth))

    val read_address1 = Input(UInt(Settings.PhysicalRegisterAddrWidth))
    val read_address2 = Input(UInt(Settings.PhysicalRegisterAddrWidth))
    val read_data1    = Output(UInt(Settings.DataWidth))
    val read_data2    = Output(UInt(Settings.DataWidth))

    val debug_read_address = Input(UInt(Settings.PhysicalRegisterAddrWidth))
    val debug_read_data    = Output(UInt(Settings.DataWidth))
  })
  val registers = RegInit(VecInit(Seq.fill(Settings.PhysicalRegisters)(0.U(Settings.DataWidth))))

  when(!reset.asBool) {
    when(io.write_enable && io.write_address =/= 0.U) {
      registers(io.write_address) := io.write_data
    }
  }

  io.read_data1 := Mux(
    io.read_address1 === 0.U,
    0.U,
    registers(io.read_address1)
  )

  io.read_data2 := Mux(
    io.read_address2 === 0.U,
    0.U,
    registers(io.read_address2)
  )

  io.debug_read_data := Mux(
    io.debug_read_address === 0.U,
    0.U,
    registers(io.debug_read_address)
  )
}
