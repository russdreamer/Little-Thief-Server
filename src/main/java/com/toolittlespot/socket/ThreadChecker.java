package com.toolittlespot.socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ThreadChecker extends Thread {
    private Socket socket;
    private PhotoThread checkingThread;
    private DataInputStream dis;

    public ThreadChecker(Socket socket, PhotoThread checkingThread, DataInputStream dis) {
        this.socket = socket;
        this.checkingThread = checkingThread;
        this.dis = dis;
    }

    @Override
    public void run() {
        try {
            while (checkingThread.isConnected){
                if (dis.read() == -1){
                    checkingThread.interrupt();

                    dis.close();
                }
            }
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
