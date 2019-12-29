package comun;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LoginUsuario {
	public static void addUser(User user) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(("Trabajo/src/comun/Users.xml")));
			Element rootElement = doc.getDocumentElement();
			Element child = doc.createElement("user");
			child.setAttribute("username", user.getUsername());
			child.setAttribute("password", user.getPassword());
			rootElement.appendChild(child);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("Trabajo/src/comun/Users.xml"));
			transformer.transform(source, result);

		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}

	}

	public static boolean existUser(String userName) {
		boolean exist = false;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(("Trabajo/src/comun/Users.xml")));
			Element rootElement = doc.getDocumentElement();
			NodeList childNodes = rootElement.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) childNodes.item(i);
					if (el.getNodeName().contains("user")) {
						String usernameAux = el.getAttribute("username");
						exist = exist || usernameAux.equalsIgnoreCase(userName);
					}
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return exist;

	}
	public static User getUser(String userName) {
		User user  = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(("Trabajo/src/comun/Users.xml")));
			Element rootElement = doc.getDocumentElement();
			NodeList childNodes = rootElement.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) childNodes.item(i);
					if (el.getNodeName().contains("user") && el.getAttribute("username").equalsIgnoreCase(userName)) {
						user = new User(el.getAttribute("username"), el.getAttribute("password"));
					}
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return user;

	}

	public static List<String> getUsernames() {
		List<String> usernames = new ArrayList<String>();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(("Trabajo/src/comun/Users.xml")));
			Element rootElement = doc.getDocumentElement();
			NodeList childNodes = rootElement.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) childNodes.item(i);
					if (el.getNodeName().contains("user")) {
						String usernameAux = el.getAttribute("username");
						usernames.add(usernameAux);
					}
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return usernames;

	}

}
