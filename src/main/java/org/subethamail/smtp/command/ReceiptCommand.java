package org.subethamail.smtp.command;

import java.io.IOException;
import java.util.Locale;

import org.subethamail.smtp.DropConnectionException;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.server.BaseCommand;
import org.subethamail.smtp.server.Session;
import org.subethamail.smtp.util.EmailUtils;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
public class ReceiptCommand extends BaseCommand {
	public ReceiptCommand() {
		super("RCPT", "Specifies the recipient. Can be used any number of times.", "TO: <recipient> [ <parameters> ]");
	}

	@Override
	public void execute(final String commandString, final Session sess) throws IOException, DropConnectionException {
		if (!sess.isMailTransactionInProgress()) {
			sess.sendResponse("503 5.5.1 Error: need MAIL command");
			return;
		}
		if (sess.getServer().getMaxRecipients() >= 0
				&& sess.getRecipientCount() >= sess.getServer().getMaxRecipients()) {
			sess.sendResponse("452 Error: too many recipients");
			return;
		}

		final String args = this.getArgPredicate(commandString);
		if (!args.toUpperCase(Locale.ENGLISH).startsWith("TO:")) {
			sess.sendResponse("501 Syntax: RCPT TO: <address>  Error in parameters: \"" + args + "\"");
		}
		final String recipientAddress = EmailUtils.extractEmailAddress(args, 3);
		try {
			sess.getMessageHandler().recipient(recipientAddress);
			sess.addRecipient(recipientAddress);
			sess.sendResponse("250 Ok");
		} catch (final DropConnectionException ex) {
			throw ex; // Propagate this
		} catch (final RejectException ex) {
			sess.sendResponse(ex.getErrorResponse());
		}
	}
}
