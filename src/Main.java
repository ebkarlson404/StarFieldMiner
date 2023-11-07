import datamodel.COBJRecord;
import datamodel.Record;
import datamodel.ShipWeapon;
import parser.ESMJsonParser;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //String dataMiner = args[0];
        String filename1 = "U:\\xEdit\\clean\\AMMO.json";
        String filename2 = "U:\\xEdit\\clean\\COBJ.json";
        String filename3 = "U:\\xEdit\\clean\\EXPL.json";
        String filename4 = "U:\\xEdit\\clean\\GBFM.json";
        String filename5 = "U:\\xEdit\\clean\\KEYW.json";
        String filename6 = "U:\\xEdit\\clean\\PERK.json";
        String filename7 = "U:\\xEdit\\clean\\PROJ.json";
        String filename8 = "U:\\xEdit\\clean\\WEAP.json";
        String filename9 = "C:\\Users\\Eric Karlson\\Downloads\\xEdit\\FLST.json";

        try (PrintStream output = new PrintStream("C:\\Users\\Eric Karlson\\Downloads\\xEdit\\output.csv")) {
            ESMJsonParser parser = new ESMJsonParser();
            //for (int idx = 1; idx < args.length; idx++) {
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
            ShipWeapon.emitHeaders(output);
            List<Record> group = parser.getGroup(COBJRecord.class);
            group.iterator().forEachRemaining(r ->
            {
                ShipWeapon shipWeapon = ShipWeapon.fromCOBJRecord((COBJRecord) r);
                if (null != shipWeapon) {
                    shipWeapon.emitAsCSV(output);
                }
            });
        } catch (IOException e) {
            System.out.println("StarField <filename>: error opening file: " + e.getMessage());
        }
    }
}