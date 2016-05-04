package pro.outcome.test;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import pro.outcome.rest.JsonObject;
import static pro.outcome.util.Shortcuts.*;


public class TestJson {

	// TYPE:
	public static void main(String[] args) throws Exception {
		TestJson test = new TestJson();
		test.testJSON();
		println("All done.");
	}

	// INSTANCE:
	@Test
	public void testJSON() throws Exception {
		StringBuilder text = new StringBuilder();
		text.append("{");
		text.append("  \"message\": {");
		text.append("    \"to\": [");
		text.append("      {");
		text.append("        \"email\": \"person1@drinkacola.com\",");
		text.append("        \"name\": \"Person 1\",");
		text.append("        \"type\": \"to\",");
		text.append("      },");
		text.append("      {");
		text.append("        \"email\": \"person2@drinkacola.com\",");
		text.append("        \"name\": \"Person 2\",");
		text.append("        \"type\": \"cc\",");
		text.append("      },");
		text.append("      {");
		text.append("        \"email\": \"person3@drinkacola.com\",");
		text.append("        \"name\": \"Person 3\",");
		text.append("        \"type\": \"bcc\",");
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
		assertEquals("to", recipient1.getString("type"));
		JsonObject recipient2 = to.get(1);
		assertEquals("person2@drinkacola.com", recipient2.getString("email"));
		assertEquals("Person 2", recipient2.getString("name"));
		assertEquals("cc", recipient2.getString("type"));
		JsonObject recipient3 = to.get(2);
		assertEquals("person3@drinkacola.com", recipient3.getString("email"));
		assertEquals("Person 3", recipient3.getString("name"));
		assertEquals("bcc", recipient3.getString("type"));
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
