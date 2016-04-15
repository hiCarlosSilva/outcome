package pro.outcome.data;
import java.util.Map;
import java.util.HashMap;
import pro.outcome.util.Checker;
import pro.outcome.util.Strings;


public final class Flag {
	
	// TYPE:
	private final static Map<Class<?>,Map<String,Flag>> _map = new HashMap<>();

	public static <E extends Entity<?>> Flag createFor(Class<E> c, String shortcut, String description) {
		Checker.checkNull(c);
		Checker.checkEmpty(shortcut);
		Checker.checkEmpty(description);
		Map<String,Flag> flagsForClass = _map.get(c);
		if(flagsForClass == null) {
			flagsForClass = new HashMap<>();
			_map.put(c, flagsForClass);
		}
		Flag flag = flagsForClass.get(shortcut);
		if(flag != null) {
			throw new IllegalArgumentException(Strings.expand("flag for class {} with shorcut '{}' already exists", c.getSimpleName(), shortcut));
		}
		flag = new Flag(c, shortcut, description);
		flagsForClass.put(shortcut, flag);
		return flag;
	}
	
	// INSTANCE:
	private final Class<?> _class;
	private final String _shortcut;
	private final String _description;
	
	private Flag(Class<?> c, String shortcut, String description) {
		_class = c;
		_shortcut = shortcut;
		_description = description;
	}
	
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + _class.hashCode();
		hash = 31 * hash + _shortcut.hashCode();
		return hash;
	}

	public String toString() {
		return Strings.expand("Flag[{}={}]", _shortcut, _description);
	}
}
