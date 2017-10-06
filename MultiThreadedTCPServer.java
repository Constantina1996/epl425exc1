package com.tcp.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedTCPServer {

    private static class TCPWorker implements Runnable {

        private Socket client;
        private String clientbuffer;

        public TCPWorker(Socket client) {
            this.client = client;
            this.clientbuffer = "";
        }

        @Override
        public void run() {

            try {
                System.out.println("Client connected with: " + this.client.getInetAddress());

                DataOutputStream output = new DataOutputStream(client.getOutputStream());
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(this.client.getInputStream())
                );

                this.clientbuffer = reader.readLine();
                System.out.println("[" + new Date() + "] Received: " + this.clientbuffer);
                output.writeBytes(this.clientbuffer.toUpperCase() + System.lineSeparator());
            } catch (IOException e) {
               e.printStackTrace();
            }

        }

    }

    public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(10);

    public static void main(String args[]) {
        try {
            ServerSocket socket = new ServerSocket(80);
            System.out.println("Server listening to: " + socket.getInetAddress() + ":" + socket.getLocalPort());

            while (true) {
                Socket client = socket.accept();

                TCP_WORKER_SERVICE.submit(
                        new TCPWorker(client)
                );

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

