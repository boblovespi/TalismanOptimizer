package q256.optimizer;

/**
 * Created by Willi on 5/25/2020.
 */
public class Equipment
{
	public static final Equipment NO_BOOTS = Equipment.Armor("None", 0, 0, 0, 0, 0, 0, 0, 0);
	public static final Equipment NO_LEGS = Equipment.Armor("None", 0, 0, 0, 0, 0, 0, 0, 0);
	public static final Equipment NO_CHEST = Equipment.Armor("None", 0, 0, 0, 0, 0, 0, 0, 0);
	public static final Equipment NO_HELM = Equipment.Armor("None", 0, 0, 0, 0, 0, 0, 0, 0);
	public static final Equipment NO_WEAPON = Equipment.Weapon("None", 0, 0, 0, 0, 0, 0, 0, 0, 0);
	public final String name;
	public final boolean isWeapon;
	public final boolean mastiffOrShaman;
	public final boolean isSuperior;
	public final double dmg;
	public final double str;
	public final double cd;
	public final double cc;
	public final double ats;
	public final double hp;
	public final double def;
	public final double intel;
	public final double mf;
	public final double pl;
	public final double spd;

	private Equipment(String name, boolean isWeapon, boolean mastiffOrShaman, boolean isSuperior, double dmg,
			double str, double cd, double cc, double ats, double hp, double def, double intel, double mf, double pl,
			double spd)
	{
		this.name = name;
		this.isWeapon = isWeapon;
		this.mastiffOrShaman = mastiffOrShaman;
		this.isSuperior = isSuperior;
		this.dmg = dmg;
		this.str = str;
		this.cd = cd;
		this.cc = cc;
		this.ats = ats;
		this.hp = hp;
		this.def = def;
		this.intel = intel;
		this.mf = mf;
		this.pl = pl;
		this.spd = spd;
	}

	public static Equipment SupPiece(String name, double str, double cd, double cc, double ats, double hp, double def,
			double intel, double spd)
	{
		return new Equipment(name, false, false, true, 0, str, cd, cc, ats, hp, def, intel, 0, 0, spd);
	}

	public static Equipment Weapon(String name, double dmg, double str, double cd, double cc, double ats, double hp,
			double def, double intel, double spd)
	{
		return new Equipment(name, true, false, false, dmg, str, cd, cc, ats, hp, def, intel, 0, 0, spd);
	}

	public static Equipment Armor(String name, double str, double cd, double cc, double ats, double hp, double def,
			double intel, double spd)
	{
		return new Equipment(name, false, false, false, 0, str, cd, cc, ats, hp, def, intel, 0, 0, spd);
	}

	@Override
	public String toString()
	{
		return name;
	}
}
