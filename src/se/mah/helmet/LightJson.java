package se.mah.helmet;

/**
 * A very basic and light weight json writer.
 *
 */
public class LightJson {
	StringBuilder json = new StringBuilder();
	private boolean firstValue = true;
	private boolean finalized = false;
	
	private LightJson() {
		json.append('{');
	}
	
	/**
	 * Returns a new instance.
	 * @return new instance
	 */
	public static LightJson newInstance() {
		return new LightJson();
	}
	
	/**
	 * Adds a new key + value pair to the json String.
	 * 
	 * @param key key
	 * @param value value
	 * @return reference to this instance
	 */
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
		json.append(value);
		json.append('"');
		
		return this;
	}
	
	/**
	 * Finalizes and returns the json String. After this call put can no longer
	 * be called.
	 * 
	 * @return the json String
	 */
	public String toJson() {
		finalized = true;
		json.append('}');
		return json.toString();
	}
}
