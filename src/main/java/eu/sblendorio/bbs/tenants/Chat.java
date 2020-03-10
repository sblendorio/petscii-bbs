package eu.sblendorio.bbs.tenants;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.internal.StringUtil;

import eu.sblendorio.bbs.core.Keys;
import eu.sblendorio.bbs.core.PetsciiThread;

public class Chat extends PetsciiThread {

    public static class Row {
        long userId;
        String text;

        Row(long userId, String text) {
            this.userId = userId;
            this.text = text;
        }
    }

    List<Row> rows = new LinkedList<>();


    @Override
    public void doLoop() throws Exception {
        write(Keys.CLR);
        println();
        do {
            flushMessageBuffer();
            println("You are: " + getClientName());
            println("Users:");
            getClients().values().stream().filter(x -> !x.getClientName().equals(getClientName())).forEach(x -> println("- "+ x.getClientName()));
            print("Send to: ");
            String to = readLine();
            if (StringUtils.isBlank(to)) {
                continue;
            }
            Long receiverId = getClientIdByName(to);
            if (receiverId != null) {
                print("Message: ");
                String message = readLine();
                send(receiverId, message);
            }
        } while (true);

    }

    private void flushMessageBuffer() {
        rows.forEach(row -> println(getClients().get(row.userId).getClientName() + ": " + row.text));
        rows.clear();
    }

    @Override
    public void receive(long senderId, Object message) {
        rows.add(new Row(senderId, ObjectUtils.defaultIfNull(message.toString(), StringUtils.EMPTY)));
    }
}
