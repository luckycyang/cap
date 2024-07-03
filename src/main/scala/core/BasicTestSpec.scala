package core
import chisel3._
import chisel3.util._






class BasicModule extends Module {
  val in = IO(Input(Bool()))
  val out = IO(Bool())
  out := in  
}


class Operation(mode: Boolean) extends Module {
  val in0, in1 = IO(Input(UInt(16.W)))
  val out = IO(Output(UInt(16.W)))
  
  if (mode) {
    out := in0 + in1
  } else {
    out := in0 - in1
  }
}

class OperationTestSpec() extends Module {
  val in0, in1 = IO(Input(UInt(16.W)))
  val out = IO(Output(UInt(16.W)))
  val sel = IO(Input(Bool()))
  val op0 = Module(new Operation(true))
  val op1 = Module(new Operation(false))
  in0 <> op0.in0
  in0 <> op1.in0
  in1 <> op0.in1
  in1 <> op1.in1
  out := MuxLookup(sel, in0)(Seq(false.B -> op0.out, true.B -> op1.out))
}

object Basic extends App {
  println(getVerilogString(new OperationTestSpec))
}
