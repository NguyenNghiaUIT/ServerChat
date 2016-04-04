

public class Program {

	public static void main(String[] args) {
		
		
		ServerManager manager = new ServerManager();
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				manager.startServer();
				
			}
		});
		
		thread.start();
		
	
		

	}

}
