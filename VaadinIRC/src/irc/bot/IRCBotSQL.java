/**
 * Copyright (C) 2012 Aleksi Postari
 * License type: MIT (http://en.wikipedia.org/wiki/MIT_License)
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package irc.bot;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
	
	/**
	 * Constructor to create the IRCBotSQL object and to initialize the SQL connection.
	 * @param enabled Is the bot enabled?
	 * @param address SQL server address.
	 * @param username SQL server username.
	 * @param password SQL server password.
	 * @param databaseDriver Java Database Driver.
	 * @param databaseName SQL database name.
	 */
	public IRCBotSQL(boolean enabled, String address, String username, String password, String databaseDriver, String databaseName)
	{
		this.enabled = enabled;
		this.address = address;
		this.username = username;
		this.password = password;
		this.databaseDriver = databaseDriver;
		this.databaseName = databaseName;
		
		boolean initDatabase = true;
		// Check that database exists. If it does not exist, create the database with default mysql tables.
		try
		{
			createDatabaseIfDoesNotExist();
		}
		catch (Exception e)
		{
			System.out.println("There was an error trying to create the database: " + e);
			System.out.println("Bot / database functionality will be disabled for now.");
			e.printStackTrace();
			initDatabase = false;
		}
		
		if (initDatabase)
		{
			// Try to initialize the database connection.
			try
			{
				initDatabaseConnection();
				System.out.println("Connection to database " + databaseName + " has been initialized succesfully! Bot should be operating now.");
			}
			catch (Exception e)
			{
				System.out.println("Could not initialize database connection: " + e);
				e.printStackTrace();
				this.enabled = false;
			}
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
	 * This uses Prepared statements to send data to database.<br>
	 * Substitute values you want to add to database with ?.<br>
	 * <b>Remember to call {@link #closeResultsetAndPreparedStatement(ResultSet, PreparedStatement)} 
	 * after you are done with the results.</b>
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
		ResultSet results = query.executeQuery();
		return results;
	}
	
	/**
	 * Executes given UPDATE, INSERT or DELETE query.<br>
	 * This uses Prepared statements to send data to database. <br>
	 * Substitute values you want to add to database with ?.<br>
	 * Automatically closes the PreparedStatement so you don't need to.
	 * @param queryString SQL query which needs to be executed.
	 * @param values String array containing the values to be added to database.
	 * @return Returns Returns the affected row amount (0 for no changes).
	 * @throws SQLException If SQL exception did happen (for ex. invalid SQL syntax), this gets thrown.
	 */
	protected int executePreparedUpdate(String queryString, String[] values) throws SQLException
	{
		int foundRows = 0;
		PreparedStatement query = null;
		try
		{
			query = connection.prepareStatement(queryString);
			for (int i = 0; i < values.length; i++) query.setString(i+1, values[i]);
			foundRows = query.executeUpdate();
			closePreparedStatement(query);
		}
		catch (SQLException e)
		{
			closePreparedStatement(query);
			throw e;
		}
		return foundRows;
	}
	
	/**
	 * Returns the amount of found rows for given SQL query.<br>
	 * This uses Prepared statements to send data to database.<br>
	 * Substitute values you want to add to database with ?.
	 * @param queryString SQL query.
	 * @param values String array containing the values substituted for SQL operation.
	 * @return Returns the amount of rows for given SQL query.
	 */
	protected int getAmountOfRowsForQuery(String queryString, String[] values)
	{
		int rowAmount = 0;
		ResultSet results = null;
		try
		{
			results = executePreparedQuery(queryString, values);
			while (results.next()) rowAmount++;
		}
		catch (SQLException e)
		{
			System.out.println("Could not fetch row amount: " + e);
			rowAmount = 0;
			closeResultset(results);
		}
		finally
		{
			closeResultset(results);
		}
		return rowAmount;
	}
	
	/**
	 * Closes a ResultSet and PreparedStatement.
	 * @param set Target ResultSet which needs to be closed.
	 * @param pre Target PreparedStatement which needs to be closed.
	 */
	public void closeResultsetAndPreparedStatement(ResultSet set, PreparedStatement pre)
	{
		closeResultset(set);
		closePreparedStatement(pre);
	}
	
	/**
	 * Used to close a ResultSet.<br>
	 * Will print an error to the console if there were any.
	 * @param set Target ResultSet which needs to be closed.
	 */
	private void closeResultset(ResultSet set)
	{
		if (set == null) return;
		try
		{
			set.close();
		}
		catch (Exception e)
		{
			System.out.println("Error closing ResultSet: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Used to close PreparedStatement.<br>
	 * Will print an error to the console if there were any.
	 * @param pre Target PreparedStatement which needs to be closed.
	 */
	private void closePreparedStatement(PreparedStatement pre)
	{
		if (pre == null) return;
		try
		{
			pre.close();
		}
		catch (Exception e)
		{
			System.out.println("Error closing PreparedStatement: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Used at the constructor to check if database does not exist 
	 * and if it does not exist, will create the database with default
	 * tables.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 * @throws Exception
	 */
	private void createDatabaseIfDoesNotExist() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, Exception
	{
		ResultSet rs = null;
		Statement st = null;
		boolean canDeleteDB = false;
		
		try
		{
			Class.forName(databaseDriver).newInstance();
			connection = DriverManager.getConnection(address, username, password);
			st = connection.createStatement();
			DatabaseMetaData meta = connection.getMetaData();
			rs = meta.getCatalogs();
			// If databases contains the current database name, return.
			while (rs.next())
				if (rs.getString("TABLE_CAT").equals(databaseName)) return;
			
			// Database does not exist, create it.
			st.executeUpdate("CREATE DATABASE " + databaseName);
			System.out.println("Database did not exist, created database: " + databaseName + ".");
			System.out.println("Starting to create tables for the database...");
			rs.close();
			st.close();
			connection.close();
			
			// From this point on, new database can be deleted on errors.
			canDeleteDB = true;
			
			connection = DriverManager.getConnection(address + databaseName, username, password);
			// Create table chanmessages
			st = connection.createStatement();
			st.executeUpdate("CREATE TABLE chanmessages (" +
			  "id int(11) NOT NULL AUTO_INCREMENT," +
			  "channel text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL," +
			  "nickname text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL," +
			  "sent timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
			  "content text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL," +
			  "PRIMARY KEY (id)" +
			  ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci");
			st.close();
			System.out.println("Created table chanmessages!");
			
			// Create table chanstats
			st = connection.createStatement();
			st.executeUpdate("CREATE TABLE chanstats ("+
			  "id int(11) NOT NULL AUTO_INCREMENT,"+
			  "channel text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,"+
			  "words int(11) NOT NULL DEFAULT '0',"+
			  "added timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"+
			  "PRIMARY KEY (id)"+
			  ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci");
			st.close();
			System.out.println("Created table chanstats!");
			
			// Create table defines
			st = connection.createStatement();
			st.executeUpdate("CREATE TABLE defines ("+
			  "nickname text COLLATE utf8_unicode_ci NOT NULL,"+
			  "created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"+
			  "channel text COLLATE utf8_unicode_ci NOT NULL,"+
			  "id int(11) NOT NULL AUTO_INCREMENT,"+
			  "define text COLLATE utf8_unicode_ci,"+
			  "content text COLLATE utf8_unicode_ci,"+
			  "KEY id (id)"+
			  ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci");
			st.close();
			System.out.println("Created table defines!");
			
			// Create table joinmessages
			st = connection.createStatement();
			st.executeUpdate("CREATE TABLE joinmessages ("+
			  "nickname text COLLATE utf8_unicode_ci NOT NULL,"+
			  "created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"+
			  "message text COLLATE utf8_unicode_ci,"+
			  "channel text COLLATE utf8_unicode_ci NOT NULL,"+
			  "id int(11) NOT NULL AUTO_INCREMENT,"+
			  "KEY id (id)"+
			  ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci");
			st.close();
			System.out.println("Created table joinmessages!");
			
			// Create table nickstats
			st = connection.createStatement();
			st.executeUpdate("CREATE TABLE nickstats ("+
			  "id int(11) NOT NULL AUTO_INCREMENT,"+
			  "channel text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,"+
			  "nickname text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,"+
			  "words int(11) NOT NULL DEFAULT '0',"+
			  "added timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"+
			  "PRIMARY KEY (id)"+
			  ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci");
			st.close();
			System.out.println("Created table nickstats!");
			
			// Create table words
			st = connection.createStatement();
			st.executeUpdate("CREATE TABLE words ("+
			  "nickname text COLLATE utf8_unicode_ci NOT NULL,"+
			  "created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"+
			  "message text COLLATE utf8_unicode_ci,"+
			  "channel text COLLATE utf8_unicode_ci NOT NULL,"+
			  "id int(11) NOT NULL AUTO_INCREMENT,"+
			  "word text COLLATE utf8_unicode_ci,"+
			  "KEY id (id)"+
			  ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci");
			System.out.println("Created table words!");
			st.close();
			
			// Create table settings
			st = connection.createStatement();
			st.executeUpdate("CREATE TABLE settings ("+
			  "id int(11) NOT NULL AUTO_INCREMENT,"+
			  "setting text NOT NULL,"+
			  "value text NOT NULL,"+
			  "PRIMARY KEY (id)"+
			  ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci");
			System.out.println("Created table settings!");
			st.close();
			
			System.out.println("New database for VaadinIRC was created succesfully!");
		}
		catch (InstantiationException e)
		{
			if (rs != null) rs.close(); if (st != null) st.close(); if (connection != null) connection.close();
			throw e;
		}
		catch (IllegalAccessException e)
		{
			if (rs != null) rs.close(); if (st != null) st.close(); if (connection != null) connection.close();
			throw e;
		}
		catch (ClassNotFoundException e)
		{
			if (rs != null) rs.close(); if (st != null) st.close(); if (connection != null) connection.close();
			throw e;
		}
		catch (SQLException e)
		{
			// Delete the whole database on SQL exceptions if the database was already created and there were problems
			// creating the tables for the new database.
			if (canDeleteDB) deleteDatabase();
			if (rs != null) rs.close(); if (st != null) st.close(); if (connection != null) connection.close();
			throw e;
		}
		finally
		{
			if (rs != null) rs.close(); if (st != null) st.close(); if (connection != null) connection.close();
		}
	}
	
	/**
	 * Removes the current database.<br>
	 * This is used if any errors did occur in creating the default database in method
	 * {@link #createDatabaseIfDoesNotExist()}.
	 * @throws SQLException 
	 * @throws Exception
	 */
	private void deleteDatabase() throws SQLException, Exception
	{
		Statement st = null;
		try
		{
			connection.close();
			Class.forName(databaseDriver).newInstance();
			connection = DriverManager.getConnection(address, username, password);
			st = connection.createStatement();
			st.executeUpdate("DROP DATABASE " + databaseName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (st != null) st.close();
			System.out.println("error creating the database " + databaseName + ": " + e.getMessage());
			throw e;
		}
		finally
		{
			if (st != null) st.close();
		}
	}
}