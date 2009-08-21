package goocalsync;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import de.bea.domingo.DDatabase;
import de.bea.domingo.DDocument;
import de.bea.domingo.DItem;
import de.bea.domingo.DView;
import de.bea.domingo.DViewEntry;
import de.bea.domingo.service.NotesServiceRuntimeException;
import de.bea.domingo.util.GregorianDateTime;

public class NotesCalendarDAO implements BaseDAO {

	private static DView calView = null;
	private static DDocument workDoc = null;
	private List<BaseDoc> calDoc = new ArrayList<BaseDoc>();

	public void delete(String unid) {

		// try{
		DDocument doc = Factory.getMailDatabase().getDocumentByUNID(unid);
		if (doc != null) {
			doc.remove(true);
			workDoc = null;
		}
		// }
		// catch (DNotesException e){
		// e.printStackTrace() ;
		// GooCalUtil.logStackTrace(e);
		// System.exit(-1);
		// }

	}

	public BaseDoc getFirstEntry() {

		// try {
		DDatabase mailDb = Factory.getMailDatabase();
		if (calView == null) {
			calView = mailDb.getView(Constants.NOTES_CALENDAR_VIEW);
		}

		// Convert syncStartDate(xs:date) to MM/DD/YYY to create DateTime
		// object.
		// International intl = Factory.getInternational();
		// String syncSt = "";
		// syncSt = syncStartDate.substring(5, 7) + intl.getDateSep() +
		// syncStartDate.substring(8, 10) + intl.getDateSep() +
		// syncStartDate.substring(0, 4);
		// DateTime sdt = Factory.getNotesSession().createDateTime(syncSt);
		Calendar sdt = Factory.getSettings().getSyncStartDate();
		Calendar edt = Factory.getSettings().getSyncEndDate();
		Iterator<DViewEntry> viewEntrys = (Iterator<DViewEntry>) calView.getAllEntriesByKey(sdt, edt, false);

		while (viewEntrys.hasNext()) {
			DViewEntry viewEntry = viewEntrys.next();
			DDocument workDoc = viewEntry.getDocument();

			if (GooCalUtil.convNull(workDoc.getItemValueString("Form")).equals("Appointment")) {
				// GregorianDateTimeRange dt = (GregorianDateTimeRange)
				// workDoc.getItemValue("StartDateTime").get(0);
				// if (dt.getFrom().before(sdt)) {
				// continue;
				// }

				// If this is a conflict document, skip to next document.
				if (workDoc.hasItem("$Conflict")) {
					continue;
				}

				BaseDoc convDoc = convDoc(workDoc);
				if (convDoc != null) {
					calDoc.add(convDoc);
				}
			}
		}

		return calDoc.isEmpty() ? null : calDoc.remove(0);
		// } catch (NotesException e) {
		// e.printStackTrace();
		// GooCalUtil.logStackTrace(e);
		// System.exit(-1);
		// }
		// return null;
	}

	public BaseDoc getNextEntry() {
		return calDoc.isEmpty() ? null : calDoc.remove(0);
	}

	public String insert(BaseDoc bd) {

		// try {
		DDocument doc = Factory.getMailDatabase().createDocument();
		doc.appendItemValue("Form", "Appointment");
		doc.appendItemValue("Subject", bd.getTitle());
		doc.appendItemValue("Body", bd.getContent());
		doc.appendItemValue("Location", bd.getLocation());
		DItem item = (DItem) doc.appendItemValue("AppointmentType");
		item.setValueString(Integer.toString(bd.getApptype()));
		item.setSummary(true);
		doc.appendItemValue("ExcludeFromView", "D");

		Calendar sdt = bd.getStartDateTime();
		Calendar edt = bd.getEndDateTime();
		switch (bd.getApptype()) {
		case Constants.ALL_DAY_EVENT:
			doc.appendItemValue("StartDateTime", sdt);
			edt.add(Calendar.DAY_OF_YEAR, -1);
			doc.appendItemValue("EndDateTime", edt);

			// In case that All_DAY_EVENT has duration, add all date to
			// CalendarDateTime to show up on Notes calendar.
			Vector resultdt = new Vector();
			while (edt.after(sdt)) {
				resultdt.addElement(new GregorianDateTime(sdt.getTime()));
				sdt.add(Calendar.DAY_OF_YEAR, 1);
			}
			doc.appendItemValue("CalendarDateTime", resultdt);

			doc.appendItemValue("orgTable", "P0");
			break;
		case Constants.REMINDER:
			doc.appendItemValue("CalendarDateTime", sdt);
			doc.appendItemValue("StartDateTime", sdt);
			doc.appendItemValue("EndDateTime", edt);
			doc.appendItemValue("orgTable", "C0");
			break;
		case Constants.NORMAL_EVENT:
			doc.appendItemValue("CalendarDateTime", sdt);
			doc.appendItemValue("StartDateTime", sdt);
			doc.appendItemValue("EndDateTime", edt);
			doc.appendItemValue("orgTable", "C0");
			break;
		default:
			doc.appendItemValue("CalendarDateTime", sdt);
			doc.appendItemValue("StartDateTime", sdt);
			doc.appendItemValue("EndDateTime", edt);
			break;
		}

		doc.computeWithForm(true);
		doc.save(true, true);
		return doc.getUniversalID();
		// } catch (NotesException e) {
		// e.printStackTrace();
		// GooCalUtil.logStackTrace(e);
		// return null;
		// }

	}

	public BaseDoc select(String unid) {

		try {
			workDoc = Factory.getMailDatabase().getDocumentByUNID(unid);
			if (workDoc != null) {
				// setWorkDocNext();
				BaseDoc bd = convDoc(workDoc);
				return bd;
			} else {
				return null;
			}
		} catch (NotesServiceRuntimeException e) {
			// if (e.id == NotesError.NOTES_ERR_BAD_UNID) {
			// return null;
			// }
			// e.printStackTrace();
			// GooCalUtil.logStackTrace(e);
		}
		return null;

	}

	public void update(BaseDoc bd) {

		// try {
		workDoc.replaceItemValue("Subject", bd.getTitle());
		workDoc.replaceItemValue("Body", bd.getContent());
		workDoc.replaceItemValue("Location", bd.getLocation());
		workDoc.replaceItemValue("StartDateTime", new GregorianDateTime(bd.getStartDateTime()));
		workDoc.replaceItemValue("StartDate", new GregorianDateTime(bd.getStartDateTime()));
		workDoc.replaceItemValue("StartTime", new GregorianDateTime(bd.getStartDateTime()));
		workDoc.replaceItemValue("EndDateTime", new GregorianDateTime(bd.getEndDateTime()));
		workDoc.replaceItemValue("EndDate", new GregorianDateTime(bd.getEndDateTime()));
		workDoc.replaceItemValue("EndTime", new GregorianDateTime(bd.getEndDateTime()));
		workDoc.replaceItemValue("AppointmentType", Integer.toString(bd.getApptype()));

		switch (bd.getApptype()) {
		case Constants.ALL_DAY_EVENT:
			GregorianDateTime sdt = new GregorianDateTime(bd.getStartDateTime());
			GregorianDateTime edt = new GregorianDateTime(bd.getEndDateTime());
			edt.add(Calendar.DAY_OF_YEAR, -1);

			// In case that All_DAY_EVENT has duration, add all date to
			// CalendarDateTime to show up on Notes calendar.
			Vector resultdt = new Vector();
			while (edt.after(sdt)) {
				resultdt.addElement((new GregorianDateTime(sdt.getTime())));
				sdt.add(Calendar.DAY_OF_YEAR, 1);
			}
			workDoc.replaceItemValue("CalendarDateTime", resultdt);

			workDoc.replaceItemValue("EndDateTime", edt);
			workDoc.replaceItemValue("EndDate", edt);
			workDoc.replaceItemValue("EndTime", edt);

			workDoc.replaceItemValue("orgTable", "P0");
			break;
		case Constants.REMINDER:
			workDoc.replaceItemValue("CalendarDateTime", new GregorianDateTime(bd.getStartDateTime()));
			workDoc.replaceItemValue("orgTable", "C0");
			break;
		case Constants.NORMAL_EVENT:
			workDoc.replaceItemValue("CalendarDateTime", new GregorianDateTime(bd.getStartDateTime()));
			workDoc.replaceItemValue("orgTable", "C0");
			break;
		default:
			workDoc.replaceItemValue("CalendarDateTime", new GregorianDateTime(bd.getStartDateTime()));
			break;
		}
		workDoc.computeWithForm(true);
		workDoc.save(true, true);

		// } catch (NotesException e) {
		// e.printStackTrace();
		// GooCalUtil.logStackTrace(e);
		// System.exit(-1);
		// }

	}

	private BaseDoc convDoc(DDocument doc) {

		BaseDoc bd = new BaseDoc();
		try {
			bd.setTitle(GooCalUtil.convNull(doc.getItemValueString("Subject")));
			bd.setContent(GooCalUtil.convNull(doc.getItemValueString("Body")));

			bd.setId(doc.getUniversalID());
			bd.setRefId(IDTable.getGoogleUID(doc.getUniversalID()));
			bd.setLocation(GooCalUtil.convNull(doc.getItemValueString("Location")));
			bd.setLastUpdated(doc.getLastModified());
			bd.setApptype(Integer.parseInt(GooCalUtil.convNull(doc.getItemValueString("AppointmentType"))));

			// if (doc.getItemValueString("Repeats").equals("1")) { <-- In some
			// environment, it ends up with NullPonterException.
			if (doc.hasItem("Repeats")) {
				bd.setRecur(analyzeRecurrence(doc.getItemValue("CalendarDateTime")));

			} else {
				BaseRecur recur = new BaseRecur();
				recur.setFrequency(Constants.FREQ_NONE);
				recur.setInterval(0);
				recur.setUntil(null);
				recur.setRdate(null);
				bd.setRecur(recur);
			}
			List startDateTime = doc.getItemValue("StartDateTime");
			GregorianDateTime sdt = (GregorianDateTime) startDateTime.get(0);
			List endDateTime = doc.getItemValue("EndDateTime");
			GregorianDateTime edt = (GregorianDateTime) endDateTime.get(0);
			bd.setStartDateTime(sdt);
			bd.setEndDateTime(edt);
		} catch (NotesServiceRuntimeException e) {
			System.out.println(bd.toString());
			System.out.println(e.toString());
			System.out.println("------------------------------------------------");
			return null;
		}
		return bd;
	}

	private BaseRecur analyzeRecurrence(List vdt) {

		BaseRecur recur = new BaseRecur();
		recur.setFrequency(Constants.FREQ_OTHER);
		recur.setInterval(0);
		recur.setUntil(null);

		if (vdt.size() == 1) {
			recur.setFrequency(Constants.FREQ_NONE);
			return recur;
		}

		int i;
		for (i = 1; vdt.size() > i; i++) {

			GregorianDateTime dta = (GregorianDateTime) vdt.get(i - 1);
			GregorianDateTime dtb = (GregorianDateTime) vdt.get(i);
			boolean supportedFlag = false;

			// check whether daily or not
			dta.add(Calendar.DAY_OF_YEAR, 1);
			if (dta.equals(dtb)) {
				supportedFlag = true;
				if (i == 1) {
					recur.setFrequency(Constants.FREQ_DAILY);
					recur.setInterval(1);
				} else {
					if (recur.getFrequency() != Constants.FREQ_DAILY) {
						recur.setFrequency(Constants.FREQ_OTHER);
						break;
					}
				}
			}
			dta.add(Calendar.DAY_OF_YEAR, -1);

			// check whether weekly or not
			dta.add(Calendar.DAY_OF_YEAR, 7);

			if (dta.equals(dtb)) {
				supportedFlag = true;
				if (i == 1) {
					recur.setFrequency(Constants.FREQ_WEEKLY);
					recur.setInterval(1);
				} else {
					if (recur.getFrequency() != Constants.FREQ_WEEKLY) {
						recur.setFrequency(Constants.FREQ_OTHER);
						break;
					}
				}
			}
			dta.add(Calendar.DAY_OF_YEAR, -7);

			// check whether bi-weekly or not
			dta.add(Calendar.DAY_OF_YEAR, 14);
			if (dta.equals(dtb)) {
				supportedFlag = true;
				if (i == 1) {
					recur.setFrequency(Constants.FREQ_WEEKLY);
					recur.setInterval(2);
				} else {
					if (recur.getFrequency() != Constants.FREQ_WEEKLY && recur.getInterval() != 2) {
						recur.setFrequency(Constants.FREQ_OTHER);
						break;
					}
				}
			}
			dta.add(Calendar.DAY_OF_YEAR, -14);

			// check whether monthly or not
			dta.add(Calendar.MONTH, 1);
			if (dta.equals(dtb)) {
				supportedFlag = true;
				if (i == 1) {
					recur.setFrequency(Constants.FREQ_MONTHLY);
					recur.setInterval(1);
				} else {
					if (recur.getFrequency() != Constants.FREQ_MONTHLY) {
						recur.setFrequency(Constants.FREQ_OTHER);
						break;
					}
				}
			}
			dta.add(Calendar.MONTH, -1);

			// check whether yearly or not
			dta.add(Calendar.YEAR, 1);
			if (dta.equals(dtb)) {
				supportedFlag = true;
				if (i == 1) {
					recur.setFrequency(Constants.FREQ_YEARLY);
					recur.setInterval(1);
				} else {
					if (recur.getFrequency() != Constants.FREQ_YEARLY) {
						recur.setFrequency(Constants.FREQ_OTHER);
						break;
					}
				}
			}
			dta.add(Calendar.YEAR, -1);

			// check whether recurrence type is supported.
			if (supportedFlag == false) {
				recur.setFrequency(Constants.FREQ_OTHER);
				break;
			}
		}

		if (recur.getFrequency() == Constants.FREQ_OTHER) {
			Calendar rdatelist[] = new Calendar[vdt.size()];
			for (i = 0; i < vdt.size(); i++) {
				rdatelist[i] = (GregorianDateTime) vdt.get(i);
			}
			recur.setRdate(rdatelist);
		} else {
			// set GMT time to UNTIL in accordance with iCAl recurrence
			// specification.
			GregorianDateTime zdt = (GregorianDateTime) vdt.get(i - 1);
			// zdt.adjustHour(zdt.getTimeZone());
			recur.setUntil(zdt);
		}

		return recur;
	}

	@Override
	public String getDirection() {
		return Constants.NOTES_TO_GOOGLE;
	}
}
