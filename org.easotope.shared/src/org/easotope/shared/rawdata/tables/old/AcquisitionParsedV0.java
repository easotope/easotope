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

package org.easotope.shared.rawdata.tables.old;

import java.util.HashMap;

import org.easotope.framework.dbcore.tables.TableObjectWithIntegerId;
import org.easotope.shared.rawdata.InputParameter;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName=AcquisitionParsedV0.TABLE_NAME)
public class AcquisitionParsedV0 extends TableObjectWithIntegerId {
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "ACQUISITIONPARSED_V0";
	public static final String RAWFILEID_FIELD_NAME = "RAWFILEID";
	public static final String VENDORID_FIELD_NAME = "VENDORID";
	public static final String DATE_FIELD_NAME = "DATE";
	public static final String NUMCYCLES_FIELD_NAME = "NUMCYCLES";
	public static final String MEASUREMENTS_FIELD_NAME = "MEASUREMENTS";
	public static final String BACKGROUND_FIELD_NAME = "BACKGROUND";

	@DatabaseField(columnName=RAWFILEID_FIELD_NAME, canBeNull=false, index=true)
	private int rawFileId;
	@DatabaseField(columnName=VENDORID_FIELD_NAME, canBeNull=false)
	private int vendorId;
	@DatabaseField(columnName=DATE_FIELD_NAME, canBeNull=false, index=true)
	private long date;
	@DatabaseField(columnName=NUMCYCLES_FIELD_NAME, canBeNull=false)
	private int numCycles;
	@DatabaseField(columnName=MEASUREMENTS_FIELD_NAME, canBeNull=false, dataType=DataType.SERIALIZABLE)
	private HashMap<InputParameter,Double[]> measurements;
	@DatabaseField(columnName=BACKGROUND_FIELD_NAME, canBeNull=false, dataType=DataType.SERIALIZABLE)
	private HashMap<InputParameter,Double> backgrounds;

	public AcquisitionParsedV0() { }

	public AcquisitionParsedV0(AcquisitionParsedV0 acquisitionParsed) {
		super(acquisitionParsed);

		this.rawFileId = acquisitionParsed.rawFileId;
		this.vendorId = acquisitionParsed.vendorId;
		this.date = acquisitionParsed.date;
		this.numCycles = acquisitionParsed.numCycles;
		this.measurements = acquisitionParsed.measurements == null ? null : new HashMap<InputParameter,Double[]>(acquisitionParsed.measurements);
		this.backgrounds = acquisitionParsed.backgrounds == null ? null : new HashMap<InputParameter,Double>(acquisitionParsed.backgrounds);
	}

	public int getRawFileId() {
		return rawFileId;
	}

	public void setRawFileId(int rawFileId) {
		this.rawFileId = rawFileId;
	}

	public int getVendorId() {
		return vendorId;
	}

	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getNumCycles() {
		return numCycles;
	}

	public void setNumCycles(int numCycles) {
		this.numCycles = numCycles;
	}

	public HashMap<InputParameter,Double[]> getMeasurements2() {
		return measurements;
	}

	public void setMeasurements2(HashMap<InputParameter,Double[]> measurements) {
		this.measurements = measurements;
	}

	public HashMap<InputParameter,Double> getBackgrounds2() {
		return backgrounds;
	}

	public void setBackgrounds2(HashMap<InputParameter,Double> backgrounds) {
		this.backgrounds = backgrounds;
	}
}
