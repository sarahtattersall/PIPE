package pipe.utilities.transformers;

import java.io.ByteArrayInputStream;

import javax.xml.transform.stream.StreamSource;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLTestCase;
import org.w3c.dom.Document;

public class PNMLTransformerTest extends XMLTestCase
{
	// This class uses XMLUnit.  syntax is junit 3: test methods must begin with "test"
	//
	// TestXmlFileConverter converts XML files to strings for use in these tests; SIMPLE_NET is an example. 
	
	private static final String SLASH = System.getProperty("file.separator");
	public static final String SIMPLE_NET_WITHOUT_TOKEN = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><pnml><net id=\"Net-One\" type=\"P/T net\"><place id=\"P0\"><graphics><position x=\"135.0\" y=\"120.0\"/></graphics><name><value>P0</value><graphics/></name><initialMarking><value>2</value><graphics/></initialMarking></place><place id=\"P1\"><graphics><position x=\"375.0\" y=\"120.0\"/></graphics><name><value>P1</value><graphics/></name><initialMarking><value>0</value><graphics/></initialMarking></place><transition id=\"T0\"><graphics><position x=\"255.0\" y=\"120.0\"/></graphics><name><value>T0</value><graphics/></name><orientation><value>0</value></orientation><rate><value>1.0</value></rate><timed><value>true</value></timed></transition><arc id=\"P0 to T0\" source=\"P0\" target=\"T0\"><graphics/><inscription><value>1</value><graphics/></inscription><arcpath id=\"000\" x=\"160\" y=\"131\" curvePoint=\"false\"/><arcpath id=\"001\" x=\"260\" y=\"131\" curvePoint=\"false\"/></arc><arc id=\"T0 to P1\" source=\"T0\" target=\"P1\"><graphics/><inscription><value>1</value><graphics/></inscription><arcpath id=\"000\" x=\"270\" y=\"131\" curvePoint=\"false\"/><arcpath id=\"001\" x=\"371\" y=\"131\" curvePoint=\"false\"/></arc></net></pnml>";
	public static final String SIMPLE_NET_WITH_TOKEN = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><pnml><net id=\"Net-One\" type=\"P/T net\"><token id=\"Default\" enabled=\"true\" red=\"0\" green=\"0\" blue=\"0\"/><place id=\"P0\"><graphics><position x=\"135.0\" y=\"120.0\"/></graphics><name><value>P0</value><graphics/></name><initialMarking><value>2</value><graphics/></initialMarking></place><place id=\"P1\"><graphics><position x=\"375.0\" y=\"120.0\"/></graphics><name><value>P1</value><graphics/></name><initialMarking><value>0</value><graphics/></initialMarking></place><transition id=\"T0\"><graphics><position x=\"255.0\" y=\"120.0\"/></graphics><name><value>T0</value><graphics/></name><orientation><value>0</value></orientation><rate><value>1.0</value></rate><timed><value>true</value></timed></transition><arc id=\"P0 to T0\" source=\"P0\" target=\"T0\"><graphics/><inscription><value>1</value><graphics/></inscription><arcpath id=\"000\" x=\"160\" y=\"131\" curvePoint=\"false\"/><arcpath id=\"001\" x=\"260\" y=\"131\" curvePoint=\"false\"/></arc><arc id=\"T0 to P1\" source=\"T0\" target=\"P1\"><graphics/><inscription><value>1</value><graphics/></inscription><arcpath id=\"000\" x=\"270\" y=\"131\" curvePoint=\"false\"/><arcpath id=\"001\" x=\"371\" y=\"131\" curvePoint=\"false\"/></arc></net></pnml>";
	public static final String ONE_TOKEN_TWO_INITIAL_MARKINGS = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><pnml><net id=\"Net-One\" type=\"P/T net\"><token id=\"Default\" enabled=\"true\" red=\"0\" green=\"0\" blue=\"0\"/><place id=\"P0\"><graphics><position x=\"135.0\" y=\"120.0\"/></graphics><name><value>P0</value><graphics/></name><initialMarking><value>Default,0,Red,0</value><graphics/></initialMarking></place></net></pnml>";;
	public static final String TWO_TOKENS_TWO_INITIAL_MARKINGS_ONE_NONZERO = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><pnml><net id=\"Net-One\" type=\"P/T net\"><token id=\"Default\" enabled=\"true\" red=\"0\" green=\"0\" blue=\"0\"/><token id=\"Red\" enabled=\"true\" red=\"255\" green=\"0\" blue=\"0\"/><place id=\"P0\"><graphics><position x=\"135.0\" y=\"120.0\"/></graphics><name><value>P0</value><graphics/></name><initialMarking><value>Default,0,Red,1</value><graphics/></initialMarking></place></net></pnml>";;
	private static final String SIMPLE_NET_FILE = "test"+SLASH+"xml"+SLASH+"simpleNet.xml";
	private PNMLTransformer transformer;

//	@Test  Junit 3 will be used to run this class because it uses XMLUnit; @Test annotations ignored.
	public void testDocumentsBuiltFromSameFileAreEqual() throws Exception
	{
		transformer = new PNMLTransformer();
		Document doc = transformer.transformPNML(SIMPLE_NET_FILE);
		Document doc2 = transformer.transformPNML(SIMPLE_NET_FILE);
		assertXMLEqual("similar; nodes may appear in different sequence",doc, doc2); 
		Diff diff = new Diff(doc, doc2); 
		assertXMLIdentical("identical",diff, true); 
	}
	public void testDocumentsBuiltFromFileAndFromStreamAreEqual() throws Exception
	{
		transformer = new PNMLTransformer();
		Document doc = transformer.transformPNML(SIMPLE_NET_FILE);
		Document doc2 = transformer.transformPNMLStreamSource(getNetAsStreamSource(SIMPLE_NET_WITHOUT_TOKEN));
		Diff diff = new Diff(doc, doc2); 
		assertXMLIdentical("identical",diff, true); 
	}
	public static	 StreamSource getNetAsStreamSource(String net)
	{
		StreamSource simpleSource = new StreamSource(new ByteArrayInputStream(net.getBytes()));
		return simpleSource;
	}
		
}
