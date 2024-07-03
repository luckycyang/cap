package core
import chisel3._

class TimingGeneration extends Module {
  val out = IO(Output(Vec(4, Bool())))
  val reg = RegInit(0.U(2.W))
  reg := reg + 1.U
  when (reg === 0.U) {
    out(0) := true.B
  } .otherwise {
    out(0) := false.B
  }
  when (reg === 1.U) {
    out(1) := true.B
  } .otherwise {
    out(1) := false.B
  }
  when (reg === 2.U) {
    out(2) := true.B
  } .otherwise {
    out(2) := false.B
  }
  when (reg === 3.U) {
    out(3) := true.B
  } .otherwise {
    out(3) := false.B
  }




  
}
