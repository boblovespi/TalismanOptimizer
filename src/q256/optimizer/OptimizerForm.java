package q256.optimizer;

import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

/**
 * Created by Willi on 5/24/2020.
 */
public class OptimizerForm
{
	private static ConfigManager configManager;
	private JTable talisTable;
	private JButton calculateButton;
	private JTabbedPane optimizerTab;
	private JEditorPane console;
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

	public static void main(String[] args)
	{
		configManager = new ConfigManager();
		configManager.loadConfigs("config.cfg");

		// init the lnf
		initLNF();
		JFrame frame = new JFrame("OptimizerForm");
		frame.setContentPane(new OptimizerForm().mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

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
}
