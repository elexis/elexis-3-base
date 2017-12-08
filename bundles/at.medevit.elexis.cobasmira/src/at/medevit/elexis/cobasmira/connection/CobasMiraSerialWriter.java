package at.medevit.elexis.cobasmira.connection;

import java.io.IOException;
import java.io.OutputStream;

public class CobasMiraSerialWriter implements Runnable {
	private OutputStream out;
	
	public CobasMiraSerialWriter(OutputStream out){
		this.out = out;
	}
	
	public void run(){
		try {
			System.out.println("Starting Writer");
			int c = 0;
			while ((c = System.in.read()) > -1) {
				this.out.write(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
