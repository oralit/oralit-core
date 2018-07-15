package org.javautil.oralit.instrumentation;

import java.sql.SQLException;

public interface DbStats {

	/**
	 * Begin database tracing. Module is the name of this service and may not be
	 * longer than 40 characters
	 * 
	 * @param Module
	 * @return
	 * @throws SQLException
	 */
	public boolean traceOn(final String Module);

	public boolean traceOff();

	/**
	 * An action is a named collection of database calls.
	 * 
	 * @param action
	 */
	public void setAction(final String action);

	public void setModule(final String module, final String action);

	public void setClientInfo(final String info);

	public void setClientIdentifier(final String info);

	public void setTraceFileIdentifier(final String id);

	public String getTraceFileName() throws SQLException;

}
