import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ServerManager {
	
	private ServerSocket mListener;
	private List<ServerThread> mListServerThread = new ArrayList<>();
	private HashMap<String, BufferedWriter> outMaps = new HashMap<>();
	public ServerManager(){
		
	}
	
	public List<ServerThread> getListServerThread(){
		return this.mListServerThread;
	}
	
	public void startServer(){
		try {
			mListener = new ServerSocket(9090);
			while(true){
				System.out.println("Waiting for connection....");
				ServerThread handleClientThread = new ServerThread(mListener.accept());
				System.out.println("Connected");
				handleClientThread.start();
				mListServerThread.add(handleClientThread);
			}
		} catch (IOException e) {
			System.out.println("Crash");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
}
	
	
	public class ServerThread extends Thread{
		private Socket mSocket;
		private BufferedReader mIs;
		private BufferedWriter mOut;
		private boolean mRunning;
		private String mId;
		private String mName;
		
		public ServerThread(Socket socket){
			this.mSocket = socket;	
		}
		
		public void closeServerThread(){
			this.mRunning = false;
		}
		
		public void releaseServerThread(){
			try{
				mIs.close();
				mOut.close();
				mSocket.close();
				outMaps.clear();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		
		public void sendMessageForId(String id, String message){
			try{
				if(outMaps.containsKey(id)){
					BufferedWriter writer = outMaps.get(id);
					writer.write(mId + ", " + mName +", " +  message);
					writer.newLine();
					writer.flush();
				}
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try{
				mIs = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
				mOut = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
				
				this.mId = mIs.readLine();
				this.mName = mIs.readLine();
				outMaps.put(mId, mOut);
				
				System.out.println(mId);
				System.out.println(mName);
				mRunning = true;
				while(mRunning){
					String value;
					if ((value = mIs.readLine()) != null && !value.equals("")){
						//get message from client
						System.out.println(mId + ", " + mName + ", " + value);
						//send message to all client in connection
						synchronized (outMaps) {
							for(String id: outMaps.keySet()){
								if(!mId.equals(id)){
									sendMessageForId(id, value);
								}
							}
						}
					}
				}
			} catch(IOException e){
				closeServerThread();
			}
		}
	}
	

	
}
