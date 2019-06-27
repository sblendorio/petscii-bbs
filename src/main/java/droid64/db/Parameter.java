package droid64.db;

import java.util.ArrayList;
import java.util.List;

/**<pre style='font-family:sans-serif;'>
 * Created on 03.05.2018
 *
 *   droiD64 - A graphical file manager for D64 files
 *   Copyright (C) 2004 Wolfram Heyer
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *   eMail: wolfvoz@users.sourceforge.net
 *   http://droid64.sourceforge.net
 *
 * @author henrik
 * </pre>
 */
public class Parameter {

	private String name;
	private Object value;

	public Parameter(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		Parameter that = (Parameter) obj;
		if (!stringsEqual(name, that.name)) {
			return false;
		}
		if (this.value == null || that.value == null) {
			return this.value == null && that.value == null;
		}
		return this.value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return (value != null ? value.hashCode() : 0) + (name != null ? name.hashCode() : 0);
	}

	private boolean stringsEqual(String s1, String s2) {
		if (s1 == s2) {
			return true;
		}
		if (s1 == null || s2 == null) {
			return false;
		}
		return s1.equals(s2);
	}

	public String getName() {
		return name;
	}
	public List<String> getStringListValue() {
		if (value == null) {
			value = new ArrayList<String>();
		}
		if (value instanceof List) {
			return (List<String>) value;
		}
		throw new IllegalArgumentException(name + " is not a list parameter. ");
	}

}
