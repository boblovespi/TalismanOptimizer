package q256.optimizer.apidialog;

import com.flowpowered.nbt.CompoundTag;
import com.google.gson.JsonObject;
import q256.optimizer.Equipment;
import q256.optimizer.PlayerEquipment;
import q256.optimizer.PlayerStats;
import q256.optimizer.api.APIReader;
import q256.optimizer.api.Parser;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProfileSelector extends JDialog
{
	public String username;
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JComboBox<String> profileCombo;
	private JLabel loading;
	private JCheckBox talisOutsideOfBag;
	private ApiCallback callback;
	private JsonObject profiles;
	private String uuid;

	public ProfileSelector()
	{
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		buttonOK.addActionListener(e -> onOK());

		buttonCancel.addActionListener(e -> onCancel());

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		profileCombo.setModel(new DefaultComboBoxModel<>());
	}

	private void onOK()
	{
		if (profiles != null)
		{
			// add your code here
			String fruit = (String) profileCombo.getSelectedItem();
			JsonObject profile = Parser.getPlayerProfile(profiles, fruit, uuid);
			PlayerEquipment p = new PlayerEquipment();
			int[] talis;
			PlayerStats baseStats = Parser.baseStats(profile);

			// add nothing
			p.boots.add(Equipment.NO_BOOTS);
			p.legs.add(Equipment.NO_LEGS);
			p.chests.add(Equipment.NO_CHEST);
			p.helms.add(Equipment.NO_HELM);
			p.swords.add(Equipment.NO_WEAPON);

			// getting equipment
			CompoundTag armorNbt = (CompoundTag) Parser
					.decodeNbt(profile.get("inv_armor").getAsJsonObject().get("data").getAsString());
			PlayerEquipment armorEquip = Parser
					.getEquipment((List<CompoundTag>) armorNbt.getValue().get("i").getValue());
			CompoundTag invNbt = (CompoundTag) Parser
					.decodeNbt(profile.get("inv_contents").getAsJsonObject().get("data").getAsString());
			PlayerEquipment invEquip = Parser.getEquipment((List<CompoundTag>) invNbt.getValue().get("i").getValue());
			CompoundTag echestNbt = (CompoundTag) Parser
					.decodeNbt(profile.get("ender_chest_contents").getAsJsonObject().get("data").getAsString());
			PlayerEquipment echestEquip = Parser
					.getEquipment((List<CompoundTag>) echestNbt.getValue().get("i").getValue());

			p.boots.addAll(armorEquip.boots);
			p.legs.addAll(armorEquip.legs);
			p.chests.addAll(armorEquip.chests);
			p.helms.addAll(armorEquip.helms);
			p.swords.addAll(armorEquip.swords);

			p.boots.addAll(invEquip.boots);
			p.legs.addAll(invEquip.legs);
			p.chests.addAll(invEquip.chests);
			p.helms.addAll(invEquip.helms);
			p.swords.addAll(invEquip.swords);

			p.boots.addAll(echestEquip.boots);
			p.legs.addAll(echestEquip.legs);
			p.chests.addAll(echestEquip.chests);
			p.helms.addAll(echestEquip.helms);
			p.swords.addAll(echestEquip.swords);

			talis = Parser.getTalisInBag(profile);
			if (talisOutsideOfBag.isSelected())
			{
				int[] talisArmor = Parser.getTalisInInv((List<CompoundTag>) armorNbt.getValue().get("i").getValue());
				int[] talisInv = Parser.getTalisInInv((List<CompoundTag>) invNbt.getValue().get("i").getValue());
				int[] talisEchest = Parser.getTalisInInv((List<CompoundTag>) echestNbt.getValue().get("i").getValue());
				for (int i = 0; i < talis.length; i++)
				{
					talis[i] += talisArmor[i] + talisInv[i] + talisEchest[i];
				}
			}
			callback.callback(baseStats, p, talis);
		}
		dispose();
	}

	private void onCancel()
	{
		APINameInput next = new APINameInput();
		next.setCallback(callback);
		next.pack();
		next.setLocationRelativeTo(null);
		dispose();
		next.setVisible(true);
	}

	public void grabProfiles()
	{
		CompletableFuture<String> uuid = APIReader.API.nameToUUID(username);
		uuid.whenComplete((u, e) ->
		{
			if (e != null)
			{
				SwingUtilities.invokeLater(() -> loading.setText("There was an exception in grabbing profile data!"));
				return;
			}
			System.out.println(u);
			CompletableFuture<JsonObject> profiles = APIReader.API.profilesData(u);
			profiles.whenComplete((u1, e1) ->
			{
				if (e1 != null)
				{
					SwingUtilities
							.invokeLater(() -> loading.setText("There was an exception in grabbing profile data!"));
					return;
				}
				String[] fruits = Parser.getFruits(u1);
				System.out.println(Arrays.toString(fruits));
				this.profiles = u1;
				this.uuid = u;
				SwingUtilities.invokeLater(() ->
				{
					for (String fruit : fruits)
					{
						profileCombo.addItem(fruit);
					}
					loading.setText("Choose profile");
				});
			});
		});
	}

	public void setCallback(ApiCallback callback)
	{
		this.callback = callback;
	}
}
