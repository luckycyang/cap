package core
import chisel3._
import chisel3.util.Cat
import settings.Settings
import cpu.CPUBundle

class CPU extends Module {
  val io = IO(new CPUBundle)

  val regs       = Module(new RegisterFile)
  val inst_fetch = Module(new InstructionFetch)
  val id         = Module(new InstructionDecode)
  val ex         = Module(new Execute)
  val mem        = Module(new MemoryAccess)
  val wb         = Module(new WriteBack)

  io.deviceSelect := mem.io.memory_bundle
    .address(Settings.AddrBits - 1, Settings.AddrBits - Settings.SlaveDeviceCountBits)

  inst_fetch.io.jump_address_id       := ex.io.if_jump_address
  inst_fetch.io.jump_flag_id          := ex.io.if_jump_flag
  inst_fetch.io.instruction_valid     := io.instruction_valid
  inst_fetch.io.instruction_read_data := io.instruction
  io.instruction_address              := inst_fetch.io.instruction_address

  regs.io.write_enable  := id.io.reg_write_enable
  regs.io.write_address := id.io.reg_write_address
  regs.io.write_data    := wb.io.regs_write_data
  regs.io.read_address1 := id.io.regs_reg1_read_address
  regs.io.read_address2 := id.io.regs_reg2_read_address

  regs.io.debug_read_address := io.debug_read_address
  io.debug_read_data         := regs.io.debug_read_data

  id.io.instruction := inst_fetch.io.instruction

  // lab3(cpu) begin
  ex.io.instruction := inst_fetch.io.instruction  
  ex.io.instruction_address := inst_fetch.io.instruction_address
  ex.io.reg1_data := regs.io.read_data1
  ex.io.reg2_data := regs.io.read_data2
  ex.io.immediate := id.io.ex_immediate
  ex.io.aluop1_source := id.io.ex_aluop1_source
  ex.io.aluop2_source := id.io.ex_aluop2_source
  // lab3(cpu) end

  mem.io.alu_result          := ex.io.mem_alu_result
  mem.io.reg2_data           := regs.io.read_data2
  mem.io.memory_read_enable  := id.io.memory_read_enable
  mem.io.memory_write_enable := id.io.memory_write_enable
  mem.io.funct3              := inst_fetch.io.instruction(14, 12)

  io.memory_bundle.address := Cat(
    0.U(Settings.SlaveDeviceCountBits.W),
    mem.io.memory_bundle.address(Settings.AddrBits - 1 - Settings.SlaveDeviceCountBits, 0)
  )
  io.memory_bundle.write_enable  := mem.io.memory_bundle.write_enable
  io.memory_bundle.write_data    := mem.io.memory_bundle.write_data
  io.memory_bundle.write_strobe  := mem.io.memory_bundle.write_strobe
  mem.io.memory_bundle.read_data := io.memory_bundle.read_data

  wb.io.instruction_address := inst_fetch.io.instruction_address
  wb.io.alu_result          := ex.io.mem_alu_result
  wb.io.memory_read_data    := mem.io.wb_memory_read_data
  wb.io.regs_write_source   := id.io.wb_reg_write_source
}
