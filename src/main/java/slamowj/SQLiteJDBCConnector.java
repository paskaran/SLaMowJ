package slamowj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class SQLiteJDBCConnector {
	private Connection c = null;
	private Statement stmt = null;

	protected SQLiteJDBCConnector() {

	}

	protected SQLiteJDBCConnector(String dbName) {
		createContentTable(dbName);
	}

	protected void initConnection(String dbName) {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbName + ".db");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	protected void close() {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void createStatement() {
		try {
			stmt = c.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void closeStatement() {
		try {
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected int createContentTable(String dbName) {
		initConnection(dbName);
		createStatement();
		int ret = 0;
		try {
			ret = stmt
					.executeUpdate("CREATE TABLE content "
							+ "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
							+ " fw   CHAR(64)     NOT NULL, "
							+ " pf  TEXT  NOT NULL);");
		} catch (SQLException e) {
			e.printStackTrace();
			ret = -1;
		}
		closeStatement();
		close();
		return ret;
	}

	protected int insertGram(String firstWord, String followers) {
		try {
			return stmt.executeUpdate("INSERT INTO content (id, fw, pf) "
					+ "VALUES (null,  '" + firstWord + "', '" + followers
					+ "');");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	protected String[] getGramByFirstWord(String firstWord) {

		try {
			String sql = "SELECT pf FROM content WHERE fw = '" + firstWord
					+ "';";

			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				String pf = rset.getString("pf");

				if (pf.contains("[") && pf.contains("]")) {
					pf = pf.replaceAll("\\[[0-9]+\\]", "");
				}
				String[] pfs = null;
				if (pf.contains(";")) {
					pfs = pf.split(";");
				} else {
					pfs = new String[] { pf };
				}

				return pfs;

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
