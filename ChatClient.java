import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Ganti dengan alamat server Anda
        int serverPort = 12345;

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Terhubung ke server. Masukkan nama Anda:");

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String name = userInput.readLine();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(name);

            Thread receiveThread = new Thread(new ReceiveThread(socket));
            receiveThread.start();

            System.out.println("Anda sekarang dapat mulai mengirim pesan:");

            while (true) {
                String message = userInput.readLine();
                out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ReceiveThread implements Runnable {
    private Socket socket;

    public ReceiveThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

