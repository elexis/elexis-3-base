package at.medevit.ch.artikelstamm.marge;

public class Marge {
	public double startInterval;
	public double endInterval;
	public double addition;
	
	public double getStartInterval(){
		return startInterval;
	}
	
	public void setStartInterval(double startInterval){
		this.startInterval = startInterval;
	}
	
	public double getEndInterval(){
		return endInterval;
	}
	
	public void setEndInterval(double endInterval){
		this.endInterval = endInterval;
	}
	
	public double getAddition(){
		return addition;
	}
	
	public void setAddition(double addition){
		this.addition = addition;
	}
	
	public boolean isValid(){
		boolean valid = true;
		if (Double.isNaN(startInterval) || startInterval == 0)
			valid = false;
		if (Double.isNaN(endInterval) || endInterval == 0)
			valid = false;
		if (Double.isNaN(addition) || addition == 0)
			valid = false;
		return valid;
	}
	
}
