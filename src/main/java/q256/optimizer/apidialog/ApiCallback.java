package q256.optimizer.apidialog;

import q256.optimizer.PlayerEquipment;
import q256.optimizer.PlayerStats;

/**
 * a callback for the api dialog
 */
@FunctionalInterface
public interface ApiCallback
{
	void callback(PlayerStats baseStats, PlayerEquipment equipment, int[] talis);
}
