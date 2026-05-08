import miner.ShipWeaponMiner;
import parser.ESMJsonParser;
import util.Holder;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StarfieldMiner {
    public static void main(String[] args) {
        // String dataMiner = args[0];
        String base = "C:\\Users\\Eric Karlson\\Documents\\Starfield\\JSON\\";
        List<String> files = new ArrayList<String>(0);
        files.add(base + "AMMO.json");
        files.add(base + "AVIF.json");
        files.add(base + "COBJ.json");
        files.add(base + "DMGT.json");
        files.add(base + "EXPL.json");
        files.add(base + "FLST.json");
        files.add(base + "GBFM.json");
        files.add(base + "GLOB.json");
        files.add(base + "KYWD.json");
        files.add(base + "PERK.json");
        files.add(base + "PROJ.json");
        files.add(base + "WEAP.json");

        /*
        files.add(base + "AMMO-DSTGO.json");
        files.add(base + "COBJ-DSTGO.json");
        files.add(base + "FLST-DSTGO.json");
        files.add(base + "GBFM-DSTGO.json");
        files.add(base + "GLOB-DSTGO.json");
        files.add(base + "KYWD-DSTGO.json");
        files.add(base + "PERK-DSTGO.json");
        files.add(base + "PROJ-DSTGO.json");
        files.add(base + "WEAP-DSTGO.json");
        */

        /*
        files.add(base + "COBJ-FALK.json");
        files.add(base + "FLST-FALK.json");
        files.add(base + "GBFM-FALK.json");
        files.add(base + "KYWD-FALK.json");
        files.add(base + "WEAP-FALK.json");
        */

        /*
        files.add(base + "AMMO-MAT.json");
        files.add(base + "AVIF-MAT.json");
        files.add(base + "COBJ-MAT.json");
        files.add(base + "EXPL-MAT.json");
        files.add(base + "FLST-MAT.json");
        files.add(base + "GBFM-MAT.json");
        files.add(base + "GLOB-MAT.json");
        files.add(base + "KYWD-MAT.json");
        files.add(base + "PERK-MAT.json");
        files.add(base + "PROJ-MAT.json");
        files.add(base + "WEAP-MAT.json");
        */

        /*
        files.add(base + "AVIF-DSA.json");
        files.add(base + "COBJ-DSA.json");
        files.add(base + "FLST-DSA.json");
        files.add(base + "GBFM-DSA.json");
        files.add(base + "GLOB-DSA.json");
        files.add(base + "KYWD-DSA.json");
        files.add(base + "PERK-DSA.json");
        files.add(base + "WEAP-DSA.json");
        */

        Holder<String> current = new Holder<>();
        try (PrintStream output =
                     new PrintStream(base + "..\\output.csv")) {
            ESMJsonParser parser = new ESMJsonParser();
            Iterator<String> iter = files.iterator();
            while (iter.hasNext()) {
                parser.parse(iter.next(), current);
            }
            ShipWeaponMiner miner = new ShipWeaponMiner();
            miner.run(parser, output);
        } catch (IOException e) {
            System.out.println("StarField " + current.get() + ": error opening file: " + e.getMessage());
        }
    }
}
