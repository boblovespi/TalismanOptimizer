package q256.optimizer.api;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import q256.optimizer.Equipment;
import q256.optimizer.PlayerEquipment;
import q256.optimizer.PlayerStats;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Willi on 5/25/2020.
 */
public class Parser
{
	private static Pattern strPat = Pattern.compile("c[+-]\\d+");
	private static Pattern hpPat = Pattern.compile("a[+-]\\d+");

	private static double[] skillXpTable = { 50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500, 5000, 7500, 10000,
			15000, 20000, 30000, 50000, 75000, 100000, 200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000,
			1000000, 1100000, 1200000, 1300000, 1400000, 1500000, 1600000, 1700000, 1800000, 1900000, 2000000, 2100000,
			2200000, 2300000, 2400000, 2500000, 2600000, 2750000, 2900000, 3100000, 3400000, 3700000, 4000000 };

	private static double[] fairySoulHp = { 0, 3, 6, 10, 14, 19, 24, 30, 36, 43, 50, 58, 66, 75, 84, 94, 104, 115, 126,
			138, 150, 163, 176, 190, 204, 219, 234, 250, 266, 283, 300, 318, 336, 355, 374, 394, 414, 435, 456, 478,
			500 };
	private static double[] fairySoulDef = { 0, 1, 2, 3, 4, 6, 7, 8, 9, 10, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 24,
			25, 26, 27, 28, 30, 31, 32, 33, 34, 36, 37, 38, 39, 40, 42, 43, 44, 45, 46, 48 };

	private static double[] slayerXp = { 5, 15, 200, 1000, 5000, 20000, 100000, 400000, 1000000 };

	public static JsonObject getPlayerProfile(JsonObject profile, String fruit, String uuid)
	{
		JsonArray profiles = profile.get("profiles").getAsJsonArray();
		for (JsonElement prof : profiles)
		{
			if (prof.getAsJsonObject().get("cute_name").getAsString().equals(fruit))
				return prof.getAsJsonObject().get("members").getAsJsonObject().get(uuid).getAsJsonObject();
		}
		return null;
	}

	public static String[] getFruits(JsonObject profile)
	{
		JsonArray profiles = profile.get("profiles").getAsJsonArray();
		String[] fruits = new String[profiles.size()];
		for (int i = 0; i < profiles.size(); ++i)
		{
			fruits[i] = profiles.get(i).getAsJsonObject().get("cute_name").getAsString();
		}
		return fruits;
	}

	public static int[] getTalisInBag(JsonObject profile)
	{

		String data = profile.get("talisman_bag").getAsJsonObject().get("data").getAsString();
		CompoundTag talis = (CompoundTag) Parser.decodeNbt(data);
		List<CompoundTag> list = (List<CompoundTag>) talis.getValue().get("i").getValue();
		return getTalisInInv(list);
	}

	public static int[] getTalisInInv(List<CompoundTag> list)
	{
		int[] talisCount = new int[6];
		for (CompoundTag tag : list)
		{
			if (!getType(tag).equals("Talisman"))
				continue;
			String rarity = getRarity(tag);
			if (rarity.equals("Mythical"))
				talisCount[5]++;
			if (rarity.equals("Legendary"))
				talisCount[4]++;
			if (rarity.equals("Epic"))
				talisCount[3]++;
			if (rarity.equals("Rare"))
				talisCount[2]++;
			if (rarity.equals("Uncommon"))
				talisCount[1]++;
			if (rarity.equals("Common"))
				talisCount[0]++;
		}
		return talisCount;
	}

	private static String getRarity(CompoundTag tag)
	{
		if (tag.getValue().size() == 0)
			return "None";
		CompoundTag tag1 = (CompoundTag) tag.getValue().get("tag");
		CompoundTag tag2 = (CompoundTag) tag1.getValue().get("display");
		List<Tag<String>> stats = (List<Tag<String>>) tag2.getValue().get("Lore").getValue();

		String rarity = stats.get(stats.size() - 1).getValue();

		if (rarity.contains("§d§lMYTHICAL"))
			return "Mythical";
		if (rarity.contains("§6§lLEGENDARY"))
			return "Legendary";
		if (rarity.contains("§5§lEPIC"))
			return "Epic";
		if (rarity.contains("§9§lRARE"))
			return "Rare";
		if (rarity.contains("§a§lUNCOMMON"))
			return "Uncommon";
		return "Common";
	}

	private static String getType(CompoundTag tag)
	{
		if (tag.getValue().size() == 0)
			return "None";
		CompoundTag tag1 = (CompoundTag) tag.getValue().get("tag");
		CompoundTag tag2 = (CompoundTag) tag1.getValue().get("display");

		String name = (String) tag2.getValue().get("Name").getValue();
		if (name.contains("Backpack"))
			return "Backpack";

		List<Tag<String>> stats = (List<Tag<String>>) tag2.getValue().get("Lore").getValue();

		String rarity = stats.get(stats.size() - 1).getValue();

		if (rarity.contains("SWORD") || rarity.contains("BOW"))
			return "Weapon";
		else if (rarity.contains("HELMET"))
			return "Helmet";
		else if (rarity.contains("CHESTPLATE") || rarity.contains("JACKET"))
			return "Chestplate";
		else if (rarity.contains("LEGGINGS") || rarity.contains("PANTS"))
			return "Leggings";
		else if (rarity.contains("BOOTS") || rarity.contains("OXFORDS"))
			return "Boots";
		else if (rarity.contains("ACCESSORY"))
			return "Talisman";
		return "None";
	}

	public static Tag decodeNbt(String gzippedNbt)
	{
		Tag tag = null;
		try (NBTInputStream gzip = new NBTInputStream(
				Base64.getDecoder().wrap(new ByteArrayInputStream(gzippedNbt.getBytes("utf-8")))))
		{
			tag = gzip.readTag();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return tag;
	}

	public static Tag decodeNbt(byte[] gzippedNbt)
	{
		Tag tag = null;
		try (NBTInputStream gzip = new NBTInputStream(new ByteArrayInputStream(gzippedNbt)))
		{
			tag = gzip.readTag();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return tag;
	}

	public static Equipment equipmentFromTag(CompoundTag tag)
	{
		if (tag.getValue().size() == 0)
			return Equipment.Armor("None", 0, 0, 0, 0, 0, 0, 0, 0);

		int id = ((Tag<Short>) tag.getValue().get("id")).getValue();
		boolean isArmor = false;
		if ((id >= 298 && id <= 317) || id == 397)
			isArmor = true;
		CompoundTag tag1 = (CompoundTag) tag.getValue().get("tag");
		CompoundTag tag2 = (CompoundTag) tag1.getValue().get("display");
		List<Tag<String>> stats = (List<Tag<String>>) tag2.getValue().get("Lore").getValue();
		Tag<String> name = (Tag<String>) tag2.getValue().get("Name");
		boolean isSup = false;
		if (name.getValue().contains("Superior Dragon"))
			isSup = true;

		double dmg = 0, str = 0, cd = 0, cc = 0, ats = 0, hp = 0, def = 0, intel = 0, spd = 0;
		for (Tag<String> stat : stats)
		{
			String line = stat.getValue();
			if (line.contains("7Damage:"))
			{
				Matcher matcher = strPat.matcher(line);
				dmg = getStatFromMatcher(matcher);
			} else if (line.contains("7Strength:"))
			{
				Matcher matcher = strPat.matcher(line);
				str = getStatFromMatcher(matcher);
			} else if (line.contains("7Crit Damage:"))
			{
				Matcher matcher = strPat.matcher(line);
				cd = getStatFromMatcher(matcher);
			} else if (line.contains("7Crit Chance:"))
			{
				Matcher matcher = strPat.matcher(line);
				cc = getStatFromMatcher(matcher);
			} else if (line.contains("7Attack Speed:"))
			{
				Matcher matcher = strPat.matcher(line);
				ats = getStatFromMatcher(matcher);
			} else if (line.contains("7Health:"))
			{
				Matcher matcher = hpPat.matcher(line);
				hp = getStatFromMatcher(matcher);
			} else if (line.contains("7Defense:"))
			{
				Matcher matcher = hpPat.matcher(line);
				def = getStatFromMatcher(matcher);
			} else if (line.contains("7Intelligence:"))
			{
				Matcher matcher = hpPat.matcher(line);
				intel = getStatFromMatcher(matcher);
			} else if (line.contains("7Speed:"))
			{
				Matcher matcher = hpPat.matcher(line);
				spd = getStatFromMatcher(matcher);
			}
		}

		if (isArmor)
		{
			if (isSup)
				return Equipment.SupPiece(name.getValue(), str, cd, cc, ats, def, hp, intel, spd);
			else
				return Equipment.Armor(name.getValue(), str, cd, cc, ats, def, hp, intel, spd);
		} else
			return Equipment.Weapon(name.getValue(), dmg, str, cd, cc, ats, def, hp, intel, spd);
	}

	public static int lvlFromXp(double xp)
	{
		int lvl = 0;
		while (xp > 0)
		{
			if (lvl == 50)
				return lvl;
			xp -= skillXpTable[lvl];
			lvl++;
		}
		return lvl - 1;
	}

	public static double statsFromLvl(int lvl, String skill)
	{
		double bonus = 0;
		if (skill.equals("farming"))
		{
			bonus = Math.min(lvl, 14) * 2 + Math.max(0, Math.min(lvl, 19) - 14) * 3
					+ Math.max(0, Math.min(lvl, 25) - 19) * 4 + Math.max(0, lvl - 25) * 5;
		} else if (skill.equals("mining"))
		{
			bonus = Math.min(lvl, 14) + Math.max(0, lvl - 14) * 2;
		} else if (skill.equals("combat"))
		{
			bonus = lvl;
		} else if (skill.equals("foraging"))
		{
			bonus = Math.min(lvl, 14) + Math.max(0, lvl - 14) * 2;
		} else if (skill.equals("fishing"))
		{
			bonus = Math.min(lvl, 14) * 2 + Math.max(0, Math.min(lvl, 19) - 14) * 3
					+ Math.max(0, Math.min(lvl, 25) - 19) * 4 + Math.max(0, lvl - 25) * 5;
		} else if (skill.equals("enchanting"))
		{
			bonus = Math.min(lvl, 14) + Math.max(0, lvl - 14) * 2;
		} else if (skill.equals("alchemy"))
		{
			bonus = Math.min(lvl, 14) + Math.max(0, lvl - 14) * 2;
		} else if (skill.equals("taming"))
		{
			bonus = lvl;
		}
		return bonus;
	}

	public static PlayerStats baseStats(JsonObject profile)
	{
		double str = 0, cd = 50, cc = 20, ats = 0, hp = 100, def = 0, intel = 0, spd = 100, mf = 10, pl = 0, scc = 20;
		int fairySouls = profile.get("fairy_exchanges").getAsInt();
		hp += fairySoulHp[fairySouls];
		def += fairySoulDef[fairySouls];
		str += fairySoulDef[fairySouls];
		spd += fairySouls / 10;

		hp += statsFromLvl(lvlFromXp(profile.get("experience_skill_farming").getAsDouble()), "farming");
		hp += statsFromLvl(lvlFromXp(profile.get("experience_skill_fishing").getAsDouble()), "fishing");
		def += statsFromLvl(lvlFromXp(profile.get("experience_skill_mining").getAsDouble()), "mining");
		cc += statsFromLvl(lvlFromXp(profile.get("experience_skill_combat").getAsDouble()), "combat");
		str += statsFromLvl(lvlFromXp(profile.get("experience_skill_foraging").getAsDouble()), "foraging");
		intel += statsFromLvl(lvlFromXp(profile.get("experience_skill_enchanting").getAsDouble()), "enchanting");
		intel += statsFromLvl(lvlFromXp(profile.get("experience_skill_alchemy").getAsDouble()), "alchemy");
		if (profile.has("experience_skill_taming"))
			pl += statsFromLvl(lvlFromXp(profile.get("experience_skill_taming").getAsDouble()), "taming");

		JsonObject rev = profile.get("slayer_bosses").getAsJsonObject().get("zombie").getAsJsonObject()
								.get("claimed_levels").getAsJsonObject();
		if (rev.has("level_1"))
			hp += 2;
		if (rev.has("level_2"))
			hp += 2;
		if (rev.has("level_3"))
			hp += 3;
		if (rev.has("level_4"))
			hp += 3;
		if (rev.has("level_5"))
			hp += 4;
		if (rev.has("level_6"))
			hp += 4;
		if (rev.has("level_7"))
			hp += 5;
		if (rev.has("level_8"))
			hp += 5;
		if (rev.has("level_9"))
			hp += 6;

		JsonObject tara = profile.get("slayer_bosses").getAsJsonObject().get("spider").getAsJsonObject()
								 .get("claimed_levels").getAsJsonObject();
		if (tara.has("level_1"))
			cd += 1;
		if (tara.has("level_2"))
			cd += 1;
		if (tara.has("level_3"))
			cd += 1;
		if (tara.has("level_4"))
			cd += 1;
		if (tara.has("level_5"))
			cd += 2;
		if (tara.has("level_6"))
			cd += 2;
		if (tara.has("level_7"))
			cc += 1;
		if (tara.has("level_8"))
			cd += 3;
		if (tara.has("level_9"))
			cd += 3;

		JsonObject sven = profile.get("slayer_bosses").getAsJsonObject().get("wolf").getAsJsonObject()
								 .get("claimed_levels").getAsJsonObject();
		if (sven.has("level_1"))
			spd += 1;
		if (sven.has("level_2"))
			hp += 2;
		if (sven.has("level_3"))
			spd += 1;
		if (sven.has("level_4"))
			hp += 2;
		if (sven.has("level_5"))
			cd += 1;
		if (sven.has("level_6"))
			hp += 3;
		if (sven.has("level_7"))
			cd += 2;
		if (sven.has("level_8"))
			spd += 1;
		if (sven.has("level_9"))
			hp += 5;

		return PlayerStats.fromStatsOnly(str, cd, ats, cc, def, hp, intel, mf, pl, spd);
	}

	public static PlayerEquipment getEquipment(List<CompoundTag> inv)
	{
		PlayerEquipment p = new PlayerEquipment();
		for (CompoundTag tag : inv)
		{
			if (tag.getValue().size() == 0)
				continue;
			String type = getType(tag);
			if (type.equals("None"))
				continue;
			else if (type.equals("Backpack"))
			{

				CompoundTag tag1 = (CompoundTag) tag.getValue().get("tag");
				CompoundTag tag2 = (CompoundTag) tag1.getValue().get("ExtraAttributes");
				Collection<Tag<?>> values = tag2.getValue().values();
				Optional<Tag<?>> optional = values.stream().filter(n -> n instanceof ByteArrayTag).findFirst();
				if (optional.isPresent())
				{
					ByteArrayTag backpackData = (ByteArrayTag) optional.get();
					byte[] bytes = backpackData.getValue();
					CompoundTag backpackNbt = (CompoundTag) decodeNbt(bytes);
					PlayerEquipment backpackEquip = getEquipment(
							(List<CompoundTag>) backpackNbt.getValue().get("i").getValue());
					p.boots.addAll(backpackEquip.boots);
					p.legs.addAll(backpackEquip.legs);
					p.chests.addAll(backpackEquip.chests);
					p.helms.addAll(backpackEquip.helms);
					p.swords.addAll(backpackEquip.swords);
				}
				continue;
			}
			Equipment equipment = equipmentFromTag(tag);
			if (type.equals("Weapon"))
				p.swords.add(equipment);
			else if (type.equals("Helmet"))
				p.helms.add(equipment);
			else if (type.equals("Chestplate"))
				p.chests.add(equipment);
			else if (type.equals("Leggings"))
				p.legs.add(equipment);
			else if (type.equals("Boots"))
				p.boots.add(equipment);
		}

		return p;
	}

	private static int getStatFromMatcher(Matcher m)
	{
		m.find();
		String group = m.group().substring(2);
		int stat = Integer.parseInt(group);
		if (m.group().substring(1, 2).equals("-"))
			stat *= -1;
		return stat;
	}
}
