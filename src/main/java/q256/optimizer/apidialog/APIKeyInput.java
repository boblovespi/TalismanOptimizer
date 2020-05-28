package q256.optimizer.apidialog;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class APIKeyInput extends JDialog
{
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField apiKey;
	private Consumer<String> callback;

	public APIKeyInput()
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
	}

	public void setCallback(Consumer<String> callback)
	{
		this.callback = callback;
	}

	private void onOK()
	{
		callback.accept(apiKey.getText());
		// add your code here
		dispose();
	}

	private void onCancel()
	{
		// add your code here if necessary
		dispose();
	}
}
