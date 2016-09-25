package app;

import model.Message;
import model.Player;
import util.KeyPairGen;

/**
 * Created by Bruno on 21/09/2016.
 */
public class CheckPubKey {
    public static boolean check(App app, Message message) {
        for (Player p : app.player().players()) {
            if (message.getPlayer().equals(p.getName())) {
                try {
                    String check = KeyPairGen.decrypt(message.getCheck(), p.getPublicKey());
                    if (check.equals(Message.CHECK))
                        return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
