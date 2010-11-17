package sep.conquest.model;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.UUID;

/**
 * The <code>Iterator</code> for the class <code>RobotStatus</code>.
 * 
 * @author Andreas Wilhelm
 *
 */
public class RobotStatusIterator implements Iterator<RobotStatus> {
	
	/**
	 * The <code>TreeMap</code> which contains the status-informations about
	 * the robots.
	 */
	private final TreeMap<UUID, RobotStatus> status;
	
	/**
	 * The internal iterator for key values.
	 */
	private Iterator<UUID> iterator;
	
	/**
	 * The constructor takes a <code>TreeMap</code> and sets the attribute
	 * as well as an iterator.
	 * 
	 * @param status
	 */
	public RobotStatusIterator(TreeMap<UUID, RobotStatus> status) {
		this.status = status;
		iterator = status.keySet().iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return iterator.hasNext();	
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public RobotStatus next() {
		UUID id =  iterator.next();
		return status.get(id);
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() { }

}
