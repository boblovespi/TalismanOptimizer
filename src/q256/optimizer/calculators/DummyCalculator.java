package q256.optimizer.calculators;

import q256.optimizer.PlayerStats;

/**
 * Created by Willi on 5/24/2020.
 */
public class DummyCalculator extends Calculator
{
	public DummyCalculator(PlayerStats stats)
	{
		super(stats);
	}

	@Override
	public void start()
	{
		System.out.println("noop lol");
	}
}
