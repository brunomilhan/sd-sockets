package app;

import connection.ResHandlerInterface;
import model.Message;
import model.WordGenerator;

/**
 * Created by Bruno on 12/09/2016.
 */
public class WordGenHandler implements ResHandlerInterface {
    private App app;
    private WordGenerator wordGenerator;

    public WordGenHandler( WordGenerator wordGenerator){
        this.wordGenerator = wordGenerator;
    }

    public void handler(Message message) {

        if (message.getType().equals(Message.CHAR)){
            wordGenerator.receiveChar(message, app);
        }
        if (message.getType().equals(Message.WORD)){
            wordGenerator.receiveWord(message, app);
        }
        if (message.getType().equals(Message.LEAVE)){
            wordGenerator.receiveLeave(message, app);
            System.out.println("saiu: " + new String(message.getBody()));

        }

        if (message.getType().equals(Message.KEEPALIVE)){
            this.wordGenerator.playerKeepAlive(app, message.getPlayer());
        }
        if (message.getType().equals(Message.GAME_INFO)){
            System.out.println(message.getBodyString());
        }

    }

    public void setApp(App app) {
        this.app = app;
    }
}
