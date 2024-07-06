package core

import chisel3._
import chisel3.util._
import peripheral.RAMBundle
import settings.Settings

/**
  * 内存控制模块
  *
  * 目的是解耦CPU和外部设备连接 
  *
  * CPU 访存接口， 例如我们使用了自定义的 RamBundle, 根据实际上IO总线即可， 例如果壳处理机使用 AXI4
  *
  * write_enable 和 write_strobe[3..0] 共同决定是否写内存
  *
  * 写掩码干嘛的， 我们这里是32位机， 内存对齐是4字节，但是对于 i16, u8 类型实际不用写满32Bits, 对于 u16, 只要 write_strobe = "b0011".U 即可
  * 
  * 掩码需要根据低2位选择, 对于 Byte
  * 
  * 0x01 表示偏移 1 字节写 [15..8] 位
  */
class MemoryAccess extends Module {
  val io = IO(new Bundle() {
    /**
      * ALU 计算结果, 一般为写内存地址
      */
    val alu_result          = Input(UInt(Settings.DataWidth))
    /**
      * 寄存器数据输入
      */
    val reg2_data           = Input(UInt(Settings.DataWidth))
    /**
      * 内存读使能
      */
    val memory_read_enable  = Input(Bool())
    /**
      * 内存写使能
      */
    val memory_write_enable = Input(Bool())
    /**
      * 功能字 - 判断内存写掩码， 目的防止冲突
      */
    val funct3              = Input(UInt(3.W))

    /**
      * 写回数据 - 给 `WriteBack` 模块
      */
    val wb_memory_read_data = Output(UInt(Settings.DataWidth))

    /**
      * 内存 Bundle
      *
      */
    val memory_bundle = Flipped(new RAMBundle)
  })
  // 32 位机 就是 alu_result[3..0]
  val mem_address_index = io.alu_result(log2Up(Settings.WordSize) - 1, 0).asUInt

  // 默认写失能
  io.memory_bundle.write_enable := false.B
  // 默认数据 0.U(DataWidth)
  io.memory_bundle.write_data   := 0.U
  // 默认写地址 = alu_result
  io.memory_bundle.address      := io.alu_result
  // 掩码失效， false对应写无效
  io.memory_bundle.write_strobe := VecInit(Seq.fill(Settings.WordSize)(false.B))
  io.wb_memory_read_data        := 0.U

  // 开读
  when(io.memory_read_enable) {
    // 暂存读到的数据
    val data = io.memory_bundle.read_data
    // 根据功能字选择写回的数据, 默认写回 0.U
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
  }.elsewhen(io.memory_write_enable) { // 开写
    io.memory_bundle.write_data   := io.reg2_data
    io.memory_bundle.write_enable := true.B
    io.memory_bundle.write_strobe := VecInit(Seq.fill(Settings.WordSize)(false.B))
    when(io.funct3 === InstructionsTypeS.sb) {
      // 一个 Byte 对应开 0 
      io.memory_bundle.write_strobe(mem_address_index) := true.B
      io.memory_bundle.write_data := io.reg2_data(Settings.ByteBits, 0) << (mem_address_index << log2Up(
        Settings.ByteBits
      ).U)
    }.elsewhen(io.funct3 === InstructionsTypeS.sh) {
      // halfword
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
      // sw 存字指令, 全开
      for (i <- 0 until Settings.WordSize) {
        io.memory_bundle.write_strobe(i) := true.B
      }
    }
  }
}
