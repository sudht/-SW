package fileProcessing;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;

public class GraphSaveLoad<GraphDisplayPanel>
{
	private static final String file_path = "data/", file_name = "graph_panel_data.txt";
	
	public GraphSaveLoad(){}
	
	public void save(GraphDisplayPanel item, boolean append)
	{
		try {
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file_path + file_name, append));
			output.writeObject(item);
			output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void save(GraphDisplayPanel[] items, boolean append)
	{
		try {
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file_path + file_name, append));
			for(int i=0; i<items.length; ++i)
				output.writeObject(items[i]);
			output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public GraphDisplayPanel[] load(Class<GraphDisplayPanel> type, int count)
	{
		GraphDisplayPanel[] items = (GraphDisplayPanel[]) Array.newInstance(type, count);
		try {
			ObjectInputStream input = new ObjectInputStream(new FileInputStream(file_path + file_name));
			for(int i=0; i<count; ++i)
				items[i] = (GraphDisplayPanel)input.readObject();
			input.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return items;
	}
}
