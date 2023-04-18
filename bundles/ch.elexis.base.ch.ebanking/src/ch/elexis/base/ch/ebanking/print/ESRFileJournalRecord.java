package ch.elexis.base.ch.ebanking.print;

import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;

@XmlRootElement(name = "record")
public class ESRFileJournalRecord {

	@XmlElement
	private String date;
	@XmlElement
	private String entries;
	@XmlElement
	private String amount;
	@XmlElement
	private String file;

	public ESRFileJournalRecord() {
		// TODO Auto-generated constructor stub
	}

	public ESRFileJournalRecord(List<?> valuesList) {
		TimeTool dateTool = new TimeTool((String) valuesList.get(0));
		date = dateTool.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		entries = Long.toString((Long) valuesList.get(1));
		amount = new Money(((Double) valuesList.get(2)).intValue()).getAmountAsString();
		file = (String) valuesList.get(3);
	}
}
