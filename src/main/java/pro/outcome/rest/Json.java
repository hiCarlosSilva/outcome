// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.rest;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.util.List;

import pro.outcome.util.Checker;
import pro.outcome.util.KeyValue;
import pro.outcome.util.Strings;

import java.util.ArrayList;
import java.util.Iterator;


public class Json {

	// TYPE:
	private static final String _INDENT = "  ";

	// INSTANCE:
	private final List<KeyValue<Object>> _fields;
	private boolean _added = false;
	
	public Json() {
		_fields = new ArrayList<KeyValue<Object>>();
	}

	public boolean isEmpty() {
		return _fields.isEmpty();
	}

	public Json add(String name, Json value) {
		Checker.checkEmpty(name);
		Checker.checkNull(value);
		value._checkAdded();
		value._added = true;
		_fields.add(new KeyValue<Object>(name, value));
		return value;
	}

	public Json add(String name) {
		return add(name, new Json());
	}

	public Json add(String name, Object value) {
		_fields.add(new KeyValue<Object>(name, value == null ? null : value));
		return this;
	}
	
	public Json print(PrintStream out) {
		return print(new PrintWriter(out));
	}

	public Json print(PrintWriter out) {
		_print(out, 0, _fields.iterator());
		return this;
	}

	private void _print(PrintWriter out, int level, Iterator<KeyValue<Object>> it) {
		out.println("{");
		while(it.hasNext()) {
			KeyValue<Object> p = it.next();
			out.print(Strings.repeat(_INDENT, level+1));
			out.print("\""+p.getName()+"\": ");
			if(p.getValue() == null) {
				out.print("\"\"");
			}
			else {
				if(p.getValue() instanceof Json) {
					Json child = (Json)p.getValue();
					child._print(out, level+1, child._fields.iterator());
				}
				else if(p.getValue() instanceof String) {
					out.print("\"");
					out.print(Strings.escape(p.getValue().toString(), "\""));
					out.print("\"");
				}
				else {
					out.print(p.getValue().toString());
				}
			}
			if(it.hasNext()) {
				out.print(",");
			}
			out.println();
			out.flush();
		}
		if(level > 0) {
			out.print(Strings.repeat(_INDENT, level));
		}
		out.print("}");
		if(level == 0) {
			out.println();
		}
		out.flush();
	}
	
	private void _checkAdded() {
		if(_added) {
			throw new IllegalStateException("this object is already part of another JSON object and cannot be added again");
		}
	}
}
