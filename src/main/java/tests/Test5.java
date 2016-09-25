package tests;

import app.App;
import app.GenericHandler;
import model.Message;
import model.Player;
import ui.SimpleUI;

/**
 * Created by Bruno on 14/09/2016.
 */
public class Test5 {
    public static void main(String[] args) {
        App app = new App();

        app.initGame();

        /*private void testkey(String pbk){
            byte[] test = new byte[5];
            test[0] = 0;
            test[1] = 1;
            test[2] = 2;
            test[3] = 3;
            test[4] = 4;
            byte[] out = new byte[5];


            try {
                out = KeyPairGen.encrypt(test, keyPairGen.getPrivateKey());
                for (int i=0; i<5; i++)
                    System.out.println(out[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("DECRIPT");

            try {
                byte[] outdec = new byte[5];
                outdec = KeyPairGen.decrypt(out, pbk);

                for (int i=0; i<5; i++)
                    System.out.println(outdec[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }
}
