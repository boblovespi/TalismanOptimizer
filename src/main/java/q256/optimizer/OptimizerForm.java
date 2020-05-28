package q256.optimizer;

import q256.optimizer.api.APIReader;
import q256.optimizer.apidialog.APINameInput;
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
	private PlayerStats myStats;
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
	private JScrollBar scrollBar2;
	private JTextArea helpText;
	private JScrollBar creditsScroll;
	private JTextArea creditsText;
	private JButton importButton;
	private JSpinner commonApi;
	private JSpinner uncommonApi;
	private JSpinner rareApi;
	private JSpinner epicApi;
	private JSpinner legendaryApi;
	private JSpinner mythicApi;
	private JComboBox<Equipment> weaponCombo;
	private JSpinner bStr;
	private JSpinner bCc;
	private JSpinner bCd;
	private JSpinner bAts;
	private JSpinner bSpd;
	private JSpinner bHp;
	private JSpinner bDef;
	private JSpinner bInt;
	private JSpinner bMf;
	private JSpinner bPl;
	private JComboBox<Equipment> helmCombo;
	private JComboBox<Equipment> chestCombo;
	private JComboBox<Equipment> legsCombo;
	private JComboBox<Equipment> bootsCombo;
	private PlayerStats baseStats;
	private PlayerEquipment equipment;
	private SpinnerNumberModel[] talisModel;
	private boolean pauseRefresh = false;

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
			double statMult = cast(statBoost.getValue()) / 100d;
			myStats = PlayerStats
					.fromDmgStatsOnly(cast(str.getValue()) / (statMult + 1), cast(cd.getValue()) / (statMult + 1),
							cast(ats.getValue()) / (statMult + 1), statMult, cast(weaponDmg.getValue()) + 5,
							cast(atsMult.getValue()), usingMastiff.isSelected(), usingShaman.isSelected(),
							getNumOfTalis());
			Calculator calculator = new Q256Calculator(myStats);
			calcThread = new CalculationThread(calculator);
			calcThread.onFinish(this::displayStats);
			calcThread.run();
		});
		importButton.addActionListener(e ->
		{
			APINameInput next = new APINameInput();
			next.setCallback(this::apiCallback);
			next.pack();
			next.setLocationRelativeTo(null);
			next.setVisible(true);
		});

		talisModel = new SpinnerNumberModel[6];
		for (int i = 0; i < talisModel.length; i++)
		{
			talisModel[i] = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
		}

		// set the models for all the spinners
		commonCount.setModel(talisModel[0]);
		commonApi.setModel(talisModel[0]);
		uncommonCount.setModel(talisModel[1]);
		uncommonApi.setModel(talisModel[1]);
		rareCount.setModel(talisModel[2]);
		rareApi.setModel(talisModel[2]);
		epicCount.setModel(talisModel[3]);
		epicApi.setModel(talisModel[3]);
		legendaryCount.setModel(talisModel[4]);
		legendaryApi.setModel(talisModel[4]);
		mythicCount.setModel(talisModel[5]);
		mythicApi.setModel(talisModel[5]);

		weaponCombo.setModel(new DefaultComboBoxModel<>());
		helmCombo.setModel(new DefaultComboBoxModel<>());
		chestCombo.setModel(new DefaultComboBoxModel<>());
		legsCombo.setModel(new DefaultComboBoxModel<>());
		bootsCombo.setModel(new DefaultComboBoxModel<>());

		weaponCombo.addActionListener((e) -> refreshBaseStats(true));
		helmCombo.addActionListener((e) -> refreshBaseStats(true));
		chestCombo.addActionListener((e) -> refreshBaseStats(true));
		legsCombo.addActionListener((e) -> refreshBaseStats(true));
		bootsCombo.addActionListener((e) -> refreshBaseStats(true));
		bStr.addChangeListener((e) -> refreshBaseStats(true));
		bCd.addChangeListener((e) -> refreshBaseStats(true));
		bAts.addChangeListener((e) -> refreshBaseStats(true));
		bCc.addChangeListener((e) -> refreshBaseStats(true));
		bDef.addChangeListener((e) -> refreshBaseStats(true));
		bHp.addChangeListener((e) -> refreshBaseStats(true));
		bInt.addChangeListener((e) -> refreshBaseStats(true));
		bSpd.addChangeListener((e) -> refreshBaseStats(true));
		bMf.addChangeListener((e) -> refreshBaseStats(true));
		bPl.addChangeListener((e) -> refreshBaseStats(true));
		str.addChangeListener((e) -> refreshBaseStats(false));
		cd.addChangeListener((e) -> refreshBaseStats(false));
		ats.addChangeListener((e) -> refreshBaseStats(false));

	}

	public static void main(String[] args)
	{
		// read configs
		// could store stats in config in the future (so the program remembers what talis you have)
		configManager = new ConfigManager();
		configManager.loadConfigs("config.cfg");

		// init the api
		APIReader.initialize(configManager.apiKey);

		// init reforge stats
		Constants.initReforgeStats();

		// init the lnf
		initLNF();

		// jframe stuff
		JFrame frame = new JFrame("Talisman Optimizer " + Constants.version + " - by q256");
		frame.setContentPane(new OptimizerForm().mainPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
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

	// called when we are done fetching stuff from api
	public void apiCallback(PlayerStats baseStats, PlayerEquipment equipment, int[] talis)
	{
		pauseRefresh = true;
		this.baseStats = baseStats;
		this.equipment = equipment;
		for (int i = 0; i < 6; i++)
		{
			talisModel[i].setValue(talis[i]);
		}
		weaponCombo.setModel(new DefaultComboBoxModel<>());
		helmCombo.setModel(new DefaultComboBoxModel<>());
		chestCombo.setModel(new DefaultComboBoxModel<>());
		legsCombo.setModel(new DefaultComboBoxModel<>());
		bootsCombo.setModel(new DefaultComboBoxModel<>());

		for (Equipment weapon : equipment.swords)
			weaponCombo.addItem(weapon);
		for (Equipment weapon : equipment.helms)
			helmCombo.addItem(weapon);
		for (Equipment weapon : equipment.chests)
			chestCombo.addItem(weapon);
		for (Equipment weapon : equipment.legs)
			legsCombo.addItem(weapon);
		for (Equipment weapon : equipment.boots)
			bootsCombo.addItem(weapon);

		bStr.setValue(baseStats.baseStr);
		bCd.setValue(baseStats.baseCd);
		bAts.setValue(baseStats.baseAts);
		bCc.setValue(baseStats.baseCc);
		bDef.setValue(baseStats.baseDef);
		bHp.setValue(baseStats.baseHp);
		bInt.setValue(baseStats.baseInt);
		bSpd.setValue(baseStats.baseSpd);
		bMf.setValue(baseStats.baseMf);
		bPl.setValue(baseStats.basePl);

		pauseRefresh = false;
		refreshBaseStats(true);

		System.out.println("called callback!");
	}

	private void writeLine(String line)
	{
		console.setText(console.getText() + line + "\n");
	}

	private void populateTalisTable(int[][] counts)
	{
		((TalisCountTableModel) talisTable.getModel()).setCounts(counts);
	}

	public void refreshBaseStats(boolean fromApiPage)
	{
		if (pauseRefresh)
			return;
		pauseRefresh = true;
		if (fromApiPage)
		{
			baseStats = PlayerStats.fromStatsOnly(cast(bStr.getValue()), cast(bCd.getValue()), cast(bAts.getValue()),
					cast(bCc.getValue()), cast(bDef.getValue()), cast(bHp.getValue()), cast(bInt.getValue()),
					cast(bMf.getValue()), cast(bPl.getValue()), cast(bSpd.getValue()));
			str.setValue(baseStats.baseStr + ((Equipment) weaponCombo.getSelectedItem()).str + ((Equipment) helmCombo
					.getSelectedItem()).str + ((Equipment) chestCombo.getSelectedItem()).str + ((Equipment) legsCombo
					.getSelectedItem()).str + ((Equipment) bootsCombo.getSelectedItem()).str);
			cd.setValue(baseStats.baseCd + ((Equipment) weaponCombo.getSelectedItem()).cd + ((Equipment) helmCombo
					.getSelectedItem()).cd + ((Equipment) chestCombo.getSelectedItem()).cd + ((Equipment) legsCombo
					.getSelectedItem()).cd + ((Equipment) bootsCombo.getSelectedItem()).cd);
			ats.setValue(baseStats.baseAts + ((Equipment) weaponCombo.getSelectedItem()).ats + ((Equipment) helmCombo
					.getSelectedItem()).ats + ((Equipment) chestCombo.getSelectedItem()).ats + ((Equipment) legsCombo
					.getSelectedItem()).ats + ((Equipment) bootsCombo.getSelectedItem()).ats);
			weaponDmg.setValue(((Equipment) weaponCombo.getSelectedItem()).dmg);
		} else
		{
			baseStats = PlayerStats.fromStatsOnly(
					cast(str.getValue()) - (((Equipment) weaponCombo.getSelectedItem()).str + ((Equipment) helmCombo
							.getSelectedItem()).str + ((Equipment) chestCombo.getSelectedItem()).str
							+ ((Equipment) legsCombo.getSelectedItem()).str + ((Equipment) bootsCombo
							.getSelectedItem()).str),
					cast(cd.getValue()) - (((Equipment) weaponCombo.getSelectedItem()).cd + ((Equipment) helmCombo
							.getSelectedItem()).cd + ((Equipment) chestCombo.getSelectedItem()).cd
							+ ((Equipment) legsCombo.getSelectedItem()).cd + ((Equipment) bootsCombo
							.getSelectedItem()).cd),
					cast(ats.getValue()) - (((Equipment) weaponCombo.getSelectedItem()).ats + ((Equipment) helmCombo
							.getSelectedItem()).ats + ((Equipment) chestCombo.getSelectedItem()).ats
							+ ((Equipment) legsCombo.getSelectedItem()).ats + ((Equipment) bootsCombo
							.getSelectedItem()).ats), cast(bCc.getValue()), cast(bDef.getValue()), cast(bHp.getValue()),
					cast(bInt.getValue()), cast(bMf.getValue()), cast(bPl.getValue()), cast(bSpd.getValue()));
			bStr.setValue(baseStats.baseStr);
			bCd.setValue(baseStats.baseCd);
			bAts.setValue(baseStats.baseAts);
		}
		pauseRefresh = false;
	}

	private double cast(Object value)
	{
		if (value instanceof Integer)
			return (int) value;
		if (value instanceof Double)
			return (double) value;
		return 0;
	}
}
