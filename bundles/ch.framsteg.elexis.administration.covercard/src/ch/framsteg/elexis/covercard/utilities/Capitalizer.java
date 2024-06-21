/*******************************************************************************
 * Copyright 2024 Framsteg GmbH / olivier.debenath@framsteg.ch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ch.framsteg.elexis.covercard.utilities;

public class Capitalizer {

	public static String capitalize(String raw) {
		raw = raw.contains("--")?raw.replace("--", "-"):raw;
		String out = new String();
		if (raw.contains(" ")) {
			String[] arr1 = raw.split(" ");
			if (arr1.length > 1) {
				for (String part : arr1) {
					String low1 = part.toLowerCase();
					String cap1 = low1.substring(0, 1).toUpperCase() + low1.substring(1);
					if (out.isEmpty()) {
						out = cap1;
					} else {
						out = out + " " + cap1;
					}
				}
			}
		} else if (raw.contains("-")) {
			String[] arr2 = raw.split("-");
			if (arr2.length > 1) {
				for (String part : arr2) {
					String low1 = part.toLowerCase();
					String cap1 = low1.substring(0, 1).toUpperCase() + low1.substring(1);
					if (out.isEmpty()) {
						out = cap1;
					} else {
						out = out + "-" + cap1;
					}
				}
			}
		} else {
			String low = raw.toLowerCase();
			out = low.substring(0, 1).toUpperCase() + low.substring(1);
		}
		return out;
	}
}
