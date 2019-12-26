package comun;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class LoginUsuario {
	public static void addUser(User user) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(("src/Users.xml")));
			Element rootElement = doc.getDocumentElement();
			System.out.println(rootElement.getNodeName());
			Element child = doc.createElement("user");
			child.setAttribute("username", user.getUsername());
			child.setAttribute("password", user.getPassword());
			rootElement.appendChild(child);
			
			TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer transformer = tf.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        DOMSource source = new DOMSource(doc);
	        StreamResult result = new StreamResult(new File("src/Users.xml"));
	        transformer.transform(source, result);


		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}

	}

}
