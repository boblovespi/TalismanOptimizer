package q256.optimizer.api;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.Tag;
import com.google.gson.JsonObject;
import q256.optimizer.ConfigManager;
import q256.optimizer.Equipment;
import q256.optimizer.PlayerEquipment;
import q256.optimizer.PlayerStats;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Willi on 5/25/2020.
 */
public class APITest
{
	public static void main(String[] args) throws Exception
	{
		ConfigManager configManager = new ConfigManager();
		configManager.loadConfigs("config.cfg");
		APIReader.initialize(configManager.apiKey);
		CompletableFuture<String> uuidFuture = APIReader.API.nameToUUID("jjww2");
		String uuid = uuidFuture.get();
		//System.out.println(uuid);
		CompletableFuture<JsonObject> profilesFuture = APIReader.API.profilesData(uuid);
		JsonObject profiles = profilesFuture.get();
		String[] fruits = Parser.getFruits(profiles);
		JsonObject profile = Parser.getPlayerProfile(profiles, fruits[1], uuid);
		String armor = profile.get("inv_armor").getAsJsonObject().get("data").getAsString();

		PlayerEquipment p = new PlayerEquipment();
		CompoundTag tag = (CompoundTag) Parser.decodeNbt(armor);
		//System.out.println(tag);

		ListTag<CompoundTag> i = (ListTag<CompoundTag>) tag.getValue().get("i");

		CompoundTag compoundTag = i.getValue().get(0);
		Equipment equipment = Parser.equipmentFromTag(compoundTag);
		p.boots.add(equipment);

		compoundTag = i.getValue().get(1);
		equipment = Parser.equipmentFromTag(compoundTag);
		p.legs.add(equipment);

		compoundTag = i.getValue().get(2);
		equipment = Parser.equipmentFromTag(compoundTag);
		p.chests.add(equipment);

		compoundTag = i.getValue().get(3);
		equipment = Parser.equipmentFromTag(compoundTag);
		p.helms.add(equipment);

		//System.out.println(p);

		String talis = profile.get("talisman_bag").getAsJsonObject().get("data").getAsString();
		Tag talisTag = Parser.decodeNbt(talis);
		//System.out.println(talisTag);
		//System.out.println(Arrays.toString(Parser.getTalisInBag(profile)));
		String inv = profile.get("inv_contents").getAsJsonObject().get("data").getAsString();
		List<CompoundTag> invItems = ((ListTag<CompoundTag>) ((CompoundTag) Parser.decodeNbt(inv)).getValue().get("i"))
				.getValue();
		// System.out.println(invItems);
		PlayerEquipment playerEquipment = Parser.getEquipment(invItems);

		PlayerStats stats = Parser.baseStats(profile);

		APIReader.API.kill();
	}
}
