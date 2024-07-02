package core
import chisel3._






class BasicModule extends Module {
  val in = IO(Input(Bool()))
  val out = IO(Bool())
  out := in  
}

object Basic extends App {
  println(getVerilogString(new BasicModule))
}
