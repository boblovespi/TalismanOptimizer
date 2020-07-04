package q256.optimizer.apidialog;

import javax.swing.*;
import java.awt.event.*;

public class APINameInput extends JDialog
{
	private boolean useSkyLea;
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField username;
	private ApiCallback callback;

	public APINameInput(boolean useSkyLea)
	{
		this.useSkyLea = useSkyLea;
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
	}

	private void onOK()
	{
		// add your code here
		ProfileSelector next = new ProfileSelector();
		next.username = username.getText();
		next.useSkyLea = useSkyLea;
		next.setCallback(callback);
		if (!useSkyLea)
			next.grabProfiles();
		else
			next.grabFromSkyLea();
		next.pack();
		next.setLocationRelativeTo(null);
		dispose();
		next.setVisible(true);
	}

	private void onCancel()
	{
		// add your code here if necessary
		dispose();
	}

	public void setCallback(ApiCallback callback)
	{
		this.callback = callback;
	}
}
