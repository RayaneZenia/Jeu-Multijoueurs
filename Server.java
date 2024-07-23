package src.main.chatroom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static final int port = 1234;  // Port utilisé par le serveur

    public static void main(final String[] args) throws IOException {
        // Créer un nouveau serveur avec le port spécifié
        new Server(new ServerSocket(port));
    }

    private final ServerSocket serverSocket;  // Socket du serveur
    private ArrayList<ClientHandler> clientHandlers;  // Liste des gestionnaires de clients

    // Constructeur du serveur
    public Server(final ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        startAcceptClientLoop();  // Démarrer la boucle d'acceptation des clients
    }

    // Méthode pour diffuser un message de l'expéditeur à tous les clients
    public void broadcastMessage(final ClientHandler sender, final String message) {
        for (final ClientHandler clientHandler : clientHandlers) {
            clientHandler.receiveMessage(sender, message);  // Envoyer le message à chaque client
        }
        System.out.println("Message has been broadcasted.");
    }

    // Méthode pour envoyer un message du serveur à tous les clients
    private void sendServerMessage(final String message) {
        for (final ClientHandler clientHandler : clientHandlers) {
            clientHandler.receiveServerMessage(message);  // Envoyer le message du serveur à chaque client
        }
        System.out.println("Server message has been broadcasted.");
    }

    // Méthode pour démarrer la boucle d'acceptation des clients
    private void startAcceptClientLoop() {
        System.out.println("Starting server.");
        clientHandlers = new ArrayList<ClientHandler>();  // Initialiser la liste des gestionnaires de clients
        try {
            while (!serverSocket.isClosed()) {
                System.out.println("Waiting for new client.");
                final Socket socket = serverSocket.accept();  // Accepter un nouveau client
                System.out.println("A new client has connected.");
                final ClientHandler clientHandler = new ClientHandler(this, socket);
                clientHandlers.add(clientHandler);  // Ajouter le gestionnaire de client à la liste
                System.out.println("A new client handler has been made and added.");
                new Thread(clientHandler).start();  // Démarrer le gestionnaire de client dans un nouveau thread
                System.out.println("A new client handler has been started.");
                sendServerMessage("A new user has joined.");  // Informer tous les clients qu'un nouvel utilisateur a rejoint
            }
        } catch (final IOException e) {
            System.out.println("There was an IOException.");
        }
    }
}
