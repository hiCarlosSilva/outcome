package pro.outcome.data;
import pro.outcome.util.Checker;


public class Flag {
	
	private final String _shortcut;
	private final String _description;
	
	// For Flags:
	Flag(String shortcut, String description) {
		Checker.checkEmpty(shortcut);
		Checker.checkEmpty(description);
		_shortcut = shortcut;
		_description = description;
	}
	
	public String getShortcut() {
		return _shortcut;
	}

	public String getDescription() {
		return _description;
	}
}
