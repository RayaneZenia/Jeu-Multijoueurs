package src.main.chatroom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private String username;  // Nom d'utilisateur du client
    private Server server;    // Référence au serveur pour envoyer des messages
    private Socket socket;    // Socket du client
    private BufferedWriter bufferedWriter;  // Pour envoyer des messages au client
    private BufferedReader bufferedReader;  // Pour lire les messages du client

    // Constructeur du ClientHandler
    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode run exécutée lorsqu'un nouveau thread est lancé
    @Override
    public void run() {
        setUsername();             // Définir le nom d'utilisateur
        startSendMessageLoop();    // Démarrer la boucle d'envoi de messages
    }

    // Méthode pour recevoir un message d'un autre client
    public void receiveMessage(ClientHandler sender, String message) {
        if (sender == this) {
            return;  // Ne pas envoyer le message à soi-même
        }

        try {
            // Envoyer le message au client
            bufferedWriter.write(sender.username + ": " + message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour recevoir un message du serveur
    public void receiveServerMessage(String message) {
        try {
            // Envoyer le message du serveur au client
            bufferedWriter.write("SERVER: " + message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour définir le nom d'utilisateur du client
    private void setUsername() {
        try {
            // Lire le nom d'utilisateur depuis l'entrée du client
            username = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour démarrer la boucle d'envoi de messages
    private void startSendMessageLoop() {
        try {
            while (socket.isConnected()) {
                // Lire les messages du client et les envoyer au serveur pour diffusion
                String message = bufferedReader.readLine();
                server.broadcastMessage(ClientHandler.this, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
