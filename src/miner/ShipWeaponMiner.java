package miner;

import datamodel.COBJRecord;
import datamodel.Record;
import java.io.PrintStream;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import parser.ESMJsonParser;
import util.Holder;

/**
 * Data Miner for extracting all information on ship weapons. Requires that the {@link
 * parser.ESMJsonParser} contains all records for the following groups: *
 *
 * <ul>
 *   <li>AMMO - <i>Ammunition</i>
 *   <li>AVIF - <i>Actor Value Information</i>
 *   <li>COBJ - <i>Constructible Object</i>
 *   <li>DMGT - <i>Damage Type</i>
 *   <li>EXPL - <i>Explosion</i>
 *   <li>FLST - <i>FormId List</i>
 *   <li>GBFM - <i>Generic Base Form</i>
 *   <li>KYWD = <i>Keyword</i>
 *   <li>PERK - <i>Perk</i>
 *   <li>PROJ - <i>Projectile</i>
 *   <li>WEAP - <i>Weapon</i>
 * </ul>
 */
@DataMiner("ShipWeapon")
public class ShipWeaponMiner implements IDataMiner {
  /** {@inheritDoc} */
  @Override
  public void run(@NotNull ESMJsonParser parser, @NotNull PrintStream output) {
    ShipWeapon.emitHeaders(output);
    List<Record> group = parser.getGroup(COBJRecord.class);
    Holder<Integer> cnt = new Holder(0);
    group
        .iterator()
        .forEachRemaining(
            r -> {
              ShipWeapon shipWeapon = ShipWeapon.fromCOBJRecord((COBJRecord) r);
              if (null != shipWeapon) {
                shipWeapon.emitAsCSV(output);
                cnt.set(cnt.get() + 1);
              }
            });
    System.err.println("Emitted " + cnt.get() + " records");
  }
}
