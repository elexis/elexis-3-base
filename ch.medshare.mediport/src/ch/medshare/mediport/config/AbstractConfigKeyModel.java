package ch.medshare.mediport.config;

public abstract class AbstractConfigKeyModel implements ConfigKeys {
	
	private boolean hasChanged = false;
	
	protected void propertyChanged(String newValue, String oldValue){
		if (newValue != null) {
			if (!newValue.equals(oldValue)) {
				hasChanged = true;
			}
		} else if (oldValue == null) {
			hasChanged = true;
		}
	}
	
	protected boolean isEmpty(String value){
		return value == null || value.length() == 0;
	}
	
	public void setChanged(boolean changed){
		this.hasChanged = changed;
	}
	
	public boolean hasChanged(){
		return this.hasChanged;
	}
}
