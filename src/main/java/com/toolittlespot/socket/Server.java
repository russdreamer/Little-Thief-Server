package com.toolittlespot.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.toolittlespot.Contants.PORT;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);

        while (! serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            System.out.println("New client request received : " + socket);

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            long id = dis.readLong();

            PhotoThread thread = new PhotoThread(id, dos);
            ThreadChecker checker = new ThreadChecker(socket, thread, dis);

            thread.start();
            checker.start();
        }
    }
}
