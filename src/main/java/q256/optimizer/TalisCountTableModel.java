package q256.optimizer;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Arrays;

/**
 * Simple class that makes the talis count look nice
 */
public class TalisCountTableModel implements TableModel
{
	private int[][] reforges = new int[6][6];

	public TalisCountTableModel()
	{
		for (int i = 0; i < 6; i++)
		{
			reforges[i] = new int[6];
		}
	}

	@Override
	public int getRowCount()
	{
		return 7;
	}

	@Override
	public int getColumnCount()
	{
		return 7;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		if (columnIndex == 0)
			return "";
		return Constants.reforgeNames[columnIndex - 1];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		if (columnIndex == 0)
			return String.class;
		return int.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (columnIndex == 0 && rowIndex == 0)
			return "";
		else if (columnIndex == 0)
			return Constants.rarityNames[rowIndex - 1];
		else if (rowIndex == 0)
			return Constants.reforgeNames[columnIndex - 1];
		return reforges[rowIndex - 1][columnIndex - 1];
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if (columnIndex > 0 && aValue instanceof Integer)
			reforges[rowIndex][columnIndex - 1] = (int) aValue;
	}

	@Override
	public void addTableModelListener(TableModelListener l)
	{

	}

	@Override
	public void removeTableModelListener(TableModelListener l)
	{

	}

	public void setCounts(int[][] counts)
	{
		for (int i = 0; i < 6; i++)
		{
			reforges[i] = Arrays.copyOf(counts[i], 6);
		}
	}
}
