package miner;

import datamodel.*;
import java.io.PrintStream;
import parser.Util;
import util.Assert;
import util.Holder;

/**
 * Class for data mining Ship Weapon stats from a COBJ record.
 *
 * @author Eric Karlson
 */
public class ShipWeapon {
  private final String make;
  private final String model;
  private final String weaponClass;
  private final String category;
  private final int cost;
  private final int hull;
  private final int mass;
  private final int health;
  private final double crew;
  private final int maxPower;
  private final int minLevel;
  private final int minStarshipEng;
  private final String requiredPerk;
  private final int hullDmg;
  private final int shieldDmg;
  private final int emDmg;
  private final double critBonus;
  private final int range;
  private final int speed;
  private final double rof;
  private final double rechargeDelay;
  private final double rechargeTime;
  private final int magazine;
  private final boolean turret;

  /**
   * Data mines a COBJ record to extract all relevant Ship Weapon Stats. Should only be called on
   * COBJ records that are known to be Ship Weapons
   *
   * @param cobj The {@link COBJRecord} to data mine for the Ship Weapon Stats
   */
  public ShipWeapon(COBJRecord cobj) {
    Assert.assertTrue(
        KYWDRecord.CATEGORY_SHIPMOD_WEAPON_FID.equals(cobj.getCategoryFormId()),
        "COBJ Record is not for a Ship Weapon");

    // Builder for accumulating required perks
    StringBuilder otherPerks = new StringBuilder();
    Holder<String> sep = new Holder<>("");

    // Data mine the conditions (specifically the minimum character level)
    Holder<Integer> playerLevel = new Holder<>();
    cobj.getConditions()
        .forEachRemaining(
            c -> {
              String glob;

              // Skip vendor availability conditions
              if (c.isMinLevelCondition()) {
                playerLevel.set(Util.asInt(c.getComparisonValue(), 1));
              } else if (null != (glob = c.getSubjectGlobalCheckTarget())) {
                otherPerks.append(sep);
                sep.set(", ");
                if (GLOBRecord.UC02_UC_SHIP_COMPONENTS_UNLOCKED.equals(glob)) {
                  otherPerks.append("Vanguard:Grunt Work");
                } else if (GLOBRecord.SHIP_BUILDER_ALLOW_LARGE_MODS.equals(glob)) {
                  otherPerks.append("[enable large ship modules]");
                } else if (GLOBRecord.SHIP_BUILDER_TEST_MODS.equals(glob)) {
                  otherPerks.append("[test ship modules]");
                } else {
                  otherPerks.append("[GLOB:" + glob + "]");
                }
              } else if (!c.isVendorAvailabilityCondition()) {
                // Unknown condition - report it and then skip it
                System.err.println("Unknown Condition in " + cobj + ": " + c);
              }
            });
    this.minLevel = playerLevel.getWithDefault(1);
    this.cost = cobj.getCost();

    // Data mine the required perks (including min starship eng)
    Holder<Integer> starshipEng = new Holder<>();
    cobj.getRequiredPerks()
        .forEachRemaining(
            rp -> {
              // If this the STARSHIP_ENG perk, capture the minimum value
              if (PERKRecord.SKILL_STARSHIP_DESIGN_FID.equals(rp.getPerkFormId())) {
                starshipEng.set(rp.getRank());
              } else {
                PERKRecord perk = Assert.assertNotNull(rp.getPerk(), "Cannot find PERK in " + rp);
                otherPerks.append(perk.getFullName());
                otherPerks.append(" >= ");
                otherPerks.append(rp.getRank());
                otherPerks.append(sep.get());
                sep.set(", ");
              }
            });
    this.minStarshipEng = starshipEng.getWithDefault(0);
    this.requiredPerk = otherPerks.toString();

    // Data mine the GBFM associated with this COBJ to get the make and class
    GBFMRecord gbfm =
        Assert.assertNotNull(
            cobj.getCreatedObject(GBFMRecord.class), "Missing Created Object in " + cobj);
    this.make = Assert.assertNotNull(gbfm.getManufacturer(), "No weapon make found in " + gbfm);
    this.weaponClass =
        Assert.assertNotNull(gbfm.getShipModuleClass(), "No weapon class found in " + gbfm);

    // Data mine the PropertySheet to get hull, mass, crew, health and max power
    GBFMRecord.PropertySheet props =
        Assert.assertNotNull(gbfm.getPropertySheet(), "Missing Property Sheet in " + gbfm);
    this.hull = props.getPropertyValueAsInt(AVIFRecord.HEALTH_FID, "Missing hull data in " + gbfm);
    this.mass =
        props.getPropertyValueAsInt(
            AVIFRecord.SPACESHIP_PART_MASS_FID, "Missing mass data in " + gbfm);
    this.crew =
        props.getPropertyValueAsDouble(
            AVIFRecord.SPACESHIP_CREW_RATING_FID, "Missing crew data in " + gbfm, 0.25);
    this.health =
        props.getPropertyValueAsInt(
            AVIFRecord.SHIP_SYSTEM_WEAPON_HEALTH_FID, "Missing health data in " + gbfm);
    this.maxPower =
        props.getPropertyValueAsInt(
            AVIFRecord.SPACESHIP_WEAPON_POWER_FID, "Missing max power data in " + gbfm);

    // Data mine the WEAP record to get everything else but projectile speed
    WEAPRecord weap =
        Assert.assertNotNull(gbfm.getWEAPRecord(), "Could not find WEAP record in " + gbfm);
    this.model = weap.getFullName();
    this.turret = weap.isSpaceshipTurrentWeapon();
    this.category = weap.getSpaceshipWeaponCategory();
    int partialHullDmg = weap.getPhysicalDamage();
    int partialShieldDmg = weap.getAuxDamage(DMGTRecord.SHIELD_FID);
    int partialEmDmg = weap.getAuxDamage(DMGTRecord.ELECTROMAGNETIC_FID);
    this.range = weap.getMaxRange();
    this.critBonus = weap.getCriticalDamageMultiplier();
    this.rechargeDelay = weap.getRechargeDelay();
    this.rechargeTime = weap.getRechargeTime();
    this.magazine = weap.getAmmoCapacity();
    this.rof = weap.getShotsPerSecond();

    // Find this weapon's AMMO information, and from that get the PROJ information
    AMMORecord ammo =
        Assert.assertNotNull(weap.getAMMORecord(), "No AMMO record found for " + weap);
    PROJRecord proj =
        Assert.assertNotNull(ammo.getPROJRecord(), "No PROJ record found for " + ammo);

    // Data mine the PROJ record to get the speed
    this.speed = proj.getSpeed();

    // Get the EXPL record and data mine for additional damage stats
    EXPLRecord expl = proj.getEXPLRecord();
    if (null != expl) {
      partialHullDmg += expl.getPhysicalDamage();
      partialShieldDmg += expl.getAuxDamage(DMGTRecord.SHIELD_FID);
      partialEmDmg += expl.getAuxDamage(DMGTRecord.ELECTROMAGNETIC_FID);
    }
    this.hullDmg = partialHullDmg;
    this.shieldDmg = partialShieldDmg;
    this.emDmg = partialEmDmg;
  }

  public static ShipWeapon fromCOBJRecord(COBJRecord cobj) {
    // Ensure that this is a Ship Weapon COBJ
    if (KYWDRecord.CATEGORY_SHIPMOD_WEAPON_FID.equals(cobj.getCategoryFormId())) {
      // Generate the ship data metrics
      try {
        return new ShipWeapon(cobj);
      } catch (Throwable e) {
        // Output warning and then fall through to ignore this COBJ
        System.err.println("Malformed data associated with " + cobj + "details->" + e.getMessage());
      }
    }

    // Not a weapons COBJ - ignore
    return null;
  }

  public static void emitHeaders(PrintStream output) {
    output.println(
        "Make|Model|Class|Category|Cost|Hull|Mass|Health|Crew Capacity|Max Power|"
            + "Required Level|Required Starship Eng|Required Perk|Hull Dmg|Shield Dmg|EM Dmg|Crit Damage|Range|"
            + "Speed|ROF|Recharge Delay|Recharge Time|Magazine|Is Turret");
  }

  public void emitAsCSV(PrintStream output) {
    output.printf(
        "%s|%s|%s|%s|%d|%d|%d|%d|%f|%d|%d|%d|%s|%d|%d|%d|%f|%d|%d|%f|%f|%f|%d|%s\n",
        make,
        model,
        weaponClass,
        category,
        cost,
        hull,
        mass,
        health,
        crew,
        maxPower,
        minLevel,
        minStarshipEng,
        requiredPerk,
        hullDmg,
        shieldDmg,
        emDmg,
        critBonus,
        range,
        speed,
        rof,
        rechargeDelay,
        rechargeTime,
        magazine,
        turret ? "X" : "");
  }
}
