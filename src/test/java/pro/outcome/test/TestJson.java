package pro.outcome.test;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import pro.outcome.rest.JsonObject;
import static pro.outcome.util.Shortcuts.*;


public class TestJson {

	// TYPE:
	public enum ToType {TO, CC, BCC};
	
	public static void main(String[] args) throws Exception {
		TestJson test = new TestJson();
		test.testJsonText();
		test.testJsonInterface();
		test.testAnotherJsonText();
		println("All done.");
	}

	// INSTANCE:
	@Test
	public void testJsonText() throws Exception {
		StringBuilder text = new StringBuilder();
		text.append("{");
		text.append("  \"message\": {");
		text.append("    \"to\": [");
		text.append("      {");
		text.append("        \"email\": \"person1@drinkacola.com\",");
		text.append("        \"name\": \"Person 1\",");
		text.append(x("        \"type\": \"{}\",", ToType.TO));
		text.append("      },");
		text.append("      {");
		text.append("        \"email\": \"person2@drinkacola.com\",");
		text.append("        \"name\": \"Person 2\",");
		text.append(x("        \"type\": \"{}\",", ToType.CC));
		text.append("      },");
		text.append("      {");
		text.append("        \"email\": \"person3@drinkacola.com\",");
		text.append("        \"name\": \"Person 3\",");
		text.append(x("        \"type\": \"{}\",", ToType.BCC));
		text.append("      }");
		text.append("    ],");
		text.append("    \"subject\": \"An important message\",");
		text.append("    \"html\": \"You forgot your <b>lights on</b>!\",");
		text.append("    \"from\": \"albert@x.no\"");
		text.append("  }");
		text.append("}");
		print("Parsing JSON text... ");
		JsonObject json = JsonObject.parse(text.toString());
		println("done.");
		_checkContentsOf(json);
	}

	@Test
	public void testJsonInterface() throws Exception {
		print("Creating JSON object... ");
		JsonObject json = new JsonObject();
		JsonObject message = json.addChild("message");
		List<JsonObject> to = message.addChildListOf("to", JsonObject.class);
		JsonObject person1 = new JsonObject();
		person1.put("email", "person1@drinkacola.com");
		person1.put("name", "Person 1");
		person1.put("type", ToType.TO);
		to.add(person1);
		JsonObject person2 = new JsonObject();
		person2.put("email", "person2@drinkacola.com");
		person2.put("name", "Person 2");
		person2.put("type", ToType.CC);
		to.add(person2);
		JsonObject person3 = new JsonObject();
		person3.put("email", "person3@drinkacola.com");
		person3.put("name", "Person 3");
		person3.put("type", ToType.BCC);
		to.add(person3);
		message.put("subject", "An important message");
		message.put("html", "You forgot your <b>lights on</b>!");
		message.put("from", "albert@x.no");
		println("done.");
		_checkContentsOf(json);
	}
	
	public void testAnotherJsonText() {
		String text = "{\"content\":{\"lastName\":\"Smith\",\"fullName\":\"Lisa Smith\",\"firstName\":\"Lisa\",\"fields\":[{\"value\":\"lisa.connector@gmail.com\",\"label\":\"email\",\"type\":\"EMAIl\"}]},\"header\":{\"status\":0}}";
		print("Parsing JSON text... ");
		JsonObject.parse(text.toString());
		println("done.");
	}

	private void _checkContentsOf(JsonObject json) {
		print("Checking contents of JSON object... ");
		JsonObject message = json.getJsonObject("message");
		assertEquals("albert@x.no", message.getString("from"));
		assertEquals("An important message", message.getString("subject"));
		assertEquals("You forgot your <b>lights on</b>!", message.getString("html"));		
		List<JsonObject> to = message.getListOf("to", JsonObject.class);
		assertEquals(3, to.size());
		JsonObject recipient1 = to.get(0);
		assertEquals("person1@drinkacola.com", recipient1.getString("email"));
		assertEquals("Person 1", recipient1.getString("name"));
		assertEquals("TO", recipient1.getString("type"));
		JsonObject recipient2 = to.get(1);
		assertEquals("person2@drinkacola.com", recipient2.getString("email"));
		assertEquals("Person 2", recipient2.getString("name"));
		assertEquals("CC", recipient2.getString("type"));
		JsonObject recipient3 = to.get(2);
		assertEquals("person3@drinkacola.com", recipient3.getString("email"));
		assertEquals("Person 3", recipient3.getString("name"));
		assertEquals("BCC", recipient3.getString("type"));
		println("done.");
		print("Modifying JSON object... ");
		message.remove("from");
		message.remove("subject");
		message.remove("html");
		to.clear();
		assertEquals("{\"message\":{\"to\":[]}}", json.toString());
		println("done.");
	}
}
