/*
 * Copyright © 2016-2019 by Devon Bowen.
 *
 * This file is part of Easotope.
 *
 * Easotope is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify this Program, or any covered work, by linking or combining
 * it with the Eclipse Rich Client Platform (or a modified version of that
 * library), containing parts covered by the terms of the Eclipse Public
 * License, the licensors of this Program grant you additional permission
 * to convey the resulting work. Corresponding Source for a non-source form
 * of such a combination shall include the source code for the parts of the
 * Eclipse Rich Client Platform used as well as that of the covered work.
 *
 * Easotope is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Easotope. If not, see <http://www.gnu.org/licenses/>.
 */

package org.easotope.framework.commands;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.easotope.framework.dbcore.cmdprocessors.Event;
import org.easotope.framework.dbcore.util.RawFileManager;

import com.j256.ormlite.support.ConnectionSource;

public abstract class Command implements Serializable {
	private static final long serialVersionUID = 1L;

	public static int UNDEFINED_ID = Integer.MIN_VALUE;
	public enum Status { NONE, PERMISSION_ERROR, EXECUTION_ERROR, DB_ERROR, VERIFY_AND_RESEND, OK };

	private static int clientUniqueIdCounter = UNDEFINED_ID + 1;

	protected int clientUniqueId = UNDEFINED_ID;
	private volatile Status status = Status.NONE;
	private volatile String message = null;
	private ArrayList<Event> events = null;
	private transient int socketId = 0;

	public Command() {
		clientUniqueId = generateClientUniqueId();
	}
	
	private synchronized static int generateClientUniqueId() {
		clientUniqueIdCounter = (clientUniqueIdCounter == Integer.MAX_VALUE) ? UNDEFINED_ID + 1 : clientUniqueIdCounter + 1;		
		return clientUniqueIdCounter;
	}

	// generated by the client so not unique if called on the server
	public int getClientUniqueId() {
		return clientUniqueId;
	}

	public String getName() {
		return getClass().getSimpleName() + "()";
	}

	public void setStatus(Status status) {
		setStatus(status, null);
	}

	// TODO message should probably be a code rather than text so that i18n works
	public void setStatus(Status status, String message) {
		this.status = status;
		this.message = message;
	}

	public void setStatus(Status status, String message, Object[] parameters) {
		this.status = status;
		this.message = (message != null) ? MessageFormat.format(message, parameters) : null;
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	protected void addEvent(Event event) {
		if (events == null) {
			events = new ArrayList<Event>();
		}

		events.add(event);
	}

	public void addEvents(ArrayList<Event> events) {
		if (events == null) {
			events = new ArrayList<Event>();
		}

		events.addAll(events);
	}
	
	protected void removeEvent(Class<?> clazz) {
		if (events == null) {
			return;
		}

		for (Iterator<Event> i = events.iterator(); i.hasNext(); ) {
			Event event = i.next();

			if (event.getClass() == clazz) {
				i.remove();
			}
		}
	}

	public ArrayList<Event> getAndRemoveEvents() {
		ArrayList<Event> events = this.events;
		this.events = null;
		return events;
	}

	public void pauseBeforeExecute() {
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
	}

	abstract public boolean authenticate(ConnectionSource connectionSource, RawFileManager rawFileManager, Hashtable<String,Object> authenticationObjects) throws Exception;
	abstract public void execute(ConnectionSource connectionSource, RawFileManager rawFileManager, Hashtable<String,Object> authenticationObjects) throws Exception;

	public int getSocketId() {
		return socketId;
	}

	public void setSocketId(int socketId) {
		this.socketId = socketId;
	}
}
