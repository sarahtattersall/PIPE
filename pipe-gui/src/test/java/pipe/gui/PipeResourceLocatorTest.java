package pipe.gui;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PipeResourceLocatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void verifyGetsImage() {
		PipeResourceLocator locator = new PipeResourceLocator(); 
		URL url = locator.getImage("About");
		assertTrue(url.getPath().endsWith("/images/About.png")); 
		url = locator.getImage("Add token");
		assertTrue("embedded blanks ok",url.getPath().endsWith("/images/Add%20token.png")); 
	}
	@Test
	public void verifyGetsExample() {
		PipeResourceLocator locator = new PipeResourceLocator(); 
		URL url = locator.getExample("ClassicGSPN");
		assertTrue(url.getPath().endsWith("/extras/examples/ClassicGSPN.xml")); 
		url = locator.getExample("Courier Protocol");
		assertTrue(url.getPath().endsWith("/extras/examples/Courier%20Protocol.xml")); 
	}
	@Test
	public void verifyGetsPaths() {
		PipeResourceLocator locator = new PipeResourceLocator(); 
		URL url = locator.getExamplePath();
		assertTrue(url.getPath().endsWith("/extras/examples")); 
		url = locator.getImagePath();
		assertTrue(url.getPath().endsWith("/images")); 
	}
	@Test
	public void verifyImageNotFoundTellsWhereWeLooked() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Could not find nonexistent.png in ");
		PipeResourceLocator locator = new PipeResourceLocator(); 
		locator.getImage("nonexistent");
	}
	@Test
	public void verifyExampleNotFoundTellsWhereWeLooked() {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage("Could not find nonexistent example.xml in ");
		PipeResourceLocator locator = new PipeResourceLocator(); 
		locator.getExample("nonexistent example");
	}
}
