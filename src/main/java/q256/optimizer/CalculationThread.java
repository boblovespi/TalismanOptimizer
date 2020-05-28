package q256.optimizer;

import q256.optimizer.calculators.Calculator;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * Created by Willi on 5/24/2020.
 */
public class CalculationThread extends Thread
{

	private Calculator calculator;
	private Consumer<Calculator> consumer;

	public CalculationThread(Calculator calculator)
	{
		super("calculation-thread");
		this.calculator = calculator;
	}

	@Override
	public void run()
	{
		calculator.startStopwatch();
		calculator.start();
		calculator.stopStopwatch();
		calculator.calcBestStats();
		SwingUtilities.invokeLater(() ->
		{
			consumer.accept(calculator);
		});
	}

	public void onFinish(Consumer<Calculator> consumer)
	{
		this.consumer = consumer;
	}
}
