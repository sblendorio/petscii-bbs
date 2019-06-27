package droid64.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import droid64.d64.DiskImage;

import static java.util.Arrays.asList;

public class Settings {
	private static final String SETTING_FILE_EXT_D64    = "file_ext_d64";
	private static final String SETTING_FILE_EXT_D67    = "file_ext_d67";
	private static final String SETTING_FILE_EXT_D71    = "file_ext_d71";
	private static final String SETTING_FILE_EXT_D81    = "file_ext_d81";
	private static final String SETTING_FILE_EXT_T64    = "file_ext_t64";
	private static final String SETTING_FILE_EXT_D80    = "file_ext_d80";
	private static final String SETTING_FILE_EXT_D82    = "file_ext_d82";
	private static final String SETTING_FILE_EXT_LNX    = "file_ext_lnx";
	private static final String SETTING_FILE_EXT_D64_GZ = "file_ext_d64_gz";
	private static final String SETTING_FILE_EXT_D67_GZ = "file_ext_d67_gz";
	private static final String SETTING_FILE_EXT_D71_GZ = "file_ext_d71_gz";
	private static final String SETTING_FILE_EXT_D81_GZ = "file_ext_d81_gz";
	private static final String SETTING_FILE_EXT_T64_GZ = "file_ext_t64_gz";
	private static final String SETTING_FILE_EXT_D80_GZ = "file_ext_d80_gz";
	private static final String SETTING_FILE_EXT_D82_GZ = "file_ext_d82_gz";
	private static final String SETTING_FILE_EXT_LNX_GZ = "file_ext_lnx_gz";

	/** Setting parameter definitions */
	private static final Map<String, Parameter> settingTypeMap = initMap();

	private static Map<String,Parameter> initMap() {
		Map<String,Parameter> map = new HashMap<>();
		map.put(SETTING_FILE_EXT_D64, new Parameter(SETTING_FILE_EXT_D64, asList(".d64") ));
		map.put(SETTING_FILE_EXT_D67, new Parameter(SETTING_FILE_EXT_D67, asList(".d67") ));
		map.put(SETTING_FILE_EXT_D71, new Parameter(SETTING_FILE_EXT_D71, asList(".d71") ));
		map.put(SETTING_FILE_EXT_D81, new Parameter(SETTING_FILE_EXT_D81, asList(".d81") ));
		map.put(SETTING_FILE_EXT_T64, new Parameter(SETTING_FILE_EXT_T64, asList(".t64") ));
		map.put(SETTING_FILE_EXT_D80, new Parameter(SETTING_FILE_EXT_D80, asList(".d80") ));
		map.put(SETTING_FILE_EXT_D82, new Parameter(SETTING_FILE_EXT_D82, asList(".d82") ));
		map.put(SETTING_FILE_EXT_LNX, new Parameter(SETTING_FILE_EXT_LNX, asList(".lnx") ));
		map.put(SETTING_FILE_EXT_D64_GZ, new Parameter(SETTING_FILE_EXT_D64_GZ, asList("d64.gz") ));
		map.put(SETTING_FILE_EXT_D67_GZ, new Parameter(SETTING_FILE_EXT_D67_GZ, asList("d67.gz") ));
		map.put(SETTING_FILE_EXT_D71_GZ, new Parameter(SETTING_FILE_EXT_D71_GZ, asList("d71.gz") ));
		map.put(SETTING_FILE_EXT_D81_GZ, new Parameter(SETTING_FILE_EXT_D81_GZ, asList("d81.gz") ));
		map.put(SETTING_FILE_EXT_T64_GZ, new Parameter(SETTING_FILE_EXT_T64_GZ, asList("t64.gz") ));
		map.put(SETTING_FILE_EXT_D80_GZ, new Parameter(SETTING_FILE_EXT_D80_GZ, asList("d80.gz") ));
		map.put(SETTING_FILE_EXT_D82_GZ, new Parameter(SETTING_FILE_EXT_D82_GZ, asList("d82.gz") ));
		map.put(SETTING_FILE_EXT_LNX_GZ, new Parameter(SETTING_FILE_EXT_LNX_GZ, asList("lnx.gz") ));

		return map;
	}

	public static Map<Integer,List<String>> getFileExtensionMap() {
		Map<Integer,List<String>> map = new HashMap<>();
		map.put(DiskImage.D64_IMAGE_TYPE, joinLists(getStringListParam(SETTING_FILE_EXT_D64), getStringListParam(SETTING_FILE_EXT_D64_GZ)));
		map.put(DiskImage.D67_IMAGE_TYPE, joinLists(getStringListParam(SETTING_FILE_EXT_D67), getStringListParam(SETTING_FILE_EXT_D67_GZ)));
		map.put(DiskImage.D71_IMAGE_TYPE, joinLists(getStringListParam(SETTING_FILE_EXT_D71), getStringListParam(SETTING_FILE_EXT_D71_GZ)));
		map.put(DiskImage.D80_IMAGE_TYPE, joinLists(getStringListParam(SETTING_FILE_EXT_D80), getStringListParam(SETTING_FILE_EXT_D80_GZ)));
		map.put(DiskImage.D81_IMAGE_TYPE, joinLists(getStringListParam(SETTING_FILE_EXT_D81), getStringListParam(SETTING_FILE_EXT_D81_GZ)));
		map.put(DiskImage.D82_IMAGE_TYPE, joinLists(getStringListParam(SETTING_FILE_EXT_D82), getStringListParam(SETTING_FILE_EXT_D82_GZ)));
		map.put(DiskImage.LNX_IMAGE_TYPE, joinLists(getStringListParam(SETTING_FILE_EXT_LNX), getStringListParam(SETTING_FILE_EXT_LNX_GZ)));
		map.put(DiskImage.T64_IMAGE_TYPE, joinLists(getStringListParam(SETTING_FILE_EXT_T64), getStringListParam(SETTING_FILE_EXT_T64_GZ)));
		return map;
	}

	private static <T> List<T> joinLists(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<>();
		list.addAll(list1);
		list.addAll(list2);
		return list;
	}

	private static List<String> getStringListParam(String name) {
		Parameter param = settingTypeMap.get(name);
		return param != null ? param.getStringListValue() : new ArrayList<String>();
	}

}
