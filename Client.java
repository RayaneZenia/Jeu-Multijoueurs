package src.main.chatroom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(final String[] args) {
        // Initialiser le client en utilisant le port du serveur
        new Client(Server.port);
    }

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    // Constructeur du client qui initialise la connexion au serveur
    public Client(final int port) {
        try {
            // Se connecter au serveur sur localhost avec le port donné
            socket = new Socket("localhost", port);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Définir le nom d'utilisateur
            setUsername();

            // Démarrer les boucles de lecture et d'envoi de messages
            startReadMessageLoop();
            startSendMessageLoop();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour définir le nom d'utilisateur
    private void setUsername() {
        System.out.print("Entrez votre nom d'utilisateur : ");
        try (Scanner scanner = new Scanner(System.in)) {
            // Lire le nom d'utilisateur depuis l'entrée standard et l'envoyer au serveur
            bufferedWriter.write(scanner.nextLine());
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        System.out.println("Nom d'utilisateur envoyé au serveur.");
    }

    // Méthode pour démarrer la boucle d'envoi de messages
    private void startSendMessageLoop() {
        try (Scanner scanner = new Scanner(System.in)) {
            // Tant que le socket est connecté, lire les messages de l'utilisateur et les envoyer au serveur
            while (socket.isConnected()) {
                bufferedWriter.write(scanner.nextLine());
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            System.out.println("Boucle d'envoi de messages terminée.");
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    // Méthode pour démarrer la boucle de lecture de messages
    private void startReadMessageLoop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Tant que le socket est connecté, lire les messages du serveur et les afficher
                    while (socket.isConnected()) {
                        System.out.println(bufferedReader.readLine());
                    }
                    System.out.println("Boucle de lecture de messages terminée.");
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }).start(); // Démarrer le thread de lecture des messages
    }
}
