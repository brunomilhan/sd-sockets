package model;

import util.KeyPairGen;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;

/**
 * Classe que encapsula as mensagens enviadas por multicast
 * Quando a mensagem é enviada o objeto é transoformado em string
 * E quando é recebido a string é transformada nesse objeto
 * Created by Bruno on 07/09/2016.
 */
public class Message {
    private String bodyString;
    private byte[] body;
    private String player;
    private int playerID;
    private String resStatus;
    private String type;
    private String check;

    private PrivateKey privateKey;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Message(String body) {
        this.body = new byte[1000];
        this.body = body.getBytes();
    }

    /**
     * Criar um objeto mensagem a partir dos bytes recebidos do socket
     *
     * @param body
     */
    public Message(byte[] body) {
        this.body = new byte[1000];
        this.body = body;
        mountMsgObj();
    }

    /**
     * Criar uma mensagem a ser enviada por um socket
     *
     * @param player
     * @param type
     * @param body
     */
    public Message(Player player, String type, String body) {
        this.body = new byte[1000];
        this.privateKey = player.getPrivateKey();
        this.player = player.getName();
        this.playerID = player.getId();
        this.type = type;
        this.body = body.getBytes();
        encryptCheck();
        mountMsgString();
    }

    public Message(Player player, String type, String body, PrivateKey privateKey) {
        this.body = new byte[1000];
        this.privateKey = privateKey;
        this.player = player.getName();
        this.playerID = player.getId();
        this.type = type;
        this.body = body.getBytes();
        encryptCheck();
        mountMsgString();
    }

    public Message() {
        this.body = new byte[1000];
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void encryptCheck() {
        if (!type.equals(KEEPALIVE)) {
            try {
                check = KeyPairGen.encrypt(CHECK, privateKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            check = "no";
    }

    private void mountMsgString() {
        String aux = "check:=" + this.check + ";"
                + "player:=" + this.player + ";"
                + "id:=" + this.playerID + ";"
                + "type:=" + this.type + ";"
                + "body:=" + new String(this.body) + ";";

        this.body = aux.getBytes();
        //System.out.println("Msg Mounted= " + aux);
    }

    private void mountMsgObj() {
        String aux = null;
        try {
            aux = new String(this.body, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] split = aux.split(";");
        //System.out.println("msg " + aux);
        this.check = split[0].split(":=")[1];
        this.player = split[1].split(":=")[1];
        this.playerID = Integer.parseInt(split[2].split(":=")[1]);
        this.type = split[3].split(":=")[1];
        this.bodyString = split[4].split(":=")[1];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setResStatus(String resStatus) {
        this.resStatus = resStatus;
    }

    public String getCheck() {
        //System.out.println("check enc " + check);
        return check;
    }

    public int getLength() {
        return body.length;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyString() {
        return bodyString;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getType() {
        return type;
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return new String(body);
    }

    public int getPlayerID() {
        return playerID;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Static message types
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Player types
    public static final String CHAR = "char";
    public static final String WORD = "word";
    public static final String LEAVE = "leave";

    // WordGenerator types
    public static final String NEXT = "next";
    public static final String NULL = "null";
    public static final String KEEPALIVE = "keepalive";
    public static final String GAME_INFO = "refresh_info";
    public static final String GEN_WORD = "gen_word";
    public static final String NEW_GEN_REQUEST = "NEW_GEN_REQUEST";
    public static final String I_AM_GENERATOR = "I_AM_GENERATOR";
    public static final String CHECK = "HANSZIMMER";
    public static final String EXPIRE_TIME_1 = "EXPIRE_TIME_1";
    public static final String EXPIRE_TIME_2 = "EXPIRE_TIME_2";
    public static final String PLAYER_LEAVE = "PLAYER_LEAVE";
}
