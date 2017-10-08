package com.tcp.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiTCPClient {
	private static class TCPWorker implements Runnable {

		private Socket socket;
		private DataOutputStream output;
		private BufferedReader server;
		private BufferedReader reader;
		private String serverbuffer;
		private String message;
		private String response;
		private int id;

		public TCPWorker(Socket socket,int id) {
			this.socket = socket;
			this.serverbuffer = "";
			this.id=id;
		}

		@Override
		public void run() {

			try {
				for (int i = 0; i < 300; i++) {
					this.output = new DataOutputStream(this.socket.getOutputStream());
					this.server = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
					this.reader = new BufferedReader(new InputStreamReader(System.in));
					this.message = "Hello, IP client: " + this.socket.getInetAddress() + " Port: "
							+ this.socket.getLocalPort() + " User: " + this.id + "\n";
					// this.message=reader.readLine() + System.lineSeparator();
					this.output.writeBytes(message);
					this.response = server.readLine();
					System.out.println("[" + new Date() + "] Received: " + response);
				}
				
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(10);

	public static void main(String[] args) {
		Socket socket = null;
		int i;
		try {
			for (i = 1; i <= 10; i++) {
				socket = new Socket("34.208.28.211", 80);
				TCP_WORKER_SERVICE.submit(new TCPWorker(socket,i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
