/*
 * Copyright (c) 2006, Jonathan Clark <jon_DOT_h_DOT_clark_AT_gmail_DOT_com> 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of my affiliates nor the names of thier contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIEDWARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package info.jonclark.database;

import java.sql.*;

/**
 * @author Jonathan
 */
public class PostgresDatabase {
	private Connection conn = null;
	private final String username;
	private final String password;
	private final String dbname;
	private final String host;
	private final String port;
	
    /**
     * Create a new PostgresDatabase connection, load the driver, and attempt to connect
     * 
    * @param username
    * @param password
    * @param dbname
    * @param host Does not include any protocol specification. e.g.  riogrande.cs.tcu.edu
    * @param port
    */
    public PostgresDatabase(String username, String password, String dbname, String host, String port)
    	throws DatabaseException
    {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException cnfe) {
            throw new DatabaseException("Couldn't load the Postgres Driver."
                    + " Make sure the PostgreSQL driver is in your class path.",
                    cnfe);
        }
        
        this.username = username;
        this.password = password;
        this.dbname = dbname;
        this.host = host;
        this.port = port;
        
        connect();
    }
    
    /**
     * Connect to a PostgreSQL database using the specified arguments
     * 
     * @throws DatabaseException
     */
    private void connect()
    	throws DatabaseException
    {
        assert username != null;
        assert password != null;
        assert dbname != null;
        assert host != null;
        assert port != null;
        
		try {
			conn = DriverManager.getConnection(
					"jdbc:postgresql://:" + port + "/" + dbname + "?", username,
					password);
		} catch (SQLException se) {
		    throw new DatabaseException("Couldn't connect to the database " + dbname
		            + " at " + host + ":" + port + " for user " + username,
		            se);
		}

		if(conn == null) {
		    throw new DatabaseException("Couldn't connect to the database " + dbname
		            + " at " + host + ":" + port + " for user " + username + ": Null connection");
		}
	} // end connect
    
    /**
     * Execute a SQL statement on the server
     * 
     * @param statement A SQL statement. e.g. SELECT * FROM parts
     */
    public ResultSet executeQuery(String statement)
    	throws SQLException, DatabaseException
    {
        Statement sql = null;
        
        try {
			sql = conn.createStatement();
		} catch (SQLException se) {
			connect();						// attempt to reconnect
			sql = conn.createStatement();	// if this fails, give up
		}
        
		// this line could result in a SQL exception, but we'll let the user deal with that
		return sql.executeQuery(statement);
    }
}
