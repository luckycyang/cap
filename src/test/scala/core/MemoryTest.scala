import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import peripheral._
import chisel3.util.Fill
import settings.Settings



class MemoryTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Memory Basic Test"
  it should "Memory" in {
    test(new Memory(Settings.MemorySizeInWords)).withAnnotations(Seq(WriteVcdAnnotation)) {
      dut => {
        val read = (addr: Int) => readData(dut, addr)
        val write = (addr: Int, data: UInt ) => writeData(dut, addr, data)
        val debug = (addr: Int) => debugData(dut, addr) // 由 read 辅助驱动
        debug(0x3)
        dut.io.bundle.write_strobe(0).poke(true)
        write(0x00, "hffff_ffff".U)
        dut.clock.step()
        dut.io.bundle.write_strobe(1).poke(true)
        write(0x01, "h12345678".U)
        dut.io.bundle.write_strobe(0).poke(false)
        write(0x00, "h1234ffff".U)
          
      }
    }
  }

  def readData(dut: Memory, addr: Int): Unit =  {
    dut.io.bundle.address.poke(addr.U)
    dut.clock.step()
  }

  def debugData(dut: Memory, addr: Int) : Unit={
    dut.io.debug_read_address.poke(addr.U)
  }

  def writeData(dut: Memory, addr: Int, data: UInt): Unit= {
    dut.io.bundle.address.poke(addr.U)
    dut.io.bundle.write_data.poke(data)
    dut.io.bundle.write_enable.poke(true)
    dut.clock.step()
    dut.io.bundle.write_enable.poke(false)
  }

}

