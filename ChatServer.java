import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatServer {
    private static final int PORT = 12345;
    static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server telah berjalan di port " + PORT);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client terhubung: " + clientSocket);

                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    Thread clientThread = new Thread(clientHandler);
                    clientThread.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedMessage = dateFormat.format(new Date()) + " - " + sender.getClientName() + ": " + message;

        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(formattedMessage);
            }
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private String clientName;

    public ClientHandler(Socket socket) {
        clientSocket = socket;
    }

    public String getClientName() {
        return clientName;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Menerima nama dari klien
            clientName = in.readLine();
            System.out.println("Client " + clientName + " terhubung.");

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(clientName + ": " + message);
                ChatServer.broadcastMessage(message, this);
            }

            System.out.println("Client " + clientName + " terputus.");
            in.close();
            out.close();
            clientSocket.close();
            ChatServer.clients.remove(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

