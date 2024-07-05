package settings

import chisel3._
import chisel3.util._

/**
  * 记录设计的参数
  */
object Settings {
  def AddrBits = 32 // 地址字长
  def AddrWidth = AddrBits.W // 机器字长

  def InstructionBits = 32
  def InstructionWidth = InstructionBits.W
  def DataBits = 32
  def DataWidth = DataBits.W
  def ByteBits = 8
  def ByteWidth = ByteBits.W
  /**
    * 存储器是以 Byte 为单元的，机器需要一次性读取 DataBits / ByteBits
    * 比如机器字长 32bit, 也就是 4Byte, 我们将内存对齐， 实际上 0x00 和 0x01 访问的地址是一致的
    * 0b00000 和0b00001 访问的是 0b000 * 32bit = 0b00 * 4Byte, 低二位丢弃
    *
    * @return 
    */
  def WordSize = Math.ceil(DataBits / ByteBits).toInt

  /**
    * RISCV 就是32个寄存器
    */
  def PhysicalRegisters = 32
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
    * 直接表示
    *
    * @return
    */
  def CSRRegisterAddrWidth = CSRRegisterAddrBits.W

  /**
    * 中断标志位
    *
    * @return
    */
  def InterruptFlagBits = 32
  def InterruptFlagWidth = InstructionBits.W

  /**
    * 保存状态
    * 目前没几把用
    * @return
    */
  def HoldStateBits = 32
  def StallStateWidth = HoldStateBits.W

  /**
    * 内存空间
    * 目前是 32768 * Byte = 2^5KB = 32KB = 8KW
    * @return
    */
  def MemorySizeInBytes = 32768
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
  def EntryAddress = 0x1000.U(Settings.AddrWidth)

  /**
    * 设备数量
    * 一个主机 8个丛机
    * 就是 IO 外设
    * @return
    */
  def MasterDeviceCount = 1
  def SlaveDeviceCount = 8
  def SlaveDeviceCountBits = log2Ceil(Settings.SlaveDeviceCount)
  

  

  
}
