import java.io.IOException;
import java.io.PrintStream;
import miner.ShipWeaponMiner;
import parser.ESMJsonParser;

public class StarfieldMiner {
  public static void main(String[] args) {
    // String dataMiner = args[0];
    String filename = "C:\\Users\\Eric Karlson\\Downloads\\xEdit\\output.json";
    String filename1 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit\\AMMO.json";
    String filename2 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit\\COBJ.json";
    String filename3 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit\\EXPL.json";
    String filename4 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit\\FLST.json";
    String filename5 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit\\GBFM.json";
    String filename6 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit\\GLOB.json";
    String filename7 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit\\KYWD.json";
    String filename8 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit\\PERK.json";
    String filename9 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit\\PROJ.json";
    String filename10 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit\\WEAP.json";

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
      ShipWeaponMiner miner = new ShipWeaponMiner();
      miner.run(parser, output);
    } catch (IOException e) {
      System.out.println("StarField <filename>: error opening file: " + e.getMessage());
    }
  }
}
