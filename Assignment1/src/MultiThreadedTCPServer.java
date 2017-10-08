
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedTCPServer {
	public static final int range01=300;
	public static final int range02=2000;

	private static class TCPWorker implements Runnable {

		private Socket client;
		private String clientbuffer;
		private String[] userId;
		private static int req = 0;
		private double startedTime;
		private double finishedTime;
		private static double seconds;
		private double throughput;

		public TCPWorker(Socket client) {
			this.client = client;
			this.clientbuffer = "";
			this.seconds = 0.0;
		}

		@Override
		public void run() {

			try {
				System.out.println("Client connected with: " + this.client.getInetAddress());
				DataOutputStream output = new DataOutputStream(client.getOutputStream());
				BufferedReader reader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
				this.clientbuffer = reader.readLine();
				while (!this.clientbuffer.equals("FINISHED") && !this.clientbuffer.equals("CLIENTS FINISHED")) {
					this.startedTime =  (double) System.currentTimeMillis() / 1000.0;
					this.userId = clientbuffer.split(" ");
					Random randomSize = new Random(System.currentTimeMillis());
	                int finalSize = (randomSize.nextInt(range02-range01+1)+range01)*1024;
	                char[] payloadString = new char[finalSize];
	                Arrays.fill(payloadString,'m');
	                String payload = new String(payloadString);
					output.writeBytes("WELCOME <" + this.userId[7] + ">" + payload + System.lineSeparator());
					this.finishedTime =  (double) System.currentTimeMillis() / 1000.0;
					this.seconds +=(double) (finishedTime - startedTime);
					System.out.println("[" + new Date() + "] Received: " + this.clientbuffer);
					output = new DataOutputStream(client.getOutputStream());
					reader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
					this.clientbuffer = reader.readLine();
					this.req++;
				}
				if (this.clientbuffer.equals("FINISHED")){
					client.close();
				}else {
					this.throughput = ((double)req / seconds);
					System.out.println("Average server throughput =" + throughput);
					output = new DataOutputStream(client.getOutputStream());
					output.writeBytes("DONE"+"\n");
					this.req=0;
					this.seconds=0.0;
					client.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(10);

	public static void main(String args[]) {
		try {
			int port =Integer.parseInt(args[0]);
			int rep = Integer.parseInt(args[1]);
			ServerSocket socket = new ServerSocket(port);
			System.out.println("Server listening to: " + socket.getInetAddress() + ":" + socket.getLocalPort());
			while (true) {
				Socket client = socket.accept();

				TCP_WORKER_SERVICE.submit(new TCPWorker(client));

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
