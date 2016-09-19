package tests;

import app.App;
import app.WordGenHandler;
import model.WordGenerator;
import ui.SimpleUI;

/**
 * Created by Bruno on 12/09/2016.
 */
public class Test {

    public static void main(String[] args){
        SimpleUI ui = new SimpleUI();
        WordGenerator wordGenerator = new WordGenerator();

        WordGenHandler wordGenHandler = new WordGenHandler(wordGenerator);
        App app = new App(wordGenHandler);
        wordGenHandler.setApp(app);
        app.connectMulticast(4000);
        ui.registerWordGen(wordGenerator, app);
        app.listen();



    }
}
