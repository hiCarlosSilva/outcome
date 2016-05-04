package pro.outcome.test;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;
import pro.outcome.util.Strings;
import pro.outcome.util.HttpClient;
import static pro.outcome.util.Shortcuts.*;


// TODO make this work with Jetty, so that we do not have dependencies with the Connector API server 
public class TestHttp {

	// TYPE:
	public static final String SERVER = "http://localhost:8080";

	public static void main(String[] args) throws Exception {
		TestHttp test = new TestHttp();
		test.testHttpGet();
		test.testHttpPost();
		println("All done.");
	}

	// INSTANCE:
	@Test
	public void testHttpGet() throws IOException {
		String url = SERVER+"/config/check-version/v1?version=1";
		print("Testing GET {}... ", url);
		String s = HttpClient.get(url);
		s = Strings.removeWhitespace(s);
		assertEquals("{\"content\":{\"supported\":true},\"header\":{\"status\":0}}", s);
		println("done.");
	}

	@Test
	public void testHttpPost() throws IOException {
		String url = SERVER+"/sessions/sign-in/v1";
		print("Testing POST {}... ", url);
		Map<String,String> params = new HashMap<>();
		params.put("username", "lisa.connector@gmail.com");
		params.put("password", "IntersectionLabs");
		String s = HttpClient.post(url, params);
		s = Strings.removeWhitespace(s);
		assertEquals("{\"header\":{\"status\":0}}", s);
		println("done.");
	}
}
