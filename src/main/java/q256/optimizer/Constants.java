package q256.optimizer;

import java.awt.*;

/**
 * A bunch of constants
 */
public class Constants
{
	public static String version = "v3.1-SNAPSHOT";
	public static String[] rarityNames = { "Common", "Uncommon", "Rare", "Epic", "Legendary", "Mythic" };
	public static String[] reforgeNames = { "Forceful", "Superior", "Strong", "Itchy", "Hurtful", "Strange" };
	public static Color[] rarityColors = { Color.gray, new Color(0, 206, 55), Color.blue, new Color(150, 0, 200), Color.orange,
			new Color(240, 120, 240) };
	public static int[][][] reforgeStats = new int[6][7][6];

	public static void initReforgeStats()
	{
		//str, cd, atk spd, def, health, speed, int
		for (int ii = 0; ii < 6; ii++)
		{
			for (int jj = 0; jj < 7; jj++)
			{
				reforgeStats[ii][jj] = new int[] { 0, 0, 0, 0, 0, 0 };
			}
		}
		reforgeStats[0][0] = new int[] { 4, 5, 7, 10, 15, 20 };

		reforgeStats[1][0] = new int[] { 2, 3, 4, 0, 0, 0 };
		reforgeStats[1][1] = new int[] { 2, 2, 2, 0, 0, 0 };

		reforgeStats[2][0] = new int[] { 0, 0, 3, 5, 8, 12 };
		reforgeStats[2][1] = new int[] { 0, 0, 3, 5, 8, 12 };
		reforgeStats[2][3] = new int[] { 0, 0, 1, 2, 3, 4 };

		reforgeStats[3][0] = new int[] { 1, 1, 1, 2, 3, 4 };
		reforgeStats[3][1] = new int[] { 3, 4, 5, 7, 10, 15 };
		reforgeStats[3][2] = new int[] { 0, 0, 1, 1, 1, 1 };

		reforgeStats[4][1] = new int[] { 4, 5, 7, 10, 15, 20 };

		reforgeStats[5][0] = new int[] { 0, 1, 0, 3, 0, 4 };
		reforgeStats[5][1] = new int[] { 0, 2, 0, 1, 0, 9 };
		reforgeStats[5][2] = new int[] { 0, 2, 0, 4, 0, 5 };
		reforgeStats[5][3] = new int[] { 0, 3, 0, -1, 0, 1 };

		reforgeStats[5][4] = new int[] { 0, 2, 0, 7, 0, 0 };
		reforgeStats[5][5] = new int[] { 0, 0, 0, 0, 0, 3 };
		reforgeStats[5][6] = new int[] { 0, -1, 0, 0, 0, 11 };
	}
}
