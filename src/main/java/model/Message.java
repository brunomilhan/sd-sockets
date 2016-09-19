package model;

import java.io.UnsupportedEncodingException;

/**
 * Created by Bruno on 07/09/2016.
 */
public class Message {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Static message types
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Player types
    public static final String CHAR = "char";
    public static final String WORD = "word";
    public static final String LEAVE = "leave";

    // WordGenerator types
    public static final String NEXT = "next";
    public static final String NULL = "null" ;
    public static final String KEEPALIVE = "keepalive";
    public static final String GAME_INFO = "refresh_info";
    public static final String GEN_WORD = "gen_word";
    public static final String NEW_GEN_REQUEST = "NEW_GEN_REQUEST";


    private String bodyString;
    private byte[] body;
    private String player;
    private String resStatus;
    private String type;

    private int isGenerator;

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
        this.player = player.getName();
        this.type = type;
        this.body = body.getBytes();

        mountMsgString();
    }


    public Message() {
        this.body = new byte[1000];
    }

    /***
     * This method handler raw messages and construct correctly body     *
     *
     * @param body     request message
     * @param fromPort origin client port
     */
    public Message(String body, int fromPort) {
        this.body = new byte[1000];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void mountMsgString() {
        String aux = "player=" + this.player + ";"
                + "type=" + this.type + ";"
                + "body=" + new String(this.body) + ";";

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

        this.player = split[0].split("=")[1];
        this.type = split[1].split("=")[1];
        //String[] aux1 = split[2].split("=")[1].split(";");
        this.bodyString = split[2].split("=")[1];

        //System.out.println("Msg obj Mounted= " + type + " - " + player + " - " + getStringBody());

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setResStatus(String resStatus) {
        this.resStatus = resStatus;
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

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getPlayer() {
        return player;
    }

    public int getIsGenerator() {
        return isGenerator;
    }

    @Override
    public String toString() {
        return new String(body);
    }



}
