package app;

import connection.Multicast;
import connection.ResHandlerInterface;
import model.Message;
import model.Player;
import model.WordGenerator;
import ui.SimpleUI;
import util.KeyPairGen;


/**
 * Classe principal com as regras do jogo
 * Created by Bruno on 12/09/2016.
 */
public class App {
    private static int PORT = 4000;

    // Connection instances
    private Multicast multicast;
    private ResHandlerInterface handler;
    private KeepAlive keepAlive;

    // Model instances
    private Player player;
    private WordGenerator wordGenerator;

    // UI instances
    private SimpleUI ui;

    // keys
    private KeyPairGen keyPairGen;

    /**
     * O construtor inicialmente considera que o usuário é um jogador
     * É gerado as chaves publicas e privadas
     * É iniciado um interface com usuário por linha de comanda, pedindo os dados inciais do jogador
     * O handler é uma classe que filtra todas as mensagens
     * do multicast para identificar qual ação e usuário ela pertence;
     */
    public App() {
        this.player = new Player();
        genAndGetKey();
        ui = new SimpleUI(this);

        this.handler = new GenericHandler(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Inicia uma conexão multicast
     */
    private void connectMulticast() {
        multicast = new Multicast(PORT);
    }

    /**
     * Começa a escutar no multicast e envia um objeto handler para filtrar as mensagens
     */
    private void listen() {
        multicast.listenHandler(handler);
    }

    /**
     * inicia um keepalive para todos os processos se conhecerem
     */
    private void initKeepAlive() {
        System.out.println("Init KeepAlive TimerTask");
        keepAlive = new KeepAlive(multicast, player);
        keepAlive.sendKeepAlive(player.getPublicKey());
        keepAlive.timerTaskKeepAlive();
    }

    /**
     * Gera as chaves e guarda no objeto jogador
     */
    private void genAndGetKey(){
        this.keyPairGen = new KeyPairGen();
        keyPairGen.generateKeyPair();
        String key = keyPairGen.getPubKey();
        player.setPublicKey(key);
        player.setPrivateKey(keyPairGen.getPrivateKey());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * É chamado na main principal
     * inciia um multicast
     * pergunta o nome do usuário na linha de comando
     * incia o keep alive
     * e começa a escutar o multicast
     */
    public void initGame() {
        connectMulticast();
        ui.registerPlayer(player);
        initKeepAlive();
        listen();
    }

    /**
     * é chamado quando um novo jogador vira gerador de palavras.
     * Ele altera a mensagem de keepalive para dizer que agora esse player é um gerador
     */
    public void updatePlayerKeepAlive() {
        keepAlive.updatePlayer(player);
    }

    /**
     * Faz uma requisição multicast
     * @param message
     */
    public void request(Message message) {
        multicast.request(message);
    }

    /**
     * Quando um jogador vira gerador esse método cria um objeto do tipo gerador contendo as logicas
     * do gerador de palavras.
     */
    public void setNewGenerator() {
        wordGenerator = new WordGenerator();
    }

    public Player player() {
        return player;
    }

    public WordGenerator generator() {
        return wordGenerator;
    }

    public SimpleUI ui() {
        return ui;
    }

}
