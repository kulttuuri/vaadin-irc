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
	protected boolean enabled;
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
		this.enabled = enabled;
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
		
		try
		{
			ResultSet results = executeQuery("SELECT * FROM defines WHERE nickname = 'kulttuuri'");
			while (results.next())
				System.out.println("result: " + results.getString("content"));
		}
		catch (SQLException e)
		{
			System.out.println("query: " + e);
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
	
	/**
	 * Executes given query that fetches data from database 
	 * and returns the resulted ResultSet object.
	 * @param queryString SQL query.
	 * @return Returns the resulted Resultset Object for the given query.
	 * @throws SQLException If SQL exception did happen (for ex. invalid SQL syntax), this gets thrown.
	 */
	protected ResultSet executeQuery(String queryString) throws SQLException
	{
		Statement query;
		query = connection.createStatement();
		return query.executeQuery(queryString);
	}
	
	/**
	 * Executes given UPDATE, INSERT or DELETE query.
	 * @param queryString SQL query which needs to be executed.
	 * @return Returns Returns the affected row amount (0 for no changes).
	 * @throws SQLException If SQL exception did happen (for ex. invalid SQL syntax), this gets thrown.
	 */
	protected int executeUpdate(String queryString) throws SQLException
	{
		Statement query;
		query = connection.createStatement();
		return query.executeUpdate(queryString);
	}
	
	/**
	 * Returns the amount of found rows for given SQL query.
	 * @param queryString SQL query.
	 * @return Returns the amount of rows for given SQL query.
	 */
	protected int getAmountOfRowsForQuery(String queryString)
	{
		int rowAmount = 0;
		try
		{
			ResultSet results = executeQuery(queryString);
			while (results.next()) rowAmount++;
		}
		catch (SQLException e)
		{
			System.out.println("Could not fetch row amount: " + e);
			rowAmount = 0;
		}
		return rowAmount;
	}
	
	@Deprecated
	protected void executeCustomQuery(String query) throws SQLException
	{
			Statement stQuery;
			try
			{
				stQuery = connection.createStatement();
				stQuery.executeQuery(query);
			}
			catch (NullPointerException e)
			{
				// No results found
			}
			catch (SQLException e)
			{
				throw e;
			}
	}
	
	@Deprecated
	protected ArrayList<String> getSingleDataFromDatabase(String table, String item, String whereItem, String whereData) throws SQLException
	{
		ArrayList<String> returnList = new ArrayList<String>();
		try
		{
			Statement stQuery = connection.createStatement();
			String query = "SELECT " + item + " FROM " + table + " WHERE " + whereItem + " = '" + whereData + "'";
			ResultSet results = stQuery.executeQuery(query);
			// Go through found items and add them to return list.
			while (results.next())
				returnList.add(results.getString(item));
		}
		catch (SQLException e)
		{
			throw e;
		}
		catch (NullPointerException e)
		{
			// No results found
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return returnList;
	}
	
	@Deprecated
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
					query += " " + entry.getKey() + " = '" + entry.getValue() + "' AND";
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
		catch (NullPointerException e)
		{
			// No results found
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return returnList;
	}
}