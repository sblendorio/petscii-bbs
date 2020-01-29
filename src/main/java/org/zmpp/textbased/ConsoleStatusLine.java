package org.zmpp.textbased;

import org.zmpp.vm.StatusLine;

public class ConsoleStatusLine implements StatusLine {


    @Override
    public void updateStatusScore(String objectName, int score, int steps) {
        System.out.println("objectname="+objectName+", score="+score+", steps="+steps);
    }

    @Override
    public void updateStatusTime(String objectName, int hours, int minutes) {
        System.out.println("objectName="+objectName+", hours="+hours+", minutes="+minutes);
    }
}
