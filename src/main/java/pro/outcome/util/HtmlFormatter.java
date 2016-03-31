// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.util;


// For Strings
class HtmlFormatter {

	public static String format(String source) {
		Checker.checkNull(source);
		StringBuilder sb = new StringBuilder();
		int nbspCount = 0;
		for(int i=0; i<source.length(); i++) {
			if(source.charAt(i) == '\t') {
				// Substitute TAB by spaces:
				int tabLength = Constants.TAB_SIZE;
				if(nbspCount == 0) {
					sb.append(' ');
					tabLength--;
				}
				sb.append(Strings.repeat("&nbsp;", tabLength));
			}
			else if(source.charAt(i) == '\n') {
				sb.append("<br>");
			}
			else {
				String tag = _find(source.charAt(i));
				if(tag != null) {
					// Adds &nbsp; only for second occurrences of spaces.
					// This is needed, otherwise the browser doesn't
					// break the line when spaces appear
					if(source.charAt(i) == ' ' && nbspCount==0) {
						sb.append(' ');
					}
					else {
						sb.append("&"+tag+";");
					}
				}
				else {
					sb.append(source.charAt(i));
				}
			}
			// count spaces:
			if(source.charAt(i) == ' ' || source.charAt(i) == '\t') {
				nbspCount++;
			}
			else {
				nbspCount = 0;
			}
		}
		return sb.toString();
	}

	private static String _find(char c) {
		for(int i=0; i<_chars.length; i++) {
			if(_chars[i][0].charAt(0) == c) {
				return _chars[i][1];
			}
		}
		return null;
	}

	private static final String[][] _chars = {
		{" ", "nbsp"},
		{"\"", "quot"},
		{"&", "amp"},
		{"<", "lt"},
		{">", "gt"},
		{"�", "euro"},
		{"�", "Aacute"},
		{"�", "aacute"},
		{"�", "Acirc"},
		{"�", "acirc"},
		{"�", "acute"},
		{"�", "aelig"},
		{"�", "AElig"},
		{"�", "Agrave"},
		{"�", "agrave"},
		{"�", "Aring"},
		{"�", "aring"},
		{"�", "Atilde"},
		{"�", "atilde"},
		{"�", "Auml"},
		{"�", "auml"},
		{"�", "brvbar"},
		{"�", "Ccedil"},
		{"�", "ccedil"},
		{"�", "cedil"},
		{"�", "cent"},
		{"^", "circ"},
		{"�", "copy"},
		{"�", "curren"},
		{"�", "deg"},
		{"�", "divide"},
		{"�", "Eacute"},
		{"�", "eacute"},
		{"�", "Ecirc"},
		{"�", "ecirc"},
		{"�", "Egrave"},
		{"�", "egrave"},
		{"�", "ETH"},
		{"�", "eth"},
		{"�", "Euml"},
		{"�", "euml"},
		{"�", "fnof"},
		{"�", "frac12"},
		{"�", "frac14"},
		{"�", "frac34"},
		{"�", "Iacute"},
		{"�", "iacute"},
		{"�", "Icirc"},
		{"�", "icirc"},
		{"�", "Igrave"},
		{"�", "igrave"},
		{"�", "iexcl"},
		{"�", "iquest"},
		{"�", "Iuml"},
		{"�", "iuml"},
		{"�", "laquo"},
		{"�", "macr"},
		{"�", "micro"},
		{"�", "middot"},
		{"�", "not"},
		{"�", "Ntilde"},
		{"�", "ntilde"},
		{"�", "Oacute"},
		{"�", "oacute"},
		{"�", "Ocirc"},
		{"�", "ocirc"},
		{"�", "Ograve"},
		{"�", "ograve"},
		{"�", "OElig"},
		{"�", "oelig"},
		{"�", "ordf"},
		{"�", "ordm"},
		{"�", "oslash"},
		{"�", "oslash"},
		{"�", "Otilde"},
		{"�", "otilde"},
		{"�", "Ouml"},
		{"�", "ouml"},
		{"�", "para"},
		{"�", "plusmn"},
		{"�", "pound"},
		{"�", "raquo"},
		{"�", "reg"},
		{"�", "Scaron"},
		{"�", "scaron"},
		{"�", "sect"},
		{"�", "shy"},
		{"�", "sup1"},
		{"�", "sup2"},
		{"�", "sup3"},
		{"�", "szlig"},
		{"�", "THORN"},
		{"�", "thorn"},
		{"~", "tilde"},
		{"�", "times"},
		{"�", "Uacute"},
		{"�", "uacute"},
		{"�", "Ucirc"},
		{"�", "ucirc"},
		{"�", "Ugrave"},
		{"�", "ugrave"},
		{"�", "uml"},
		{"�", "Uuml"},
		{"�", "uuml"},
		{"�", "Yacute"},
		{"�", "yacute"},
		{"�", "yen"},
		{"�", "Yuml"},
		{"�", "yuml"},
		// {"", "ensp"},
		// {"", "emsp"},
		// {"", "thinsp"},
		// {"", "zwnj"},
		// {"", "zwj"},
		// {"", "lrm"},
		// {"", "rlm"},
		{"�", "ndash"},
		{"�", "mdash"},
		{"�", "lsquo"},
		{"�", "rsquo"},
		{"�", "sbquo"},
		{"�", "ldquo"},
		{"�", "rdquo"},
		{"�", "bdquo"},
		{"�", "lsaquo"},
		{"�", "rsaquo"},
		{"�", "dagger"},
		{"�", "Dagger"},
		{"�", "permil"},
		{"�", "bull"},
		{"�", "hellip"},
		//TODO get these chars, and backup the file
		//{"?", "Prime"},
		//{"?", "prime"},
		//{"?", "oline"},
		//{"?", "frasl"},
		// {"", "weierp"},
		// {"", "image"},
		// {"", "real"},
		{"�", "trade"},
		// {"", "alefsym"},
		// {"", "larr"},
		// {"", "uarr"},
		// {"", "rarr"},
		// {"", "darr"},
		// {"", "harr"},
		// {"", "carr"},
		// {"", "lArr"},
		// {"", "uArr"},
		// {"", "rArr"},
		// {"", "dArr"},
		// {"", "hArr"},
		// {"", "forall"},
		//{"?", "part"},
		// {"", "exist"},
		// {"", "empty"},
		// {"", "nabla"},
		// {"", "isin"},
		// {"", "notin"},
		// {"", "ni"},
		//{"?", "prod"},
		//{"?", "sum"},
		//{"?", "minus"},
		//{"", "lowast"},
		//{"?", "radic"},
		//{"", "prop"},
		//{"?", "infin"},
		// {"", "ang"},
		// {"", "and"},
		// {"", "or"},
		// {"", "cap"},
		// {"", "cup"},
		//{"?", "int"},
		// {"", "there4"},
		// {"", "sim"},
		// {"", "cong"},
		//{"?", "asymp"},
		//{"?", "ne"},
		// {"", "equiv"},
		// {"?", "le"},
		// {"?", "ge"},
		// {"", "sub"},
		// {"", "sup"},
		// {"", "nsub"},
		// {"", "sube"},
		// {"", "supe"},
		// {"", "oplus"},
		// {"", "otimes"},
		// {"", "perp"},
		// {"", "sdot"},
		// {"", "lceil"},
		// {"", "rceil"},
		// {"", "lfloor"},
		// {"", "rfloor"},
		// {"", "lang"},
		// {"", "rang"},
		//{"?", "loz"},
		// {"", "spades"},
		// {"", "clubs"},
		// {"", "hearts"},
		// {"", "diams"},
		//{"?", "Alpha"},
		//{"?", "alpha"},
		//{"?", "Beta"},
		//{"?", "beta"},
		//{"?", "Gamma"},
		//{"?", "gamma"},
		//{"?", "Delta"},
		//{"?", "delta"},
		//{"?", "Epsilon"},
		//{"?", "epsilon"},
		//{"?", "Zeta"},
		//{"?", "zeta"},
		//{"?", "Eta"},
		//{"?", "eta"},
		//{"?", "Theta"},
		//{"?", "theta"},
		// {"", "thetasym"},
		//{"?", "Iota"},
		//{"?", "iota"},
		//{"?", "Kappa"},
		//{"?", "kappa"},
		//{"?", "Lambda"},
		//{"?", "lambda"},
		//{"?", "Mu"},
		//{"?", "mu"},
		//{"?", "Nu"},
		//{"?", "nu"},
		//{"?", "Xi"},
		//{"?", "xi"},
		//{"?", "Omicron"},
		//{"?", "omicron"},
		//{"?", "Pi"},
		//{"?", "pi"},
		// {"", "piv"},
		//{"?", "Rho"},
		//{"?", "rho"},
		//{"?", "Sigma"},
		//{"?", "sigma"},
		//{"?", "sigmaf"},
		//{"?", "Tau"},
		//{"?", "tau"},
		//{"?", "Upsilon"},
		//{"?", "upsilon"},
		//{"", "upsih"},
		//{"?", "Phi"},
		//{"?", "phi"},
		//{"?", "Chi"},
		//{"?", "chi"},
		//{"?", "Psi"},
		//{"?", "psi"},
		//{"?", "Omega"},
		//{"?", "omega"}
	};

}
