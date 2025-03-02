package ch.elexis.base.ch.ebanking.print;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import ch.elexis.core.model.IUser;
import ch.elexis.core.services.holder.ContextServiceHolder;

@XmlRootElement
public class ESRFileJournalLetter {

	@XmlElement
	private String user;
	@XmlElement
	private String date;

	@XmlElementWrapper(name = "records")
	@XmlElement(name = "record")
	private List<ESRFileJournalRecord> records;

	public ESRFileJournalLetter() {
		// TODO Auto-generated constructor stub
	}

	public ESRFileJournalLetter(List<?> records) {
		Optional<IUser> activeUser = ContextServiceHolder.get().getActiveUser();
		if (activeUser.isPresent()) {
			user = activeUser.get().getLabel();
		} else {
			user = "?";
		}
		date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

		this.records = records.stream().map(ol -> new ESRFileJournalRecord((List<?>) Arrays.asList((Object[]) ol)))
				.collect(Collectors.toList());

	}
}
