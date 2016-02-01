package org.quasar.IceCream.utils;
/**
 * Configuration used for {@link StartServer} and {@link StopServer}.
 */
public interface ServerInfo {
	/**
	 * the host to be used. <br>
	 * If you want to run the client server examples on two computers, enter the
	 * computer name of the one that you want to use as server.
	 */

	public String HOST = "10.0.2.2";
	/**
	 * the database file to be used by the server.
	 */
	public String FILE = "RoyalAndLoyal.db4o";
	/**
	 * the port to be used by the server.
	 */
	public int PORT = 0000;
	/**
	 * the user name for access control.
	 */
	public String USER = "db4o";
	/**
	 * the pasword for access control.
	 */
	public String PASS = "db4o";
}
