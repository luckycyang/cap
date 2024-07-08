import chisel3._
import core.MemoryAccess
import peripheral.Memory
import settings.Settings
import chiseltest._
import chiseltest.ChiselScalatestTester
import org.scalatest.flatspec.AnyFlatSpec
import chiseltest.simulator.WriteVcdAnnotation


class Mem2Access extends Module {
  val io = IO(new Bundle() {
    val alu_result          = Input(UInt(Settings.DataWidth))
    val reg2_data           = Input(UInt(Settings.DataWidth))
    val memory_read_enable  = Input(Bool())
    val memory_write_enable = Input(Bool())
    val funct3              = Input(UInt(3.W))
    val wb_memory_read_data = Output(UInt(Settings.DataWidth))
    
    val instruction         = Output(UInt(Settings.DataWidth))
    val instruction_address = Input(UInt(Settings.AddrWidth))

    val debug_read_address = Input(UInt(Settings.AddrWidth))
    val debug_read_data    = Output(UInt(Settings.DataWidth))


  })
  val mem = Module(new Memory(Settings.MemorySizeInWords))
  val access = Module(new MemoryAccess)

  io.alu_result <> access.io.alu_result
  io.reg2_data <> access.io.reg2_data
  io.memory_read_enable <> access.io.memory_read_enable
  io.memory_write_enable <> access.io.memory_write_enable
  io.funct3 <> access.io.funct3
  io.wb_memory_read_data <> access.io.wb_memory_read_data

  io.instruction_address <> mem.io.instruction_address
  io.instruction <> mem.io.instruction
  io.debug_read_address <> mem.io.debug_read_address
  io.debug_read_data <> mem.io.debug_read_data

  mem.io.bundle <> access.io.memory_bundle
}



class Mem2AccessTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Mem2Access Basic Test"
  it should "Mem2Access" in {
    test(new Mem2Access).withAnnotations(Seq(WriteVcdAnnotation)) {
      dut => {
        // write to 0x1000
        dut.io.alu_result.poke(0x1000.U)
        dut.io.memory_write_enable.poke(true)
        dut.io.reg2_data.poke("h87654321".U)
        dut.io.funct3.poke(1.U) // sh
        dut.clock.step()
        dut.io.memory_write_enable.poke(false)

        
        dut.io.alu_result.poke(0x1000.U)
        dut.io.funct3.poke(4.U)
        dut.io.memory_read_enable.poke(true.B)
        dut.clock.step()

        dut.clock.step(2)
      }
    }
  }
}
 
