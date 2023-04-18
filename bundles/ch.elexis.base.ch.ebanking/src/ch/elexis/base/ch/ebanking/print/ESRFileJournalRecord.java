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
	private String importDate;
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
		importDate = (String) valuesList.get(1);
		entries = Long.toString((Long) valuesList.get(2));
		amount = new Money(((Double) valuesList.get(3)).intValue()).getAmountAsString();
		file = (String) valuesList.get(4);
	}
}
