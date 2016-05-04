package pro.outcome.test;
//import org.json.JSONObject;
//import org.json.JSONArray;
import org.junit.Test;
import static pro.outcome.util.Shortcuts.*;


public class TestJSON {

	// TYPE:
	public static void main(String[] args) throws Exception {
		TestJSON test = new TestJSON();
		test.testJSON();
		println("done.");
	}

	// INSTANCE:
	// TODO
	@Test
	public void testJSON() throws Exception {
		/*
		String s = "{\"message\":{\"to\":[{\"email\":\"listening@connector.im\",\"name\":\"Listening\",\"type\":\"to\"}],\"subject\":\"Feature request\",\"html\":\"\\\"param\\\"=\\\"val\\\"&a=b\",\"from\":\"a@x.no\"},\"key\":\"3pB3LPg9lI-4hVY6J74Ntw\"}";
		JSONObject json = new JSONObject(s);
		JSONObject message = json.getJSONObject("message");
		JSONArray to = message.getJSONArray("to");
		println(to.get(0).toString());
		println(to.getJSONObject(0).getString("email"));
		println(message.getString("subject"));
		println(message.getString("html"));
	*/
	}
}
