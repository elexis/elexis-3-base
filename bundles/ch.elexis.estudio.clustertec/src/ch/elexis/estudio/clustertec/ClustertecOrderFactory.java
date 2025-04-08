package ch.elexis.estudio.clustertec;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import ch.clustertec.estudio.schemas.order.ObjectFactory;
import ch.clustertec.estudio.schemas.order.Order;
import ch.clustertec.estudio.schemas.order.Product;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IOrderEntry;

public class ClustertecOrderFactory {

	public static Order createOrder(String clientNrPharmapool, String user, String password) {
		Order ret = new ObjectFactory().createOrder();
		ret.setClientNrRose(clientNrPharmapool);
		ret.setUser(user);
		ret.setPassword(password);
		ret.setDeliveryType(1);
		return ret;
	}

	public static Product createProduct(IOrderEntry item) {
		IArticle article = item.getArticle();

		String pharmacode = getPharmaCode(article);
		String eanId = getEan(article);
		String description = article.getName();
		int quantity = item.getAmount();

		Product product = new ObjectFactory().createProduct();
		product.setPharmacode(pharmacode);
		product.setEanId(eanId);
		product.setDescription(description);
		product.setQuantity(quantity);
		product.setPositionType(1);
		return product;
	}

	private static String getEan(IArticle article) {
		String ret = article.getGtin();
		if (StringUtils.isBlank(ret)) {
			Object value = article.getExtInfo("EAN"); //$NON-NLS-1$
			if (value instanceof String && ((String) value).length() > 11) {
				ret = (String) value;
			}
		}
		return ret;
	}

	private static String getPharmaCode(IArticle article) {
		String ret = StringUtils.EMPTY;
		try {
			Method method = article.getClass().getMethod("getPHAR"); //$NON-NLS-1$
			ret = (String) method.invoke(article);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// ignore no pharmacode available ...
		}
		if (StringUtils.isBlank(ret)) {
			Object value = article.getExtInfo("Pharmacode"); //$NON-NLS-1$
			if (value instanceof String && ((String) value).length() == 7) {
				ret = (String) value;
			}
		}
		return StringUtils.leftPad(StringUtils.defaultString(ret), 7, "0");
	}

//	String pharmacode = ArticleUtil.getPharmaCode(artikel);
//	String eanId = ArticleUtil.getEan(artikel);
//	String description = artikel.getName();
//	int quantity = item.getAmount();
//
//	if (StringTool.isNothing(pharmacode) || StringTool.isNothing(eanId) || StringTool.isNothing(description)
//			|| quantity < 1) {
//
//		StringBuffer msg = new StringBuffer();
//		msg.append("Der Artikel " + PersistentObject.checkNull(description) + " (Pharma-Code "
//				+ PersistentObject.checkNull(pharmacode) + ") ist nicht korrekt konfiguriert: ");
//		if (StringTool.isNothing(pharmacode)) {
//			msg.append("Ungültiger Pharmacode. ");
//		}
//		if (StringTool.isNothing(eanId)) {
//			msg.append("Ungültiger EAN-Code. ");
//		}
//		if (quantity < 1) {
//			msg.append("Ungültige Anzahl. ");
//		}
//		msg.append("Bitte korrigieren Sie diese Fehler.");
//
//		SWTHelper.alert("Fehlerhafter Artikel", msg.toString());
//
//		throw new XChangeException("Fehlerhafter Artikel: Pharamcode: " + pharmacode + ", EAN: " + eanId
//				+ ", Name: " + description + ", Anzahl: " + quantity);
//	}
//
//	sb.append("<product" + " pharmacode=\"" + escapeXmlAttribute(pharmacode) + "\"" + " eanId=\""
//			+ escapeXmlAttribute(eanId) + "\"" + " description=\"" + escapeXmlAttribute(description) + "\""
//			+ " quantity=\"" + quantity + "\"" + " positionType=\"1\"" + "/>" + XML_NEWLINE);

}
