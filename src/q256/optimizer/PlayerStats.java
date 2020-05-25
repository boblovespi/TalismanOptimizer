package q256.optimizer;

/**
 * A data class that stores a player's base stats
 */
public class PlayerStats
{
	public final double baseStr;
	public final double baseCd;
	public final double baseAts;
	public final double baseCc;
	public final double baseDef;
	public final double baseHp;
	public final double baseInt;
	public final double baseMf;
	public final double basePl;
	public final double statMultiplier;
	public final double baseDmg;
	public final int[][] currentReforges;
	public final boolean hasMastiff;
	public final boolean hasShaman;
	public final int[] numberOfTalismans;
	public final double baseSpd;

	private PlayerStats(double baseStr, double baseCd, double baseAts, double baseCc, double baseDef, double baseHp,
			double baseInt, double baseMf, double basePl, double baseSpd, double statMultiplier, double baseDmg,
			int[][] currentReforges, boolean hasMastiff, boolean hasShaman, int[] numberOfTalismans)
	{
		this.baseStr = baseStr;
		this.baseCd = baseCd;
		this.baseAts = baseAts;
		this.baseCc = baseCc;
		this.baseDef = baseDef;
		this.baseHp = baseHp;
		this.baseInt = baseInt;
		this.baseMf = baseMf;
		this.basePl = basePl;
		this.baseSpd = baseSpd;
		this.statMultiplier = statMultiplier;
		this.baseDmg = baseDmg;
		this.currentReforges = currentReforges;
		this.hasMastiff = hasMastiff;
		this.hasShaman = hasShaman;
		this.numberOfTalismans = numberOfTalismans;
	}

	public static PlayerStats fromDmgStatsOnly(double baseStr, double baseCd, double baseAts, double statMultiplier,
			double baseDmg, boolean hasMastiff, boolean hasShaman, int[] numberOfTalismans)
	{
		return new PlayerStats(baseStr, baseCd, baseAts, 0, 0, 0, 0, 0, 0, 0, statMultiplier, baseDmg,
				new int[][] { new int[6], new int[6], new int[6], new int[6], new int[6], new int[6] }, hasMastiff,
				hasShaman, numberOfTalismans);
	}

	public static PlayerStats fromStatsOnly(double baseStr, double baseCd, double baseAts, double baseCc,
			double baseDef, double baseHp, double baseInt, double baseMf, double basePl, double baseSpd)
	{
		return new PlayerStats(baseStr, baseCd, baseAts, baseCc, baseDef, baseHp, baseInt, baseMf, basePl, baseSpd, 0,
				0, null, false, false, null);
	}
}
