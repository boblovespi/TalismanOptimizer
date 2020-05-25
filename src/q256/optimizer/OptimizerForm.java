package q256.optimizer;

import q256.optimizer.calculators.Calculator;
import q256.optimizer.calculators.Q256Calculator;

import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class OptimizerForm
{
	private static ConfigManager configManager;
	private CalculationThread calcThread;
	private JTable talisTable;
	private JButton calculateButton;
	private JTabbedPane optimizerTab;
	private JTextArea console;
	private JPanel talisNumbers;
	private JSpinner commonCount;
	private JPanel mainPanel;
	private JSpinner uncommonCount;
	private JSpinner rareCount;
	private JSpinner epicCount;
	private JSpinner legendaryCount;
	private JSpinner mythicCount;
	private JPanel stats;
	private JSpinner weaponDmg;
	private JSpinner cd;
	private JSpinner ats;
	private JSpinner str;
	private JSpinner statBoost;
	private JSpinner atsMult;
	private JCheckBox usingMastiff;
	private JCheckBox usingShaman;
	private JScrollBar scrollBar1;
	private JTextField totalDmg;
	private JTextField totalStr;
	private JTextField totalCd;
	private JTextField totalAts;
	private JTextField dps;
	private JTextField eHealth;
	private JTextField eDef;
	private JTextField eInt;
	private JTextField eSpd;

	public OptimizerForm()
	{
		// a bunch of boilerplate for the gui
		talisTable.setModel(new TalisCountTableModel());
		talisTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		talisTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column)
			{
				if (row > 0)
					setForeground(Constants.rarityColors[row - 1]);
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		});
		talisTable.setDefaultRenderer(int.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column)
			{
				if (row > 0)
					setForeground(Color.LIGHT_GRAY);
				else
					setForeground(Color.BLACK);
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		});

		// listener for the button (maybe allow the button to cancel a calculation if one is running?)
		calculateButton.addActionListener(e ->
		{
			if (calcThread != null)
				return;
			double statMult = (int) statBoost.getValue() / 100d;
			PlayerStats stats = PlayerStats
					.fromDmgStatsOnly((int) str.getValue() / (statMult + 1), (int) cd.getValue() / (statMult + 1),
							(int) ats.getValue() / (statMult + 1), statMult, (int) weaponDmg.getValue() + 5,
							usingMastiff.isSelected(), usingShaman.isSelected(), getNumOfTalis());
			Calculator calculator = new Q256Calculator(stats);
			calcThread = new CalculationThread(calculator);
			calcThread.onFinish(this::displayStats);
			calcThread.run();
		});
	}

	public static void main(String[] args)
	{
		// read configs
		// could store stats in config in the future (so the program remembers what talis you have)
		configManager = new ConfigManager();
		configManager.loadConfigs("config.cfg");

		// init reforge stats
		Constants.initReforgeStats();

		// init the lnf
		initLNF();

		// jframe stuff
		JFrame frame = new JFrame("Talisman Optimizer " + Constants.version + " - by q256");
		frame.setContentPane(new OptimizerForm().mainPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	// function that sets the look and feel of the app
	private static void initLNF()
	{
		try
		{
			switch (configManager.lookAndFeel)
			{
			case NORMAL:
				MetalLookAndFeel.setCurrentTheme(new OceanTheme());
				UIManager.setLookAndFeel(new MetalLookAndFeel());
				break;
			case SYSTEM:
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				break;
			case METAL:
				MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
				UIManager.setLookAndFeel(new MetalLookAndFeel());
				break;
			}
		} catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e)
		{
			e.printStackTrace();
		}
	}

	// helper func to construct an array
	private int[] getNumOfTalis()
	{
		int[] num = new int[6];
		num[0] = (int) commonCount.getValue();
		num[1] = (int) uncommonCount.getValue();
		num[2] = (int) rareCount.getValue();
		num[3] = (int) epicCount.getValue();
		num[4] = (int) legendaryCount.getValue();
		num[5] = (int) mythicCount.getValue();
		return num;
	}

	// callback when a calculatorthread finishes
	public void displayStats(Calculator calculator)
	{
		// clean up the old thread and allow for a new thread
		calcThread = null;

		writeLine("Time (ms): " + calculator.getMills());
		populateTalisTable(calculator.getCounts());

		totalDmg.setText(String.valueOf(calculator.getBestDmg()));
		dps.setText(String.valueOf(calculator.getBestDps()));
		totalStr.setText(String.valueOf(calculator.getBestStats().baseStr));
		totalAts.setText(String.valueOf(calculator.getBestStats().baseAts));
		totalCd.setText(String.valueOf(calculator.getBestStats().baseCd));
		eDef.setText(String.valueOf(calculator.getBestStats().baseDef));
		eHealth.setText(String.valueOf(calculator.getBestStats().baseHp));
		eInt.setText(String.valueOf(calculator.getBestStats().baseInt));
		eSpd.setText(String.valueOf(calculator.getBestStats().baseSpd));
		mainPanel.updateUI();
	}

	private void writeLine(String line)
	{
		console.setText(console.getText() + line + "\n");
	}

	private void populateTalisTable(int[][] counts)
	{
		((TalisCountTableModel) talisTable.getModel()).setCounts(counts);
	}
}
