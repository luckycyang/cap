package core
import scala.collection.immutable.ArraySeq

import chisel3._
import chisel3.util._
import settings.Settings

/**
  * 指令类型
  */
object InstructionTypes {
  val L  = "b0000011".U
  val I  = "b0010011".U
  val S  = "b0100011".U
  val RM = "b0110011".U
  val B  = "b1100011".U
}

/**
  * 指令类型 + 指令， 那些由 opcode 直接判断的指令
  */
object Instructions {
  val lui   = "b0110111".U
  val nop   = "b0000001".U
  val jal   = "b1101111".U
  val jalr  = "b1100111".U
  val auipc = "b0010111".U
  val csr   = "b1110011".U
  val fence = "b0001111".U
}

/**
  * S 型指令的 Load 指令的功能字
  */
object InstructionsTypeL {
  val lb  = "b000".U
  val lh  = "b001".U
  val lw  = "b010".U
  val lbu = "b100".U
  val lhu = "b101".U
}
/**
  * I 型指令的功能字
  */
object InstructionsTypeI {
  val addi  = 0.U
  val slli  = 1.U
  val slti  = 2.U
  val sltiu = 3.U
  val xori  = 4.U
  val sri   = 5.U
  val ori   = 6.U
  val andi  = 7.U
}

/**
  * S 型指令的 Stroe 指令的功能字
  */
object InstructionsTypeS {
  val sb = "b000".U
  val sh = "b001".U
  val sw = "b010".U
}

/**
  * R 型指令的功能字
  */
object InstructionsTypeR {
  val add_sub = 0.U
  val sll     = 1.U
  val slt     = 2.U
  val sltu    = 3.U
  val xor     = 4.U
  val sr      = 5.U
  val or      = 6.U
  val and     = 7.U
}

/**
  * M 乘法指令的功能字
  */
object InstructionsTypeM {
  val mul    = 0.U
  val mulh   = 1.U
  val mulhsu = 2.U
  val mulhum = 3.U
  val div    = 4.U
  val divu   = 5.U
  val rem    = 6.U
  val remu   = 7.U
}

/**
  * B 型指令的功能字
  */
object InstructionsTypeB {
  val beq  = "b000".U
  val bne  = "b001".U
  val blt  = "b100".U
  val bge  = "b101".U
  val bltu = "b110".U
  val bgeu = "b111".U
}
/**
  * CSR 相关指令
  */
object InstructionsTypeCSR {
  val csrrw  = "b001".U
  val csrrs  = "b010".U
  val csrrc  = "b011".U
  val csrrwi = "b101".U
  val csrrsi = "b110".U
  val csrrci = "b111".U
}
/**
  * 空指令
  */
object InstructionsNop {
  val nop = 0x00000013L.U(Settings.DataWidth)
}
/**
  * 返回指令
  */
object InstructionsRet {
  val mret = 0x30200073L.U(Settings.DataWidth)
  val ret  = 0x00008067L.U(Settings.DataWidth)
}
/**
  * 自陷指令- 就是软异常
  * 1. `ecall` 一般用于 systemcall
  * 1. `ebreak` 和 `ecall` 功能一样， 一般用于 Debug
  */
object InstructionsEnv {
  val ecall  = 0x00000073L.U(Settings.DataWidth)
  val ebreak = 0x00100073L.U(Settings.DataWidth)
}

/**
  * ALU 源1 - 寄存器或者指令地址
  * 
  * - 0 -> 寄存器
  *
  * - 1 -> 指令地址 
  *
  * 指令地址用于计算跳转
  */
object ALUOp1Source {
  val Register           = 0.U(1.W)
  val InstructionAddress = 1.U(1.W)
}

/**
  * ALU 源2 - 寄存器或者立即数
  * 
  * - 0 -> 寄存器
  *
  * - 1 -> 立即数
  */
object ALUOp2Source {
  val Register  = 0.U(1.W)
  val Immediate = 1.U(1.W)
}
/**
  * 寄存器写源 - 给写回模块看的
  *
  * 0 -> ALU 计算结果
  *
  * 1 -> 内存
  *
  * 2 -> 写CSR寄存器 目前没有
  *
  * 3 -> 下一条指令地址
  */
object RegWriteSource {
  val ALUResult = 0.U(2.W)
  val Memory    = 1.U(2.W)
  // val CSR = 2.U(2.W)
  val NextInstructionAddress = 3.U(2.W)
}

/**
  * 指令译码模块
  *
  * RISCV 的指令格式分块， 要么比如对 `Addr[31..25]` 要么表示 imm, 要么就是 funct7
  * ，所以我们可以直接使用 `Cat` 函数将各段连接起来组成对应目标数
  *
  */
class InstructionDecode extends Module {
  val io = IO(new Bundle {
    /**
      * 指令输入
      */
    val instruction = Input(UInt(Settings.InstructionWidth))
    /**
      * 寄存器源1选择地址
      */
    val regs_reg1_read_address = Output(UInt(Settings.PhysicalRegisterAddrWidth))
    /**
      * 寄存器源2选择地址
      */
    val regs_reg2_read_address = Output(UInt(Settings.PhysicalRegisterAddrWidth))
    /**
      * 立即数
      */
    val ex_immediate           = Output(UInt(Settings.DataWidth))
    /**
      * ALU_Op1 源
      */
    val ex_aluop1_source       = Output(UInt(1.W))
    /**
      * ALU_Op2 源
      */
    val ex_aluop2_source       = Output(UInt(1.W))
    /**
      * 内存读使能
      */
    val memory_read_enable     = Output(Bool())
    /**
      * 内存写使能
      */
    val memory_write_enable    = Output(Bool())
    /**
      * 写回源
      *
      * 看 `WriteBack` 模块
      */
    val wb_reg_write_source    = Output(UInt(2.W))
    /**
      * 寄存器写使能
      */
    val reg_write_enable       = Output(Bool())
    /**
      * 寄存器写地址
      *
      * 指目标寄存器
      */
    val reg_write_address      = Output(UInt(Settings.PhysicalRegisterAddrWidth))
  })
  val opcode = io.instruction(6, 0)
  val funct3 = io.instruction(14, 12)
  val funct7 = io.instruction(31, 25)
  val rd     = io.instruction(11, 7)
  val rs1    = io.instruction(19, 15)
  val rs2    = io.instruction(24, 20)

  // lui 是立即加载指令 imm = Addr[31..12]
  io.regs_reg1_read_address := Mux(opcode === Instructions.lui, 0.U(Settings.PhysicalRegisterAddrWidth), rs1)
  io.regs_reg2_read_address := rs2
  val immediate = MuxLookup(opcode, Cat(Fill(20, io.instruction(31)), io.instruction(31, 20)))(
    IndexedSeq(
      InstructionTypes.I -> Cat(Fill(21, io.instruction(31)), io.instruction(30, 20)),
      InstructionTypes.L -> Cat(Fill(21, io.instruction(31)), io.instruction(30, 20)),
      Instructions.jalr  -> Cat(Fill(21, io.instruction(31)), io.instruction(30, 20)),
      InstructionTypes.S -> Cat(Fill(21, io.instruction(31)), io.instruction(30, 25), io.instruction(11, 7)),
      InstructionTypes.B -> Cat(
        Fill(20, io.instruction(31)),
        io.instruction(7),
        io.instruction(30, 25),
        io.instruction(11, 8),
        0.U(1.W)
      ),
      Instructions.lui   -> Cat(io.instruction(31, 12), 0.U(12.W)),
      Instructions.auipc -> Cat(io.instruction(31, 12), 0.U(12.W)),
      // jal's imm represents a multiple of 2 bytes.
      Instructions.jal -> Cat(
        Fill(12, io.instruction(31)),
        io.instruction(19, 12),
        io.instruction(20),
        io.instruction(30, 21),
        0.U(1.W)
      )
    )
  )
  io.ex_immediate := immediate
  // 跳转指令aluop1算地址去了
  // auipc 是立即数加载pc指令 imm = Addr[31.12]
  io.ex_aluop1_source := Mux(
    opcode === Instructions.auipc || opcode === InstructionTypes.B || opcode === Instructions.jal,
    ALUOp1Source.InstructionAddress,
    ALUOp1Source.Register
  )

  // ALU op2 from reg: R-type,
  // ALU op2 from imm: L-Type (I-type subtype),
  //                   I-type (nop=addi, jalr, csr-class, fence),
  //                   J-type (jal),
  //                   U-type (lui, auipc),
  //                   S-type (rs2 value sent to MemControl, ALU computes rs1 + imm.)
  //                   B-type (rs2 compares with rs1 in jump judge unit, ALU computes jump address PC+imm.)
  io.ex_aluop2_source := Mux(
    opcode === InstructionTypes.RM,
    ALUOp2Source.Register,
    ALUOp2Source.Immediate
  )

  // Load 指令铁定读内存了呀
  io.memory_read_enable := (opcode === InstructionTypes.L)
  // Store 指令铁定写内存了呀
  io.memory_write_enable := (opcode === InstructionTypes.S)

  // MuxCase(default, Array(c1 -> a, c2 -> b, ....))
  io.wb_reg_write_source := MuxCase(
    RegWriteSource.ALUResult,
    ArraySeq(
      (opcode === InstructionTypes.RM || opcode === InstructionTypes.I ||
        opcode === Instructions.lui || opcode === Instructions.auipc) -> RegWriteSource.ALUResult, // same as default
      (opcode === InstructionTypes.L)                                 -> RegWriteSource.Memory,
      (opcode === Instructions.jal || opcode === Instructions.jalr)   -> RegWriteSource.NextInstructionAddress
    )
  )

  // 对于有 rd 都会写回的
  io.reg_write_enable := (opcode === InstructionTypes.RM) || (opcode === InstructionTypes.I) ||
    (opcode === InstructionTypes.L) || (opcode === Instructions.auipc) || (opcode === Instructions.lui) ||
    (opcode === Instructions.jal) || (opcode === Instructions.jalr)
  // 直接目标寄存器
  io.reg_write_address := rd
}
