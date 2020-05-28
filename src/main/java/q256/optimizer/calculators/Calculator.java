package q256.optimizer.calculators;

import q256.optimizer.PlayerStats;

import static q256.optimizer.Constants.reforgeStats;

/**
 * Abstract class for an algorithm which optimizes talismans to any specification
 */
public abstract class Calculator
{
	protected PlayerStats stats;

	protected int[][] currentReforges = new int[6][6];
	protected int[][] bestReforges = new int[6][6];
	protected int bestDamage = Integer.MIN_VALUE;
	protected int bestDPS = Integer.MIN_VALUE;
	private long totalTime = 0;
	private long startTime = 0;
	private PlayerStats bestStats;

	public Calculator(PlayerStats stats)
	{
		this.stats = stats;
		for (int i = 0; i < 6; i++)
		{
			currentReforges[i] = new int[6];
			bestReforges[i] = new int[6];
		}
	}

	// the function that does everything
	public abstract void start();

	// a helper function that compares the tempReforges to the current best reforges
	protected void checkIfBest(int[][] tempReforges)
	{
		double str = stats.baseStr;
		double cd = stats.baseCd;
		double atcSpd = stats.baseAts;
		for (int ii = 0; ii < 6; ii++)
		{
			for (int jj = 0; jj < 6; jj++)
			{
				if (tempReforges[ii][jj] != 0)
				{
					str += tempReforges[ii][jj] * reforgeStats[jj][0][ii] * (1 + stats.statMultiplier);
					cd += tempReforges[ii][jj] * reforgeStats[jj][1][ii] * (1 + stats.statMultiplier);
					atcSpd += tempReforges[ii][jj] * reforgeStats[jj][2][ii] * (1 + stats.statMultiplier);
				}
			}
		}
		// str *= 1 + stats.statMultiplier;
		// cd *= 1 + stats.statMultiplier;
		// atcSpd *= 1 + stats.statMultiplier;
		double damage = calcDamage(str, cd);
		double DPS = calcDPS(str, cd, atcSpd);
		if (DPS > bestDPS)
		{
			bestDamage = (int) damage;
			bestDPS = (int) DPS;
			for (int ii = 0; ii < 6; ii++)
			{
				for (int jj = 0; jj < 6; jj++)
				{
					bestReforges[ii][jj] = tempReforges[ii][jj];
				}
			}
		}
	}

	// calculates damage based on str and cd
	protected double calcDamage(double str, double cd)
	{
		if (stats.hasMastiff && !stats.hasShaman)
			return (stats.baseDmg + str / 5) * (1 + str / 100) * (1 + cd / 200);
		else if (stats.hasMastiff && stats.hasShaman)
			return (stats.baseDmg + cd + str / 5) * (1 + str / 100) * (1 + cd / 200);
		else
			return (stats.baseDmg + str / 5) * (1 + str / 100) * (1 + cd / 100);
	}

	// calculates dps based on str, cd, and ats
	protected double calcDPS(double str, double cd, double atcSpd)
	{
		return (calcDamage(str, cd) * (2 + atcSpd * stats.atsMult / 50));
	}

	public void startStopwatch()
	{
		startTime = System.nanoTime();
	}

	public void stopStopwatch()
	{
		totalTime = System.nanoTime() - startTime;
	}

	public double getMills()
	{
		return totalTime / 1000000d;
	}

	public int[][] getCounts()
	{
		return bestReforges;
	}

	public PlayerStats getBestStats()
	{
		return bestStats;
	}

	public void calcBestStats()
	{
		double str = stats.baseStr;
		double cd = stats.baseCd;
		double ac = stats.baseAts;
		double def = 0;
		double hp = 0;
		double spd = 0;
		double intel = 0;
		for (int ii = 0; ii < 6; ii++)
		{
			for (int jj = 0; jj < 6; jj++)
			{
				str += bestReforges[ii][jj] * reforgeStats[jj][0][ii] * (1 + stats.statMultiplier);
				cd += bestReforges[ii][jj] * reforgeStats[jj][1][ii] * (1 + stats.statMultiplier);
				ac += bestReforges[ii][jj] * reforgeStats[jj][2][ii] * (1 + stats.statMultiplier);
				def += bestReforges[ii][jj] * reforgeStats[jj][3][ii] * (1 + stats.statMultiplier);
				hp += bestReforges[ii][jj] * reforgeStats[jj][4][ii] * (1 + stats.statMultiplier);
				spd += bestReforges[ii][jj] * reforgeStats[jj][5][ii] * (1 + stats.statMultiplier);
				intel += bestReforges[ii][jj] * reforgeStats[jj][6][ii] * (1 + stats.statMultiplier);
			}
		}
		bestStats = PlayerStats.fromStatsOnly(str, cd, ac, 0, def, hp, intel, 0, 0, spd);
		bestDamage = (int) calcDamage(str, cd);
		bestDPS = (int) calcDPS(str, cd, ac);
	}

	public int getBestDmg()
	{
		return bestDamage;
	}

	public int getBestDps()
	{
		return bestDPS;
	}
}
