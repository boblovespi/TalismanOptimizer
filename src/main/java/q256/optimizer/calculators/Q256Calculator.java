package q256.optimizer.calculators;

import q256.optimizer.PlayerStats;

import java.util.ArrayList;

/**
 * Created by Willi on 5/24/2020.
 */
public class Q256Calculator extends Calculator
{
	public Q256Calculator(PlayerStats stats)
	{
		super(stats);
	}

	@Override
	public void start()
	{
		atcSpdLoop();
	}

	private void atcSpdLoop()
	{
		boolean exit = false;
		currentReforges[0][0] = stats.numberOfTalismans[0];
		currentReforges[1][0] = stats.numberOfTalismans[1];
		currentReforges[2][0] = stats.numberOfTalismans[2];
		currentReforges[3][2] = stats.numberOfTalismans[3];
		currentReforges[4][2] = stats.numberOfTalismans[4];
		currentReforges[5][2] = stats.numberOfTalismans[5];
		optimizeSetUp();
		int[][] atcSpdConversions = new int[6][3];
		atcSpdConversions[0] = new int[] { 1, 0, 5 };
		atcSpdConversions[1] = new int[] { 2, 0, 3 };
		atcSpdConversions[2] = new int[] { 3, 2, 3 };
		atcSpdConversions[3] = new int[] { 3, 3, 5 };
		atcSpdConversions[4] = new int[] { 5, 2, 5 };
		atcSpdConversions[5] = new int[] { 4, 2, 3 };

		for (int ii = 0; ii < 6; ii++)
		{
			while (!exit && currentReforges[atcSpdConversions[ii][0]][atcSpdConversions[ii][1]] > 0)
			{
				int tempDPS = bestDPS;
				currentReforges[atcSpdConversions[ii][0]][atcSpdConversions[ii][1]]--;
				currentReforges[atcSpdConversions[ii][0]][atcSpdConversions[ii][2]]++;
				optimizeSetUp();
				if (bestDPS == tempDPS)
					exit = true;
			}
		}
	}

	private void optimizeSetUp()
	{
		int[][] tempReforges = new int[6][6];
		for (int ii = 0; ii < 6; ii++)
		{
			for (int jj = 0; jj < 6; jj++)
			{
				tempReforges[ii][jj] = currentReforges[ii][jj];
			}
		}
		int minStr = (int) stats.baseStr + 5 * tempReforges[3][2] + 8 * tempReforges[4][2] + 12 * tempReforges[5][2]
				+ tempReforges[1][5] + tempReforges[2][3] + 3 * tempReforges[3][5] + 2 * tempReforges[3][3]
				+ 3 * tempReforges[4][3] + 4 * tempReforges[5][5];

		int maxStr = (int) stats.baseStr + 5 * tempReforges[3][2] + 8 * tempReforges[4][2] + 12 * tempReforges[5][2]
				+ tempReforges[1][5] + tempReforges[2][3] + 3 * tempReforges[3][5] + 2 * tempReforges[3][3]
				+ 3 * tempReforges[4][3] + 4 * tempReforges[5][5] + 4 * tempReforges[0][0] + 5 * tempReforges[1][0]
				+ 7 * tempReforges[2][0];

		int totalStrCd = (int) stats.baseStr + (int) stats.baseCd + 4 * tempReforges[0][0] + 5 * tempReforges[1][0]
				+ 7 * tempReforges[2][0] + 10 * tempReforges[3][2] + 16 * tempReforges[4][2] + 24 * tempReforges[5][2]
				+ 3 * tempReforges[1][5] + 6 * tempReforges[2][3] + 4 * tempReforges[3][5] + 9 * tempReforges[3][3]
				+ 13 * tempReforges[4][3] + 13 * tempReforges[5][5];

		int optimalStr;

		boolean exit = false;
		while (!exit)
		{

			double dd = stats.baseDmg;
			double tt = totalStrCd * (1 + stats.statMultiplier);
			if (stats.hasMastiff && !stats.hasShaman)
				optimalStr = (int) ((tt - 5 * dd + 100 + Math
						.sqrt(tt * tt + 5 * dd * tt + 500 * tt + 25 * dd * dd + 500 * dd + 70000)) / 3 / (1
						+ stats.statMultiplier) + 0.5);
			else if (stats.hasMastiff && stats.hasShaman)
				optimalStr = (int) ((9 * tt + 5 * dd + 400 - Math
						.sqrt(21 * tt * tt + 30 * dd * tt + 6000 * tt + 25 * dd * dd - 2000 * dd + 1120000)) / 12 / (1
						+ stats.statMultiplier) + 0.5);
			else
				optimalStr = (int) (
						(tt - 5 * dd + Math.sqrt(25 * dd * dd + tt * tt + 30000 + 5 * tt * dd + 300 * tt)) / 3 / (1
								+ stats.statMultiplier) + 0.5);

			if (optimalStr >= minStr && optimalStr <= maxStr)
			{
				tempReforges[0][4] = stats.numberOfTalismans[0];
				tempReforges[1][4] = stats.numberOfTalismans[1] - tempReforges[1][5];
				tempReforges[2][4] = stats.numberOfTalismans[2] - tempReforges[2][3];
				tempReforges[0][0] = 0;
				tempReforges[1][0] = 0;
				tempReforges[2][0] = 0;

				int diff = optimalStr - minStr;
				approachLargerStr(diff, tempReforges);
				checkIfBest(tempReforges);
				exit = true;
			} else if (optimalStr < minStr)
			{
				tempReforges[0][4] = stats.numberOfTalismans[0];
				tempReforges[1][4] = stats.numberOfTalismans[1] - tempReforges[1][5];
				tempReforges[2][4] = stats.numberOfTalismans[2] - tempReforges[2][3];
				tempReforges[0][0] = 0;
				tempReforges[1][0] = 0;
				tempReforges[2][0] = 0;

				checkIfBest(tempReforges);
				exit = true;
				if (tempReforges[3][2] > 0)
				{
					tempReforges[3][2]--;
					tempReforges[3][4]++;
					minStr -= 5;
					exit = false;
				} else if (tempReforges[4][2] > 0)
				{
					totalStrCd--;
					tempReforges[4][2]--;
					tempReforges[4][4]++;
					minStr -= 7;
					exit = false;
				} else if (tempReforges[5][2] > 0)
				{
					totalStrCd -= 4;
					tempReforges[5][2]--;
					tempReforges[5][4]++;
					minStr -= 8;
					exit = false;
				}
			} else if (optimalStr > maxStr)
			{
					/*tempReforges[0][0] = numberOfTalismans[0];
					tempReforges[1][0] = numberOfTalismans[1]-tempReforges[1][5];
                    tempReforges[2][0] = numberOfTalismans[2]-tempReforges[2][3];
                    tempReforges[0][4] = 0;
                    tempReforges[1][4] = 0;
                    tempReforges[2][4] = 0;*/

				checkIfBest(tempReforges);
				exit = true;
				if (tempReforges[3][2] > 0)
				{
					tempReforges[3][2]--;
					tempReforges[3][0]++;
					maxStr += 5;
					exit = false;
				} else if (tempReforges[4][2] > 0)
				{
					totalStrCd--;
					tempReforges[4][2]--;
					tempReforges[4][0]++;
					maxStr += 7;
					exit = false;
				} else if (tempReforges[5][2] > 0)
				{
					totalStrCd -= 4;
					tempReforges[5][2]--;
					tempReforges[5][0]++;
					maxStr += 8;
					exit = false;
				}
			}
		}
	}

	private void approachLargerStr(int diff, int[][] tempReforges)
	{
		// amount, ending reforge, rarity
		ArrayList<Integer[]> conversions = new ArrayList<>();
		conversions.add(new Integer[] { 10, 0, 3 });
		conversions.add(new Integer[] { 7, 0, 2 });
		conversions.add(new Integer[] { 5, 2, 3 });
		conversions.add(new Integer[] { 5, 0, 1 });
		conversions.add(new Integer[] { 4, 0, 0 });
		conversions.add(new Integer[] { 3, 1, 1 });
		conversions.add(new Integer[] { 2, 1, 0 });
		conversions.add(new Integer[] { 1, 3, 1 });
		conversions.add(new Integer[] { 1, 3, 0 });

		for (Integer[] ii : conversions)
		{
			while (diff >= ii[0] && tempReforges[ii[2]][4] > 0)
			{
				diff -= ii[0];
				tempReforges[ii[2]][4]--;
				tempReforges[ii[2]][ii[1]]++;
			}
		}
	}
}
