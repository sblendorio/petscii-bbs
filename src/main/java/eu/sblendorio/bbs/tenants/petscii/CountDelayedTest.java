package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.lang3.math.NumberUtils;

public class CountDelayedTest extends PetsciiThread {
    @Override
    public void doLoop() throws Exception {
        long init = NumberUtils.toLong(System.getProperty("init", "0"));
        long ms = NumberUtils.toLong(System.getProperty("delay", "2"));
        int threshold = NumberUtils.toInt(System.getProperty("threshold", "150"));
        Thread.sleep(init);
        String s = "";
        int count = 0;
        for (int i=1; i<=276; ++i) s += " "+ i;
        for (char ch: s.toCharArray()) {
            write(ch);
            ++count;
            if (count >= threshold) {
                count = 0;
                Thread.sleep(ms);
            }
        }
        flush();
        resetInput(); readKey();
    }
}
