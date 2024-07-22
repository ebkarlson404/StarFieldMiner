import miner.ShipWeaponMiner;
import parser.ESMJsonParser;
import util.Holder;

import java.io.IOException;
import java.io.PrintStream;

public class StarfieldMiner {
    public static void main(String[] args) {
        // String dataMiner = args[0];
        String base = "C:\\Users\\Eric Karlson\\Documents\\Starfield\\JSON\\";
        String filename1 = base + "AMMO.json";
        String filename2 = base + "AVIF.json";
        String filename3 = base + "COBJ.json";
        String filename4 = base + "DMGT.json";
        String filename5 = base + "EXPL.json";
        String filename6 = base + "FLST.json";
        String filename7 = base + "GBFM.json";
        String filename8 = base + "GLOB.json";
        String filename9 = base + "KYWD.json";
        String filename10 = base + "PERK.json";
        String filename11 = base + "PROJ.json";
        String filename12 = base + "WEAP.json";

        Holder<String> current = new Holder<>();
        try (PrintStream output =
                     new PrintStream(base + "..\\output.csv")) {
            ESMJsonParser parser = new ESMJsonParser();
            // for (int idx = 1; idx < args.length; idx++) {
            //    parser.parse(args[idx]);
            //
            parser.parse(filename1, current);
            parser.parse(filename2, current);
            parser.parse(filename3, current);
            parser.parse(filename4, current);
            parser.parse(filename5, current);
            parser.parse(filename6, current);
            parser.parse(filename7, current);
            parser.parse(filename8, current);
            parser.parse(filename9, current);
            parser.parse(filename10, current);
            parser.parse(filename11, current);
            parser.parse(filename12, current);
            ShipWeaponMiner miner = new ShipWeaponMiner();
            miner.run(parser, output);
        } catch (IOException e) {
            System.out.println("StarField " + current.get() + ": error opening file: " + e.getMessage());
        }
    }
}
