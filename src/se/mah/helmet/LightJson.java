package se.mah.helmet;

public class LightJson {
	StringBuilder json = new StringBuilder('{');
	private boolean firstValue = true;
	private boolean finalized = false;
	
	private LightJson() { }
	
	public static LightJson getInstance() {
		return new LightJson();
	}
	
	public LightJson put(CharSequence key, CharSequence value) {
		if (finalized)
			throw new RuntimeException("Finalized.");
		
		if (!firstValue) json.append(',');
		else firstValue = false;
		
		json.append('"');
		json.append(key);
		json.append('"');
		json.append(':');
		json.append('"');
		json.append(key);
		json.append('"');
		
		return this;
	}
	
	public String toJson() {
		finalized = true;
		json.append('}');
		return json.toString();
	}
}
