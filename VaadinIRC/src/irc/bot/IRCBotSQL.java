package irc.bot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Database functionality for IRC Bot.
 * @author Aleksi Postari
 *
 */
public abstract class IRCBotSQL
{
	/** IRCBot enabled? */
	protected String enabled;
	/** Database address */
	private String address;
	/** Database username */
	private String username;
	/** Database password */
	private String password;
	/** Database driver */
	private String databaseDriver;
	/** Name of the database. */
	private String databaseName;
	/** Contains the database connection. */
	private Connection connection;
	
	public IRCBotSQL(boolean enabled, String address, String username, String password, String databaseDriver, String databaseName)
	{
		this.address = address;
		this.username = username;
		this.password = password;
		this.databaseDriver = databaseDriver;
		this.databaseName = databaseName;
		try
		{
			initDatabaseConnection();
		}
		catch (Exception e)
		{
			System.out.println("Could not initialize database connection: " + e);
			e.printStackTrace();
			enabled = false;
		}
		
		ArrayList<String> items = new ArrayList<String>();
		items.add("name");
		Map<String, String> where = new HashMap<String, String>();
		where.put("sender", "kulttuuri");
		try
		{
			ArrayList<String> data = getDataFromDatabase("defines", items, where);
			if (data.size() > 0) System.out.println("joo: " + data.get(0));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes the database connection.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void initDatabaseConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		try
		{
			Class.forName(databaseDriver).newInstance();
			connection = DriverManager.getConnection(address + databaseName, username, password);
		}
		catch (InstantiationException e)
		{
			throw e;
		}
		catch (IllegalAccessException e)
		{
			throw e;
		}
		catch (ClassNotFoundException e)
		{
			throw e;
		}
		catch (SQLException e)
		{
			throw e;
		} 
	}
	
	protected void setCurrentDatabase(String databaseName) throws SQLException
	{
		try
		{
			executeCustomQuery("USE " + databaseName);
		}
		catch (SQLException e)
		{
			throw e;
		}
	}
	
	protected void executeCustomQuery(String query) throws SQLException
	{
			Statement stQuery;
			try
			{
				stQuery = connection.createStatement();
				stQuery.executeQuery(query);
			}
			catch (SQLException e)
			{
				throw e;
			}
	}
	
	protected ArrayList<String> getDataFromDatabase(String table, ArrayList<String> items, Map<String, String> where) throws SQLException
	{
		ArrayList<String> returnList = new ArrayList<String>();
		try
		{
			Statement stQuery = connection.createStatement();
			// Create query
				String query = "SELECT * FROM " + table;
				// Append searchable items to query
				
				// Append where items to query
				if (where.size() > 0) query += " WHERE";
				for (Map.Entry<String, String> entry : where.entrySet())
				{
					query += " " + entry.getKey() + " = " + entry.getValue() + " AND";
				}
				// Strip last AND from query if it exists.
				if (query.substring(query.length()-3, query.length()).equals("AND")) query = query.substring(0, query.length()-3);
			System.out.println("query: " + query);
			ResultSet results = stQuery.executeQuery(query);
			// Go through found items and add them to return list.
			while (results.next())
				for (String item : items)
					returnList.add(results.getString(item));
		}
		catch (SQLException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return returnList;
	}
}