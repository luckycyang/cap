package core
import chisel3._

class TimingGeneration extends Module {
  override protected def implicitReset: Reset = reset
  val out = IO(Output(Vec(4, Bool())))
  val reg = RegInit(0.U(2.W))
  reg := reg + 1.U
  require(out.length > 0)
  for(i <- 0 until 4) {
    out(i) := Mux(reg === i.U, true.B, false.B)
  }
}
