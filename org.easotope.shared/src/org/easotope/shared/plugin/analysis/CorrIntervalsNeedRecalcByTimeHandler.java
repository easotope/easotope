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

package org.easotope.shared.plugin.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.easotope.framework.core.logging.Log;
import org.easotope.framework.core.logging.Log.Level;
import org.easotope.framework.dbcore.cmdprocessors.Event;
import org.easotope.framework.dbcore.util.RawFileManager;
import org.easotope.shared.admin.events.CorrIntervalsNeedRecalcByTime;
import org.easotope.shared.admin.events.CorrIntervalsNeedRecalcByTime.FromToRange;
import org.easotope.shared.analysis.events.CorrIntervalCompUpdated;
import org.easotope.shared.analysis.server.LoadOrCalculateCorrInterval;
import org.easotope.shared.analysis.tables.CorrIntervalV1;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

public class CorrIntervalsNeedRecalcByTimeHandler {
	public ArrayList<Event> execute(CorrIntervalsNeedRecalcByTime corrIntervalsNeedRecalcByTime, RawFileManager rawFileManager, ConnectionSource connectionSource) {
		HashSet<Integer> alreadyDidId = new HashSet<Integer>();
		ArrayList<Event> returnEvents = new ArrayList<Event>();

		try {
			Dao<CorrIntervalV1,Integer> corrIntervalDao = DaoManager.createDao(connectionSource, CorrIntervalV1.class);
			CorrIntervalCompUpdated corrIntervalCompUpdated = new CorrIntervalCompUpdated();

			for (FromToRange fromToRange : corrIntervalsNeedRecalcByTime.getFromToRanges()) {
				int massSpecId = fromToRange.getMassSpecId();
				long from = fromToRange.getFrom();
				long to = fromToRange.getTo();

				if (from == to) {
					to++;
				}

				List<CorrIntervalV1> corrIntervals = corrIntervalDao.queryForEq(CorrIntervalV1.MASSSPECID_FIELD_NAME, massSpecId);

				for (CorrIntervalV1 corrInterval : corrIntervals) {
					if (alreadyDidId.contains(corrInterval.getId())) {
						continue;
					}

					boolean inRange = (from <= corrInterval.getValidFrom() && to > corrInterval.getValidFrom());
					inRange = inRange || (from < corrInterval.getValidUntil() && to >= corrInterval.getValidUntil());
					inRange = inRange || (from >= corrInterval.getValidFrom() && to <= corrInterval.getValidUntil());

					if (inRange) {
						for (int dataAnalysisId : corrInterval.getDataAnalysis()) {
							if (LoadOrCalculateCorrInterval.removeCorrIntervalCalculations(corrInterval.getId(), dataAnalysisId, connectionSource)) {
								corrIntervalCompUpdated.add(corrInterval.getId(), dataAnalysisId);
							}
						}

						alreadyDidId.add(corrInterval.getId());
					}
				}
			}

			if (corrIntervalCompUpdated.size() != 0) {
				returnEvents.add(corrIntervalCompUpdated);
			}

		} catch (Exception e) {
			Log.getInstance().log(Level.INFO, this, "Error while recalculating correction interval by from-to dates.", e);
		}

		return returnEvents;
	}
}
