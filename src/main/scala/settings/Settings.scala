package settings

import chisel3._
import chisel3.util._

/**
  * 记录设计的参数
  */
object Settings {
  /**
    * 地址比特
    *
    * @return
    */
  def AddrBits = 32 // 地址字长
  /**
    * 地址字长
    *
    * @return
    */
  def AddrWidth = AddrBits.W // 机器字长

  /**
    * 指令比特
    *
    * @return
    */
  def InstructionBits = 32
  /**
    * 指令字长
    *
    * @return
    */
  def InstructionWidth = InstructionBits.W
  /**
    * 数据比特
    *
    * @return
    */
  def DataBits = 32
  /**
    * 数据字长
    *
    * @return
    */
  def DataWidth = DataBits.W
  /**
    * 字节比特
    *
    * @return
    */
  def ByteBits = 8
  /**
    * 字节字长
    *
    * @return
    */
  def ByteWidth = ByteBits.W
  /**
    * 字节颗粒度
    * 例如 32 位机的颗粒度是 4, 64 位的颗粒度是 8
    * 因为储存器一般的一颗粒是 8Bits = 1Byte
    * 4 颗粒的寻址通常是 (Addr >> 2) 表示取 00 01 10 11 四个 Byte为一个数据字
    * @return 
    */
  def WordSize = Math.ceil(DataBits / ByteBits).toInt

  /**
    * RISCV 就是32个寄存器
    */
  def PhysicalRegisters = 32
  /**
    * 寄存器地址比特
    *
    * @return
    */
  def PhysicalRegisterAddrBits = log2Ceil(PhysicalRegisters)
  /**
    * 对寄存器数的地址线计算 | log2(PhysicalRegisters) |
    * 例如 16 个寄存器的地址位宽就是4, 17 个的地址线位宽就是 5
    * @return
    */
  def PhysicalRegisterAddrWidth = PhysicalRegisterAddrBits.W

  /**
    * RISCV 的控制和状态寄存器
    * 作为 RISCV 特权级的表示
    * @return
    */
  def CSRRegisterAddrBits = 12
  /**
    * CSR寄存器地址字长
    *
    * @return
    */
  def CSRRegisterAddrWidth = CSRRegisterAddrBits.W

  /**
    * 中断标志位比特
    *
    * @return
    */
  def InterruptFlagBits = 32
  /**
    * 中断标志字长 - 直接表示
    *
    * @return
    */
  def InterruptFlagWidth = InstructionBits.W

  /**
    * 保存状态
    * 目前没几把用
    * @return
    */
  def HoldStateBits = 32
  /**
    * 保存状态字长 - 直接表示
    *
    * @return
    */
  def StallStateWidth = HoldStateBits.W

  /**
    * 内存空间
    * 目前是 32768 * Byte = 2^5KB = 32KB = 8KW
    * @return
    */
  def MemorySizeInBytes = 32768
  /**
    * 内存地址空间
    *
    * @return
    */
  def MemorySizeInWords = MemorySizeInBytes / 4

  /**
    * 入口地址
    * 机器复位时PC 所指向的地址
    * 通常来说应该是0x0 offset WordSize + BaseAddr
    * BaseAddr 是存储器的偏移地址
    * 开始需要放置中断向量表
    * 0x0 开始处应该程序的堆栈大小
    * 0x1 开始才是复位程序入口
    * @return
    */
  def EntryAddress = 0x00001000.U(Settings.AddrWidth)

  /**
    * 设备数量
    * 一个主机 8个丛机
    * 就是 IO 外设
    * @return
    */
  def MasterDeviceCount = 1
  /**
    * 从机数量
    *
    * @return
    */
  def SlaveDeviceCount = 8
  /**
    * 从机控制比特 - 编码表示
    *
    * @return
    */
  def SlaveDeviceCountBits = log2Ceil(Settings.SlaveDeviceCount)
  

  

  
}
