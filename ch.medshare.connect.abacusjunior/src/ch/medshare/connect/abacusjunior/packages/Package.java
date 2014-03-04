package ch.medshare.connect.abacusjunior.packages;

public abstract class Package {
	
	char _id;
	String _message;
	boolean _ack;
	char _command;
	
	public Package(char id, String message){
		_id = id;
		_message = message;
		_ack = !_message.substring(1).contains("\001");
		_command = ' ';
	}
	
	public char getId(){
		return _id;
	}
	
	public String getMessage(){
		return _message;
	}
	
	public String getResponse(){
		return _ack ? String.format("\006%c%c", _command, _id) : "\021";
	}
	
	public boolean getAck(){
		return _ack;
	}
}