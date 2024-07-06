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
    /**
      * 写使能
      */
    val write_enable  = Input(Bool())
    /**
      * 写地址
      */
    val write_address = Input(UInt(Settings.PhysicalRegisterAddrWidth))
    /**
      * 写数据
      */
    val write_data    = Input(UInt(Settings.DataWidth))

    /**
      * 寄存器1地址
      */
    val read_address1 = Input(UInt(Settings.PhysicalRegisterAddrWidth))
    /**
      * 寄存器2地址
      */
    val read_address2 = Input(UInt(Settings.PhysicalRegisterAddrWidth))
    /**
      * 读数据1
      */
    val read_data1    = Output(UInt(Settings.DataWidth))
    /**
      * 读数据2
      */
    val read_data2    = Output(UInt(Settings.DataWidth))

    /**
      * 目前调试专用
      */
    val debug_read_address = Input(UInt(Settings.PhysicalRegisterAddrWidth))
    val debug_read_data    = Output(UInt(Settings.DataWidth))
  })
  /**
    * 寄存组
    */
  val registers = RegInit(VecInit(Seq.fill(Settings.PhysicalRegisters)(0.U(Settings.DataWidth))))

  /**
    * x0 恒为 0
    */
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
