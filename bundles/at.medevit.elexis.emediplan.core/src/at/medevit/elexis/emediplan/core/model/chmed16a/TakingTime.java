/*******************************************************************************
 * Copyright (c) 2017 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.emediplan.core.model.chmed16a;

import java.util.ArrayList;
import java.util.List;

public class TakingTime {
	public Integer Off;
	public Integer Du;
	public Double DoFrom;
	public Double DoTo;
	public Double A;
	public Double MA;
	
	private static Integer[] secondsOffsets = {
		8 * 3600, 12 * 3600, 16 * 3600, 20 * 3600
	};
	
	public static List<TakingTime> fromFloats(List<Float> floats, boolean reserve){
		List<TakingTime> ret = new ArrayList<>();
		for (int i = 0; i < floats.size(); i++) {
			TakingTime tt = new TakingTime();
			tt.Off = secondsOffsets[i];
			if (reserve) {
				tt.A = floats.get(i).doubleValue();
			} else {
				tt.DoFrom = floats.get(i).doubleValue();
			}
			ret.add(tt);
		}
		return ret;
	}
}
