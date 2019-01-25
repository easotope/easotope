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

package org.easotope.shared.rawdata.events;

import java.util.ArrayList;
import java.util.Hashtable;

import org.easotope.framework.dbcore.cmdprocessors.Event;
import org.easotope.framework.dbcore.tables.User;
import org.easotope.shared.core.AuthenticationKeys;
import org.easotope.shared.core.tables.Permissions;
import org.easotope.shared.rawdata.tables.Sample;

public class SampleUpdated extends Event {
	private static final long serialVersionUID = 1L;

	private Sample sample;
	private boolean hasChildren;
	private int oldProjectId;
	private boolean oldProjectHasChildren;
	private int oldUserId;
	private ArrayList<Integer> reassignedReplicateIds;

	@Override
	public boolean isAuthorized(Hashtable<String, Object> authenticationObjects) {
		User user = (User) authenticationObjects.get(AuthenticationKeys.USER);
		Permissions permissions = (Permissions) authenticationObjects.get(AuthenticationKeys.PERMISSIONS);
		return user.id == sample.getUserId() || user.getIsAdmin() || permissions.isCanEditAllReplicates();
	}

	public SampleUpdated(Sample sample, boolean hasChildren, int oldProjectId, boolean oldProjectHasChildren, int oldUserId, ArrayList<Integer> reassignedReplicateIds) {
		this.sample = sample;
		this.hasChildren = hasChildren;
		this.oldProjectId = oldProjectId;
		this.oldProjectHasChildren = oldProjectHasChildren;
		this.oldUserId = oldUserId;
		this.reassignedReplicateIds = reassignedReplicateIds;
	}

	public Sample getSample() {
		return sample;
	}

	public int getOldProjectId() {
		return oldProjectId;
	}

	public boolean getOldProjectHasChildren() {
		return oldProjectHasChildren;
	}

	public boolean isHasChildren() {
		return hasChildren;
	}

	public int getOldUserId() {
		return oldUserId;
	}

	public ArrayList<Integer> getReassignedReplicateIds() {
		return reassignedReplicateIds;
	}
}
