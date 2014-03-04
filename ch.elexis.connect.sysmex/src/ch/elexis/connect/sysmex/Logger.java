package ch.elexis.connect.sysmex;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Logger {
	public final static byte STX = 0x02;
	public final static byte ETX = 0x03;
	PrintStream _log;
	
	public Logger(){
		_log = System.out;
	}
	
	public Logger(String filename) throws FileNotFoundException{
		_log = new PrintStream(new FileOutputStream(filename, true));
	}
	
	public Logger(boolean enable){
		if (enable) {
			_log = System.out;
		} else {
			_log = new PrintStream(new DummyPrintStream());
		}
	}
	
	public void logSTX(){
		Character ch = new Character((char) STX);
		_log.print(ch);
	}
	
	public void logETX(){
		Character ch = new Character((char) ETX);
		_log.println(ch);
	}
	
	public void log(String s){
		_log.print(s);
	}
	
	class DummyPrintStream extends OutputStream {
		@Override
		public void write(int b) throws IOException{
			// Do nothing
		}
	}
}
