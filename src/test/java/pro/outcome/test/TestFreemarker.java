package pro.outcome.test;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.HashMap;
import org.junit.BeforeClass;
import org.junit.Test;
import freemarker.template.*;


public class TestFreemarker {

	private static final Configuration _cfg = new Configuration();

	@BeforeClass
	public static void setUp() throws Exception {
		//cfg.setDirectoryForTemplateLoading(new File("/where/you/store/templates"));
		_cfg.setClassForTemplateLoading(TestFreemarker.class, ".");
		_cfg.setDefaultEncoding("UTF-8");
		_cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}

	@Test
	public void testConfigCheckVersion() throws IOException, TemplateException {
		Map<String,String> root = new HashMap<>();
		root.put("user", "Jeitoso Tenebroso");
		Template temp = _cfg.getTemplate("template.ftl");
		OutputStreamWriter out = new OutputStreamWriter(System.out);
		temp.process(root, out);
	}
}
