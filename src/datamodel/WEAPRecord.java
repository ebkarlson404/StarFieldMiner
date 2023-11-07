package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parser.ESMJsonParser;
import parser.Util;
import util.Assert;
import util.ESMKeyValueMap;

import java.util.*;

/**
 * Abstraction of the WEAP records
 *
 * @author Eric Karlson
 */
@ESMGroup("WEAP")
public class WEAPRecord extends Record {
    private static final String FLD_KEYWORDS = "Keywords";
    private static final String FLD_KWDA = "KWDA - Keywords";
    private static final String FLD_KEYWORD = "Keyword";
    private static final String FLD_WDMG = "WDMG - Damage";
    private static final String FLD_ATTACK_DAMAGE = "Attack Damage";
    private static final String FLD_MIN_RANGE = "Range - Min";
    private static final String FLD_MAX_RANGE = "Range - Max";
    private static final String FLD_CRIT_DAMAGE_MULT = "Crit Damage Mult";
    private static final String FLD_DAMA = "DAMA - Damage Types";
    private static final String FLD_DAMAGE_TYPE = "Damage Type";
    private static final String FLD_VALUE = "Value";
    private static final String FLD_QNAM = "QNAM - Power";
    private static final String FLD_RECHARGE_TIME = "Recharge time";
    private static final String FLD_RECHARGE_DELAY = "Recharge delay";
    private static final String FLD_WAM2 = "WAM2 - Ammunition";
    private static final String FLD_AMMO_CAPACITY = "Ammo Capacity";
    private static final String FLD_AMMO_TYPE = "Ammo Type";
    private static final String FLD_WFIR = "WFIR - Firing";
    private static final String FLD_SHOTS_PER_SECOND = "Shots Per Second";

    /**
     * Maps Space Ship Class KEYW Form ID's to human-readable descriptors
     */
    private static final Map<String, String> SHIP_CLASS_KEYW_FORMID_TO_READABLE = new HashMap<>();

    static {
        SHIP_CLASS_KEYW_FORMID_TO_READABLE.put(KYWDRecord.SPACESHIP_ELECTROMAGNETIC_WEAPON_FID, "EM");
        SHIP_CLASS_KEYW_FORMID_TO_READABLE.put(KYWDRecord.SPACESHIP_ENERGY_WEAPON_FID, "Energy");
        SHIP_CLASS_KEYW_FORMID_TO_READABLE.put(KYWDRecord.SPACESHIP_KINETIC_WEAPON_FID, "Ballistic");
        SHIP_CLASS_KEYW_FORMID_TO_READABLE.put(KYWDRecord.SPACESHIP_MISSILE_WEAPON_FID, "Missile");
        SHIP_CLASS_KEYW_FORMID_TO_READABLE.put(KYWDRecord.SPACESHIP_PARTICLE_WEAPON_FID, "Particle");
    }

    public WEAPRecord(String formId, String editorId, String signature, JsonNode node, ESMJsonParser parser) {
        super(formId, editorId, signature, node, parser);
    }

    /**
     * Enumerates the Form IDs of all KEYW's associated with this WEAP record
     *
     * @return An {@link Iterator} for the enumerated form ids
     */
    public @NotNull Iterator<String> getKeywordFormIds() {
        JsonNode keywords = node.get(FLD_KEYWORDS);
        if (null == keywords) {
            return Collections.emptyIterator();
        }
        JsonNode kwda = keywords.get(FLD_KWDA);
        if (null == kwda) {
            return Collections.emptyIterator();
        }

        // This is another example of where serialize-command-json creates a JSON Objcct
        // with repeating property names.  Make use of the implicit disambiguation logic
        // that replaces replicated property names with generated names that are unique
        return new Iterator<>() {
            private int idx = 1;

            @Override
            public boolean hasNext() {
                return null != kwda.get(ESMKeyValueMap.generateRepeatedKey(FLD_KEYWORD, idx));
            }

            @Override
            public String next() {
                JsonNode value = kwda.get(ESMKeyValueMap.generateRepeatedKey(FLD_KEYWORD, idx++));
                if (null == value) {
                    throw new NoSuchElementException();
                }
                return (value.isTextual()) ? value.asText() : null;
            }
        };
    }

    /**
     * Tests whether this WEAP record has the indicate KEYW
     *
     * @param keywFormId The Form ID of the KEYW of interest
     * @return {@code true} if the KEYW was found in the list of keywords for this WEAP
     */
    public boolean hasKeyword(@NotNull String keywFormId) {
        Iterator<String> iter = getKeywordFormIds();
        while (iter.hasNext()) {
            if (keywFormId.equals(iter.next())) {
                return true;
            }
        }
        return false;
    }

    public boolean isSpaceshipTurrentWeapon() {
        return hasKeyword(KYWDRecord.SPACESHIP_TURRET_WEAPON_FID);
    }

    public @NotNull String getSpaceshipWeaponCategory() {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = getKeywordFormIds();
        String sep = "";
        while (iter.hasNext()) {
            String desc = SHIP_CLASS_KEYW_FORMID_TO_READABLE.get(iter.next());
            if (null != desc) {
                builder.append(sep);
                builder.append(desc);
                sep = ", ";
            }
        }
        return builder.toString();
    }

    public int getPhysicalDamage() {
        JsonNode wdmg = node.get(FLD_WDMG);
        if (null == wdmg) {
            return 0;
        }
        JsonNode dmg = wdmg.get(FLD_ATTACK_DAMAGE);
        return Util.asInt(dmg, 0);
    }

    public int getMinRange() {
        JsonNode wdmg = node.get(FLD_WDMG);
        if (null == wdmg) {
            return 0;
        }
        JsonNode range = wdmg.get(FLD_MIN_RANGE);
        return Util.asInt(range, 0);
    }

    public int getMaxRange() {
        JsonNode wdmg = node.get(FLD_WDMG);
        if (null == wdmg) {
            return 0;
        }
        JsonNode range = wdmg.get(FLD_MAX_RANGE);
        return Util.asInt(range, 0);
    }

    public double getCriticalDamageMultiplier() {
        JsonNode wdmg = node.get(FLD_WDMG);
        if (null == wdmg) {
            return 0;
        }
        JsonNode crit = wdmg.get(FLD_CRIT_DAMAGE_MULT);
        return Util.asDouble(crit, 1.0);
    }

    public int getAuxDamage(@NotNull String damageTypeFormId) {
        JsonNode dama = node.get(FLD_DAMA);
        if (null == dama) {
            return 0;
        }

        // This is another example of where the serialize-command-json script generates JSON
        // Objects that have repeating property names.  Use our disambiguaton logic to access
        // each repeated property with a unique name.
        JsonNode dmg;
        for (int idx = 1; null != (dmg = dama.get(ESMKeyValueMap.generateRepeatedKey(FLD_DAMAGE_TYPE, idx))); idx++) {
            JsonNode dtype = dmg.get(FLD_DAMAGE_TYPE);
            if (null != dtype && damageTypeFormId.equals(dtype.asText())) {
                JsonNode value = dmg.get(FLD_VALUE);
                return Util.asInt(value, 0);
            }
        }

        // No auxiliary damage info of this type -> 0
        return 0;
    }

    public double getRechargeDelay() {
        JsonNode qnam = Assert.assertNotNull(node.get(FLD_QNAM), "Missing QNAM in " + this);
        JsonNode delay = Assert.assertNotNull(qnam.get(FLD_RECHARGE_DELAY), "Missing Recharge Delay in " + this);
        return Util.asDouble(delay, 0.0);
    }

    public double getRechargeTime() {
        JsonNode qnam = Assert.assertNotNull(node.get(FLD_QNAM), "Missing QNAM in " + this);
        JsonNode delay = Assert.assertNotNull(qnam.get(FLD_RECHARGE_TIME), "Missing Recharge Time in " + this);
        return Util.asDouble(delay, 0.0);
    }

    public int getAmmoCapacity() {
        JsonNode wam2 = node.get(FLD_WAM2);
        if (null == wam2) {
            return 0;
        }
        JsonNode ammo = wam2.get(FLD_AMMO_CAPACITY);
        return Util.asInt(ammo, 0);
    }

    public double getShotsPerSecond() {
        JsonNode wfir = node.get(FLD_WFIR);
        if (null == wfir) {
            return 0.0;
        }
        JsonNode rof = wfir.get(FLD_SHOTS_PER_SECOND);
        return Util.asDouble(rof, 0.0);
    }

    public @Nullable AMMORecord getAMMORecord() {
        JsonNode wam2 = node.get(FLD_WAM2);
        if (null == wam2) {
            return null;
        }
        JsonNode ammo = wam2.get(FLD_AMMO_TYPE);
        return (null != ammo && ammo.isTextual()) ? parser.findRecordByFormId(ammo.asText(), AMMORecord.class) : null;
    }
}
