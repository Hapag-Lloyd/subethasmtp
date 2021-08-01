package org.subethamail.smtp.server;

import java.io.IOException;

import org.subethamail.smtp.DropConnectionException;

/**
 * Verifies the presence of a TLS connection if TLS is required. The wrapped
 * command is executed when the test succeeds.
 *
 * @author Erik van Oosten
 */
public class RequireTLSCommandWrapper implements Command {
	private final Command wrapped;

	/**
	 * @param wrapped the wrapped command (not null)
	 */
	public RequireTLSCommandWrapper(final Command wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public void execute(final String commandString, final Session sess) throws IOException, DropConnectionException {
		if (!sess.getServer().getRequireTLS() || sess.isTLSStarted()) {
			wrapped.execute(commandString, sess);
		} else {
			sess.sendResponse("530 Must issue a STARTTLS command first");
		}
	}

	@Override
	public HelpMessage getHelp() throws CommandException {
		return wrapped.getHelp();
	}

	@Override
	public String getName() {
		return wrapped.getName();
	}
}
