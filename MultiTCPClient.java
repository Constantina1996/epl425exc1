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
		private static int counter=1;

		public TCPWorker(Socket socket) {
			this.socket = socket;
			this.serverbuffer = "";
		}

		@Override
		public void run() {
		
			try {
				this.output = new DataOutputStream(this.socket.getOutputStream());
				this.server = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				this.reader = new BufferedReader(new InputStreamReader(System.in));
				this.message = "Hello, IP client: "+this.socket.getInetAddress()+"Port: "+this.socket.getLocalPort()+"User: "+this.counter+++"\n";
				//this.message=reader.readLine() + System.lineSeparator();
				this.output.writeBytes(message);
				this.response = server.readLine();
				System.out.println("[" + new Date() + "] Received: " + response);
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
			for (i = 0; i < 10; i++) {
				socket = new Socket("34.212.247.247", 80);
				TCP_WORKER_SERVICE.submit(new TCPWorker(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
