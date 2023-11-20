package eu.sblendorio.bbs.core;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class MinitelConstants {
    public static final int TYPE_UNKNOWN = 1000;
    public static final int TYPE_1B = 2000;
    public static final int TYPE_1B_OLD = 3000;
    public static final int TYPE_2 = 4000;
    public static final int TYPE_12 = 5000;
    public static final int TYPE_ADF = 6000;
    public static final int TYPE_EMULATOR = 9999;

    public static final String STRING_1B =     "0143753c04";
    public static final String STRING_1B_OLD = "0143623504";
    public static final String STRING_2 =      "0142763904";
    public static final String STRING_12 =     "01437a3604";
    public static final String STRING_ADF =    "0143763104";

    public static final Set<Integer> DRCS_SUPPORTING_TERMINALS = new HashSet<>(asList(
            TYPE_EMULATOR,
            //TYPE_ADF,
            TYPE_2,
            TYPE_12
    ));

}