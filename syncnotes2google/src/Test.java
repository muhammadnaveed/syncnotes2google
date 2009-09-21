import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class Test {

	
	public static void main(String[] args) {
		// Read properties file.
	    Properties properties = new Properties();
	    try {
	        properties.store(new FileOutputStream("filename.properties"), "Just one file.");
//	        properties.load(new FileInputStream("sync.properties"));
	    } catch (IOException e) {
	    }

	}
}
