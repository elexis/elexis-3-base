package ch.elexis.base.ch.ebanking.print;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import ch.elexis.base.ch.ebanking.model.IEsrRecord;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.esr.ESRCode;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.rgw.tools.Money;

@XmlRootElement
public class ESRJournalLetter {

	@XmlElement
	private String file;
	@XmlElement
	private String user;
	@XmlElement
	private String date;

	@XmlElementWrapper(name = "booked")
	@XmlElement(name = "record")
	private List<ESRJournalRecord> bookedRecords;
	@XmlElement
	private String sumBooked;

	@XmlElementWrapper(name = "notbooked")
	@XmlElement(name = "record")
	private List<ESRJournalRecord> notBookedRecords;
	@XmlElement
	private String sumNotBooked;

	public ESRJournalLetter() {
		// TODO Auto-generated constructor stub
	}

	public ESRJournalLetter(File esrFile, List<IEsrRecord> records) {
		file = esrFile.getAbsolutePath();
		Optional<IUser> activeUser = ContextServiceHolder.get().getActiveUser();
		if (activeUser.isPresent()) {
			user = activeUser.get().getLabel();
		} else {
			user = "?";
		}
		date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

		bookedRecords = records.stream().filter(r -> r.hasBookedDate() && r.getCode() != ESRCode.Summenrecord)
				.map(r -> new ESRJournalRecord(r))
				.collect(Collectors.toList());
		Money sumBookedMoney = new Money();
		records.stream().filter(r -> r.hasBookedDate() && r.getCode() != ESRCode.Summenrecord)
				.forEach(r -> sumBookedMoney.addMoney(r.getAmount()));
		sumBooked = sumBookedMoney.getAmountAsString();

		notBookedRecords = records.stream().filter(r -> !r.hasBookedDate() && r.getCode() != ESRCode.Summenrecord)
				.map(r -> new ESRJournalRecord(r))
				.collect(Collectors.toList());
		Money sumNotBookedMoney = new Money();
		records.stream().filter(r -> !r.hasBookedDate() && r.getCode() != ESRCode.Summenrecord)
				.forEach(r -> sumNotBookedMoney.addMoney(r.getAmount()));
		sumNotBooked = sumNotBookedMoney.getAmountAsString();
	}
}
