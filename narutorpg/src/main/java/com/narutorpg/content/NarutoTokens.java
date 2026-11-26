package com.narutorpg.content;

import com.animecore.technique.TechniqueTokenItem;
import com.animecore.technique.TechniqueTokens;
import com.narutorpg.NarutoRPG;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AHZNB-style jutsu items: every jutsu lives on a token - one per nature release (token id = the affinity id) and one
 * per school. Hold it and right-click to cast (hold to charge), sneak + scroll / sneak + left-click to switch jutsu.
 * The Dōjutsu token also toggles the eyes (Sharingan stages, Byakugan, Rinnegan...). Sprites: tools/catalog/tokens.py.
 */
public final class NarutoTokens {
    private NarutoTokens() {}

    /** token id path -> (item path, colour). Nature tokens use the affinity id so affinity-based jutsu land on them. */
    private static final String[][] TOKENS = {
            {"fire", "fire_release", "FF8A1E"}, {"wind", "wind_release", "B2FF59"}, {"lightning", "lightning_release", "80D8FF"},
            {"earth", "earth_release", "A1887F"}, {"water", "water_release", "40A8FF"}, {"wood", "wood_release", "8BC34A"},
            {"ice", "ice_release", "B3F0FF"}, {"lava", "lava_release", "FF5722"}, {"magnet", "magnet_release", "D7A86E"},
            {"storm", "storm_release", "26C6DA"}, {"boil", "boil_release", "FFAB91"}, {"scorch", "scorch_release", "FFCC80"},
            {"explosion", "explosion_release", "FFEE58"}, {"dust", "dust_release", "FFFFFF"}, {"yin", "yin_release", "7B1FA2"},
            {"yang", "yang_release", "FFE57F"}, {"mud", "mud_release", "8D6E63"}, {"yin_yang", "yin_yang_release", "FAFAFA"},
            {"crystal", "crystal_release", "F8BBD0"}, {"dark", "dark_release", "5E35B1"}, {"steel", "steel_release", "CFD8DC"},
            {"swift", "swift_release", "FFF59D"}, {"typhoon", "typhoon_release", "80CBC4"}, {"ninjutsu", "ninjutsu", "90CAF9"}, {"taijutsu", "taijutsu", "66BB6A"},
            {"genjutsu", "genjutsu", "F06292"}, {"fuinjutsu", "fuinjutsu", "FFD54F"}, {"medical_ninjutsu", "medical_ninjutsu", "76FF03"},
            {"hiden", "hiden", "BDBDBD"}, {"dojutsu", "dojutsu", "E53935"}, {"bukijutsu", "bukijutsu", "CFD8DC"},
            {"jinchuriki", "jinchuriki", "FF6D00"}, {"senjutsu", "senjutsu", "FFB300"}, {"kekkei_genkai", "kekkei_genkai", "B388FF"},
    };

    public static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();

    static {
        for (String[] t : TOKENS) {
            Identifier token = NarutoRPG.CTX.id(t[0]);
            Item item = Registry.register(Registries.ITEM, NarutoRPG.CTX.id(t[1]),
                    new TechniqueTokenItem(NarutoRPG.CTX, token, Integer.parseInt(t[2], 16), new Item.Settings().rarity(Rarity.UNCOMMON)));
            ITEMS.put(token, item);
        }
        TechniqueTokens.configure(NarutoRPG.CTX, ITEMS, NarutoRPG.CTX.id("ninjutsu"), NarutoRPG.CTX.id("dojutsu"));
    }

    public static Item item(String tokenPath) {
        return ITEMS.get(NarutoRPG.CTX.id(tokenPath));
    }

    /** item path for the lang / model providers */
    public static Map<Item, String> names() {
        Map<Item, String> out = new LinkedHashMap<>();
        String[] schools = {"Ninjutsu", "Taijutsu", "Genjutsu", "Fūinjutsu", "Medical Ninjutsu", "Hiden Techniques", "Dōjutsu", "Bukijutsu",
                "Jinchūriki", "Senjutsu", "Kekkei Genkai"};
        int s = 0;
        for (String[] t : TOKENS) {
            Item item = ITEMS.get(NarutoRPG.CTX.id(t[0]));
            if (t[1].endsWith("_release")) {
                String n = t[1].replace("_release", "");
                out.put(item, n.equals("yin_yang") ? "Yin–Yang Release" : Character.toUpperCase(n.charAt(0)) + n.substring(1) + " Release");
            } else {
                out.put(item, schools[s++]);
            }
        }
        return out;
    }

    public static void init() {}
}
