import chisel3.stage.ChiselGeneratorAnnotation
import circt.stage.{CIRCTTarget, CIRCTTargetAnnotation, ChiselStage}
import firrtl.{AnnotationSeq, EmittedVerilogCircuitAnnotation}
import chisel3._


object TopMain extends App {
    def parseArgs(key: String, args: Array[String]): String = {
      var value = ""
      for(arg <- args) {
        if(args.startsWith(key + "=") == true){
          value = arg
        }
        require(value != "")
        value.substring(key.length() + 1)
      }
      require(value != "")
      value.substring(key.length() + 1)
    }
    circt.stage.ChiselStage.emitSystemVerilogFile(new top.TestTopModule("target/apps/test.hex"), Array("-td", "build"), Array("--strip-debug-info"))
    // generateVerilog(new peripheral.ROMLoader(128))
    
}

object generateVerilog {
    def apply(gen: => RawModule, args: Array[String] = Array.empty, annotations: AnnotationSeq = Seq.empty): String = {
    (new ChiselStage)
      .execute(
        Array("--target", "verilog") ++ args ++ Array("-td", "build"),
        ChiselGeneratorAnnotation(() => gen) +: annotations 
      )
      .collectFirst {
        case EmittedVerilogCircuitAnnotation(a) => a
      }
      .get
      .value
  }
}
