package q256.optimizer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager
{
	public LookAndFeel lookAndFeel;
	public String apiKey;

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

		// make sure nothing's missing
		if (properties.keySet().size() != 2)
			createConfigFile(properties, config);

		// grab the look and feel we want
		String lnf = properties.getProperty("lookandfeel", "default");
		if (lnf.toLowerCase().equals("system"))
			lookAndFeel = LookAndFeel.SYSTEM;
		else if (lnf.toLowerCase().equals("metal"))
			lookAndFeel = LookAndFeel.METAL;
		else
			lookAndFeel = LookAndFeel.NORMAL;

		// grab the api key
		apiKey = properties.getProperty("apikey", "none");
	}

	private void createConfigFile(Properties properties, String config)
	{
		// initialize config properties here
		properties.putIfAbsent("lookandfeel", "default");
		properties.putIfAbsent("apikey", "none");

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
