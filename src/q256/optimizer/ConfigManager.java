package q256.optimizer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager
{
	public LookAndFeel lookAndFeel;

	public ConfigManager()
	{

	}

	public void loadConfigs(String config)
	{
		Properties properties = new Properties();
		try (FileReader propReader = new FileReader(config))
		{
			properties.load(propReader);
		} catch (IOException e)
		{
			System.out.println("could not find properties file, resolving to default");
			e.printStackTrace();
			// make the config file if it doesn't exist
			createConfigFile(properties, config);
		}

		// grab the look and feel we want
		String lnf = properties.getProperty("lookandfeel", "default");
		if (lnf.toLowerCase().equals("system"))
			lookAndFeel = LookAndFeel.SYSTEM;
		else if (lnf.toLowerCase().equals("metal"))
			lookAndFeel = LookAndFeel.METAL;
		else
			lookAndFeel = LookAndFeel.NORMAL;
	}

	private void createConfigFile(Properties properties, String config)
	{
		// initialize config properties here
		properties.setProperty("lookandfeel", "default");

		try (FileWriter propWriter = new FileWriter(config))
		{
			properties.store(propWriter, "config for the talisman optimizer by q256");
		} catch (IOException e1)
		{
			System.out.println("failed to create property file!");
			e1.printStackTrace();
		}
	}

	enum LookAndFeel
	{
		NORMAL, SYSTEM, METAL,
	}
}
