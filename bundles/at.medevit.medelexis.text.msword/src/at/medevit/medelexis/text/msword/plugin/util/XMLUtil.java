package at.medevit.medelexis.text.msword.plugin.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtil {
	public static List<Node> getChildElementsByTagName(Node node, String name) {
		ArrayList<Node> result = new ArrayList<Node>();
		if (node != null) {
			NodeList nodes = node.getChildNodes();
			if (nodes != null) {
				for (int i = 0; i < nodes.getLength(); i++) {
					Node subnode = nodes.item(i);
					if (subnode != null && subnode.getNodeType() == Node.ELEMENT_NODE) {
						if (subnode.getNodeName().equalsIgnoreCase(name)) {
							result.add(subnode);
						}
					}
				}
			}
		}
		return result;
	}

	public static List<Node> getChildElements(Node node) {
		ArrayList<Node> result = new ArrayList<Node>();
		if (node != null) {
			NodeList nodes = node.getChildNodes();
			if (nodes != null) {
				for (int i = 0; i < nodes.getLength(); i++) {
					Node subnode = nodes.item(i);
					if (subnode != null && subnode.getNodeType() == Node.ELEMENT_NODE) {
						result.add(subnode);
					}
				}
			}
		}
		return result;
	}

	public static List<Node> getAllChildElementsByTagName(Element root, String name) {
		ArrayList<Node> result = new ArrayList<Node>();
		NodeList nodes = root.getElementsByTagName(name);
		if (nodes != null) {
			for (int i = 0; i < nodes.getLength(); i++) {
				Node resElement = nodes.item(i);
				result.add(resElement);
			}
		}
		return result;
	}

	public static void setAttribute(Element element, String name, String value) {
		element.setAttribute(name, value);
	}

	public static String getAttribute(Element element, String name) {
		return element.getAttribute(name);
	}
}
