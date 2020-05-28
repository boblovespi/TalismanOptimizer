package q256.optimizer;

import java.util.ArrayList;

/**
 * Created by Willi on 5/25/2020.
 */
public class PlayerEquipment
{
	public ArrayList<Equipment> swords;
	public ArrayList<Equipment> helms;
	public ArrayList<Equipment> chests;
	public ArrayList<Equipment> legs;
	public ArrayList<Equipment> boots;

	public PlayerEquipment()
	{
		swords = new ArrayList<>();
		helms = new ArrayList<>();
		chests = new ArrayList<>();
		legs = new ArrayList<>();
		boots = new ArrayList<>();
	}
}
