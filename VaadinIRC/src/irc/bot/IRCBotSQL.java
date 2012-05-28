package irc.bot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.PreparedStatement;

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
			System.out.println("Connection to database has been initialized succesfully! Bot should be operating now.");
		}
		catch (Exception e)
		{
			System.out.println("Could not initialize database connection: " + e);
			e.printStackTrace();
			this.enabled = false;
		}
	}
	
	/**
	 * Initializes the database connection.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws Exception
	 */
	private void initDatabaseConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, Exception
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
		catch (Exception e)
		{
			throw e;
		}
	}
	
	/**
	 * Executes given query that fetches data from database 
	 * and returns the resulted ResultSet object.<br>
	 * This uses Prepared statements to send data to database. 
	 * Substitute values you want to add to database with ?.
	 * @param queryString SQL query.
	 * @param values String array containing the values to be added to database.
	 * @return Returns the resulted Resultset Object for the given query.
	 * @throws SQLException If SQL exception did happen (for ex. invalid SQL syntax), this gets thrown.
	 */
	protected ResultSet executePreparedQuery(String queryString, String[] values) throws SQLException
	{
		PreparedStatement query = null;
		query = connection.prepareStatement(queryString);
		for (int i = 0; i < values.length; i++) query.setString(i+1, values[i]);
		return query.executeQuery();
	}
	
	/**
	 * Executes given UPDATE, INSERT or DELETE query.<br>
	 * This uses Prepared statements to send data to database. 
	 * Substitute values you want to add to database with ?.
	 * @param queryString SQL query which needs to be executed.
	 * @param values String array containing the values to be added to database.
	 * @return Returns Returns the affected row amount (0 for no changes).
	 * @throws SQLException If SQL exception did happen (for ex. invalid SQL syntax), this gets thrown.
	 */
	protected int executePreparedUpdate(String queryString, String[] values) throws SQLException
	{
		PreparedStatement query = null;
		query = connection.prepareStatement(queryString);
		for (int i = 0; i < values.length; i++) query.setString(i+1, values[i]);
		return query.executeUpdate();
	}
	
	/**
	 * Returns the amount of found rows for given SQL query.
	 * This uses Prepared statements to send data to database. 
	 * Substitute values you want to add to database with ?.
	 * @param queryString SQL query.
	 * @param values String array containing the values substituted for SQL operation.
	 * @return Returns the amount of rows for given SQL query.
	 */
	protected int getAmountOfRowsForQuery(String queryString, String[] values)
	{
		int rowAmount = 0;
		try
		{
			ResultSet results = executePreparedQuery(queryString, values);
			while (results.next()) rowAmount++;
		}
		catch (SQLException e)
		{
			System.out.println("Could not fetch row amount: " + e);
			rowAmount = 0;
		}
		return rowAmount;
	}
}