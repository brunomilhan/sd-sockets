package app;

import connection.ResHandlerInterface;
import model.Message;

/**
 * Created by Bruno on 14/09/2016.
 */
public class GenericHandler implements ResHandlerInterface {
    private App app;

    public GenericHandler(App app) {
        this.app = app;
    }

    public void handler(Message message) {
        boolean haveCheck = false;
        if (message.getType().equals(Message.KEEPALIVE)) {
            app.player().handlerKeepAlive(app, message);
        } else
            haveCheck = true;

        if (haveCheck) {

            if (CheckPubKey.check(app, message)) {
                System.out.println(message.getType()  + "pl: " + message.getBodyString() );
                String type = message.getType();
                if (type.equals(Message.NEW_GEN_REQUEST)) {
                    app.player().checkNewGenerator(app, message);
                }
                if (type.equals(Message.GAME_INFO)) {
                    System.out.println(message.getBodyString());
                }
                if (type.equals(Message.I_AM_GENERATOR)) {
                    app.player().updateGenerator(message);
                }
               /* if (type.equals(Message.EXPIRE_TIME_1)){
                    app.player().timedOut(app, message);
                }*/
                // WORDGENERATOR METHODS
                if (app.player().isGenerator()) {
                    if (type.equals(Message.CHAR)) {
                        app.generator().receiveChar(message, app);
                    }
                    if (type.equals(Message.WORD)) {
                        app.generator().receiveWord(message, app);
                    }
                    if (type.equals(Message.LEAVE)) {
                        app.generator().receiveLeave(message, app);
                    }
                    if (type.equals(Message.EXPIRE_TIME_1)) {
                        app.generator().countMatchesFails(message, app);
                    }
                    /*if (message.getType().equals(Message.GAME_INFO)) {
                        System.out.println(message.getBodyString());
                    }
                    if (message.getType().equals(Message.I_AM_GENERATOR)) {
                        app.player().updateGenerator(message);
                    }*/
                    // PLAYER METHODS
                } else {
                    if (message.getType().equals(Message.NEXT)) {
                        app.player().isNext(app, message);
                    }
                }
            }
        }
    }
}

