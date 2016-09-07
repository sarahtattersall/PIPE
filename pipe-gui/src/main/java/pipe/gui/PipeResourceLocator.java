package pipe.gui;

import java.io.File;
import java.net.URL;

public class PipeResourceLocator {

	private static final String EXAMPLE_PATH = "/extras/examples";
	private static final String IMAGE_PATH = "/images";
	
	public URL getImage(String image) {
		return getResource(IMAGE_PATH,image,".png");
	}
	public URL getExample(String example) {
		return getResource(EXAMPLE_PATH,example,".xml");
	}

	private URL getResource(String path, String resource, String suffix) {
		URL url = this.getClass().getResource(path+File.separator+resource+suffix);
		if (url != null) return url; 
		else {
			url = getResourcePath(path); 
			String urlPath = (url != null) ? url.getPath() : ""; 
			throw new RuntimeException("Could not find "+resource+suffix+" in "+urlPath); 
		}
	}
	private URL getResourcePath(String path) {
		return this.getClass().getResource(path);
	}
	public URL getExamplePath() {
		return getResourcePath(EXAMPLE_PATH);
	}
	public URL getImagePath() {
		return getResourcePath(IMAGE_PATH);
	}


}
