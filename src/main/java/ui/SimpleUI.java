package ui;

import app.App;
import model.Message;
import model.Player;

import java.util.Scanner;

/**
 * Created by Bruno on 13/09/2016.
 */
public class SimpleUI {
    private App app;
    private Scanner in = new Scanner(System.in);
    private Thread thread;
    private boolean isRunning;

    public SimpleUI(App app) {
        this.app = app;
        this.isRunning = false;
    }

    public void registerPlayer(Player player) {
        line();
        System.out.println("JOGO DA FORCA");
        line();
        System.out.println("Nome jogador: ");
        player.setName(in.nextLine());
        line();
    }

    public void registerWordGen() {
        isRunning = true;
        thread = new Thread(new Runnable() {
            public void run() {
                line();
                System.out.println("JOGO DA FORCA - GERADOR DE PALAVRAS");
                line();
                System.out.println("Digite a palavra: ");
                app.generator().setFinalWord(in.next());
                System.out.println("Aguardando jogadores...");
                app.generator().requestFirstPlayer(app);
                line();
                isRunning = false;

                try {
                    finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void round() {
        isRunning = true;
        thread = new Thread(new Runnable() {
            public void run() {
                int option;
                line();
                System.out.println("Nova rodada");
                System.out.println(" Opções disponiveis:\n" +
                        "  1 - Chutar Letra\n" +
                        "  2 - Chutar palavra\n" +
                        "  3 - Desistir\n"
                );

                try {
                    option = in.nextInt();
                    String s = "";
                    switch (option) {
                        case 1:
                            System.out.println("Digite a letra: ");
                            s = in.next();
                            app.player().requestChar(app, s);
                            break;
                        case 2:
                            System.out.println("Digite a palavra: ");
                            s = in.next();
                            app.request(new Message(app.player(), Message.WORD, s));
                            break;
                        case 3:
                            System.out.println("Você desistiu dessa rodada");
                            app.request(new Message(app.player(), Message.LEAVE, app.player().getName()));
                            break;
                    }
                    line();
                    isRunning = false;
                } catch (Exception e) {
                    System.out.println("deu erro ao finalizar");

                }
                try {
                    finalize();
                } catch (Throwable throwable) {

                    throwable.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void cancelInputThread() {
        thread.interrupt();
    }

    public static void line() {
        System.out.println("=====================================");
    }

    public void nextRound() {
        line();
        System.out.println("Jogada Disponivel!!!");
        line();
    }

    public boolean isRunning() {
        return isRunning;
    }
}
