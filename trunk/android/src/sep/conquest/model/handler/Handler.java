package sep.conquest.model.handler;

import sep.conquest.model.IRequest;

/**
 * Abstract class representing a Handler to handle messages.
 * 
 * @author Andreas Poxrucker
 */
public abstract class Handler {

	/**
	 * Reference on next Handler in chain.
	 */
	private Handler next;

	/**
	 * The constructor takes the previous handler (if exists) and builds the
	 * handler-chain.
	 * 
	 * @param prev
	 */
	public Handler(Handler prev) {
		if (prev != null)
			prev.setNext(this);			
	}

	/**
	 * Returns reference on next Handler in chain.
	 * 
	 * @return Next Handler in chain or null, if Handler has no reference on
	 *         next Handler.
	 */
	public Handler getNext() {
		return next;
	}

	/**
	 * Sets reference of this Handler to nextHandler
	 * 
	 * @param nextHandler
	 *            Reference on next Handler in chain
	 */
	public void setNext(Handler nextHandler) {
		next = nextHandler;
	}

	/**
	 * Returns whether Handler has successor.
	 * 
	 * @return True, if Handler has successor, false otherwise.
	 */
	public boolean hasNext() {
		return next == null;
	}

	/**
	 * Handles a request message if responsible and returns true. Otherwise
	 * passes request to next Handler or returns false, if there is no next
	 * Handler.
	 * 
	 * @param request
	 *            The request that has to be handled.
	 * 
	 * @return True, if Handler has handled message, false otherwise.
	 */
	public boolean handleRequest(IRequest request) {
		if (hasNext())
			return getNext().handleRequest(request);
		else
			return false;
	}
}
