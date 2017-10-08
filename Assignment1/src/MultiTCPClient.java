
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
	static int client = 0;

	private static class TCPWorker implements Runnable {

		private Socket socket;
		private DataOutputStream output;
		private BufferedReader server;
		private BufferedReader reader;
		private String serverbuffer;
		private String message;
		private String response;
		private static double latencyTime;
		private Date date;
		private double startTimeReq;
		private double finishedTimeReq;
		private int id;
		private int c;
		private int r;

		public TCPWorker(Socket socket, int id, int c, int r) {
			this.socket = socket;
			this.serverbuffer = "";
			this.id = id;
			this.c = c;
			this.r = r;
			this.date = new Date();
		}

		@Override
		public void run() {

			try {
				for (int i = 0; i < r; i++) {
					this.output = new DataOutputStream(this.socket.getOutputStream());
					this.server = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
					this.reader = new BufferedReader(new InputStreamReader(System.in));
					this.message = "Hello, IP client: " + this.socket.getInetAddress() + " Port: "
							+ this.socket.getLocalPort() + " User: " + this.id + "\n";

					// this.message=reader.readLine() + System.lineSeparator();
					this.output.writeBytes(message);
					this.startTimeReq = (double) System.currentTimeMillis() / 1000;
					this.response = server.readLine();
					this.finishedTimeReq = (double) System.currentTimeMillis() / 1000;
					this.latencyTime += (double) (finishedTimeReq - startTimeReq);
					System.out.println("[" + new Date() + "] Received : " + response);
				}
				this.output = new DataOutputStream(this.socket.getOutputStream());
				this.server = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				this.reader = new BufferedReader(new InputStreamReader(System.in));
				client++;
				if (client < c) {
					this.message = "FINISHED" + "\n";
					this.output.writeBytes(message);
					this.socket.close();
				} else {
					this.message = "CLIENTS FINISHED" + "\n";
					this.output.writeBytes(message);
					System.out.println("Average latency time=" + latencyTime / (c * r) + " sec");
					this.response = server.readLine();
					while (true) {
						if (response.equals("DONE")) {
							this.socket.close();
							System.exit(0);
						}
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(2);

	public static void main(String[] args) {
		Socket socket = null;
		int i;
		String cl = args[0];
		String req = args[1];
		int c = Integer.parseInt(cl);
		int r = Integer.parseInt(req);

		try {
			for (i = 1; i <= c; i++) {
				socket = new Socket("34.211.21.218", 80);
				TCP_WORKER_SERVICE.submit(new TCPWorker(socket, i, c, r));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
