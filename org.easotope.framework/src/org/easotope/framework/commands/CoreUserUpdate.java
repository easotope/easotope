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

import java.util.Hashtable;

import org.easotope.framework.Messages;
import org.easotope.framework.dbcore.AuthenticationKeys;
import org.easotope.framework.dbcore.DatabaseConstants;
import org.easotope.framework.dbcore.events.CoreUserNameUpdated;
import org.easotope.framework.dbcore.events.CoreUserUpdated;
import org.easotope.framework.dbcore.tables.User;
import org.easotope.framework.dbcore.util.RawFileManager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

public class CoreUserUpdate extends Command {
	private static final long serialVersionUID = 1L;

	protected User user;

	@Override
	public String getName() {
		return getClass().getSimpleName() + "(username=" + user.username + ")";
	}

	@Override
	public boolean authenticate(ConnectionSource connectionSource, RawFileManager rawFileManager, Hashtable<String,Object> authenticationObjects) throws Exception {
		User user = (User) authenticationObjects.get(AuthenticationKeys.USER);
		return user.isAdmin;
	}

	@Override
	public void execute(ConnectionSource connectionSource, RawFileManager rawFileManager, Hashtable<String,Object> authenticationObjects) throws Exception {
		Dao<User,Integer> userDao = DaoManager.createDao(connectionSource, User.class);

		if (user.getId() == DatabaseConstants.EMPTY_DB_ID) {
			if (userDao.queryForEq(User.USERNAME_FIELD_NAME, user.getUsername()).size() != 0) {
				setStatus(Command.Status.EXECUTION_ERROR, Messages.userUpdate_exists, new Object[] { user.getUsername() } );
				return;
			}

			userDao.create(user);

		} else {
			if (userDao.queryForId(user.getId()) == null) {
				setStatus(Command.Status.EXECUTION_ERROR, Messages.userUpdate_doesNotExist, new Object[] { user.getId() } );
				return;
			}

			userDao.update(user);
		}

		addEvent(new CoreUserUpdated(user));
		addEvent(new CoreUserNameUpdated(user.getId(), user.getUsername()));
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
}
