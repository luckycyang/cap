import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
class MemoryAccessTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "MemoryAccessTest Basic Test"
  it should "MemoryAccess" in {
    test(new core.MemoryAccess).withAnnotations(Seq(WriteVcdAnnotation)) {
      dut => {
        // 测试 Load 指令
        dut.io.memory_read_enable.poke(true)
        dut.io.alu_result.poke(114514.U)
        dut.io.memory_bundle.address.expect(114514.U)
        dut.io.memory_bundle.read_data.poke(1919417.U)
        // lb
        dut.io.alu_result.poke(0x00.U)
        Funct3MemAccess(dut, 0)
        dut.io.alu_result.poke(0x01.U)
        Funct3MemAccess(dut, 0)
        dut.io.alu_result.poke(0x02.U)
        Funct3MemAccess(dut, 0)


        // lh
        Funct3MemAccess(dut, 1)
        // lw 
        Funct3MemAccess(dut, 2)
        // lbu
        Funct3MemAccess(dut, 4)
        // lhu
        Funct3MemAccess(dut, 5)

        // 写测试
        dut.io.memory_read_enable.poke(false)
        dut.io.memory_write_enable.poke(true)
        dut.io.reg2_data.poke("h12488421".U)
        // sb
        dut.io.alu_result.poke(0x00.U)
        Funct3MemAccess(dut, 0) // index 0
        dut.io.alu_result.poke(0x01.U)
        Funct3MemAccess(dut, 0) // index 1
        dut.io.alu_result.poke(0x02.U)
        Funct3MemAccess(dut, 0) // index 2
        // sh
        dut.io.alu_result.poke(0x00.U)
        Funct3MemAccess(dut, 1)
        dut.io.alu_result.poke(0x01.U)
        Funct3MemAccess(dut, 1)
        // sw
        Funct3MemAccess(dut, 2)
        
      }
    }
  }

  def Funct3MemAccess(dut: core.MemoryAccess, funct3: Int) {
    dut.io.funct3.poke(funct3.U)
    dut.clock.step()
  }
}

