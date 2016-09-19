package ui;

import app.App;
import connection.Multicast;
import model.Message;
import model.Move;
import model.Player;
import model.WordGenerator;

import java.util.Scanner;

/**
 * Created by Bruno on 13/09/2016.
 */
public class SimpleUI {
    private Scanner in = new Scanner(System.in);

    public void registerPlayer(Player player) {
        line();
        System.out.println("JOGO DA FORCA");
        line();
        System.out.println("Nome jogador: ");
        player.setName(in.nextLine());
        line();
    }

    public void registerWordGen(WordGenerator wordGenerator, App app){
        line();
        System.out.println("JOGO DA FORCA - GERADOR DE PALAVRAS");
        line();
        System.out.println("Digite a palavra: ");
        wordGenerator.setFinalWord(in.next());
        System.out.println("Aguardando jogadores...");
        app.request(new Message(wordGenerator, Message.NEXT, Message.NULL));
        line();
    }

    public void round(Player player, App app) {
        int option;
        line();
        System.out.println("Nova rodada");
        System.out.println(" Opções disponiveis:\n" +
                "  1 - Chutar Letra\n" +
                "  2 - Chutar palavra\n" +
                "  3 - Desistir\n"
        );

        option = in.nextInt();
        String s = "";
        switch (option) {
            case 1:
                System.out.println("Digite a letra: ");
                s = in.next();
                app.request(new Message(player, Message.CHAR, s));
                break;
            case 2:
                System.out.println("Digite a palavra: ");
                s = in.next();
                app.request(new Message(player, Message.WORD, s));
                break;
            case 3:
                System.out.println("Você desistiu dessa rodada");
                app.request(new Message(player, Message.LEAVE, player.getName()));
                break;
        }
        line();
    }

    public static void line() {
        System.out.println("=====================================");
    }

    public void nextRound() {
        line();
        System.out.println("Jogada Disponivel!!!");
        line();
    }
}
