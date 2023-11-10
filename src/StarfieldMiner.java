import java.io.IOException;
import java.io.PrintStream;
import miner.ShipWeaponMiner;
import parser.ESMJsonParser;

public class StarfieldMiner {
  public static void main(String[] args) {
    // String dataMiner = args[0];
    String filename1 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit_4.1.5a_EXPERIMENTAL\\JSON\\AMMO.json";
    String filename2 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit_4.1.5a_EXPERIMENTAL\\JSON\\AVIF.json";
    String filename3 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit_4.1.5a_EXPERIMENTAL\\JSON\\COBJ.json";
    String filename4 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit_4.1.5a_EXPERIMENTAL\\JSON\\DMGT.json";
    String filename5 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit_4.1.5a_EXPERIMENTAL\\JSON\\EXPL.json";
    String filename6 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit_4.1.5a_EXPERIMENTAL\\JSON\\FLST.json";
    String filename7 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit_4.1.5a_EXPERIMENTAL\\JSON\\GBFM.json";
    String filename8 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit_4.1.5a_EXPERIMENTAL\\JSON\\GLOB.json";
    String filename9 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit_4.1.5a_EXPERIMENTAL\\JSON\\KYWD.json";
    String filename10 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit_4.1.5a_EXPERIMENTAL\\JSON\\PERK.json";
    String filename11 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit_4.1.5a_EXPERIMENTAL\\JSON\\PROJ.json";
    String filename12 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit_4.1.5a_EXPERIMENTAL\\JSON\\WEAP.json";

    try (PrintStream output =
        new PrintStream("C:\\Users\\Eric Karlson\\Downloads\\xEdit\\output.csv")) {
      ESMJsonParser parser = new ESMJsonParser();
      // for (int idx = 1; idx < args.length; idx++) {
      //    parser.parse(args[idx]);
      //
      parser.parse(filename1);
      parser.parse(filename2);
      parser.parse(filename3);
      parser.parse(filename4);
      parser.parse(filename5);
      parser.parse(filename6);
      parser.parse(filename7);
      parser.parse(filename8);
      parser.parse(filename9);
      parser.parse(filename10);
      parser.parse(filename11);
      parser.parse(filename12);
      ShipWeaponMiner miner = new ShipWeaponMiner();
      miner.run(parser, output);
    } catch (IOException e) {
      System.out.println("StarField <filename>: error opening file: " + e.getMessage());
    }
  }
}
