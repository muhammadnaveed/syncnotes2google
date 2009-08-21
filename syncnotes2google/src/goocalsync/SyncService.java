package goocalsync;

public class SyncService {

	public void executeNotesToGoogle() {
		BaseDAO fromDao = new NotesCalendarDAO();
		BaseDAO toDao = new GoogleCalendarDAO();

		Settings mySets = Factory.getSettings();

		Factory.getLog().info("Start Notes to Google synchronization.");

		BaseDoc entry = fromDao.getFirstEntry();
		while (entry != null) {

			String uid = entry.getRefId();
			if (uid == null) {
				if (mySets.getSyncDirection().equals(fromDao.getDirection()) || mySets.getSyncDirection().equals(Constants.BI_DIRECTION)) {

					// Google don't accept too much number of recurrence date.
					if (entry.getRecur() != null) {
						if (entry.getRecur().getRdate() != null) {
							if (entry.getRecur().getRdate().length > 100) {
								Factory.getLog().warn("This type of entry is not supported by GooCalSync : " + entry.getTitle());
								entry = fromDao.getNextEntry();
								continue;
							}
						}
					}
					Factory.getLog().debug("executing insert: " + entry.getTitle());
					insert(toDao, entry);
				}
			} else {
				BaseDoc toEntry = toDao.select(uid);
				if (toEntry == null) {
					if (!mySets.getSyncDirection().equals(fromDao.getDirection())) {
						Factory.getLog().debug("executing delete: " + entry.getTitle());
						delete(fromDao, entry);
					}
				} else {
					if (entry.getLastUpdated().after(mySets.getSyncLastDateTime())) {
						if (mySets.getSyncDirection().equals(Constants.BI_DIRECTION)) {
							// When both of google and notes calendar are updated, notes calendar is
							// put before google.
							// This is GooCalSync rule.
							// So, update google calendar entry without checking if google calendar
							// entry is newer than Notes.
							Factory.getLog().debug("executing update (BI_DIRECTION): " + entry.getTitle());
							update(toDao, entry);
						} else if (mySets.getSyncDirection().equals(fromDao.getDirection())) {
							Factory.getLog().debug("executing update (" + fromDao.getDirection() + "): " + entry.getTitle());
							update(toDao, entry);
						} else {
							// In the case of GOOGLE_TO_NOTES, do nothing.
						}
					}
				}
			}

			entry = fromDao.getNextEntry();
		}

	}

	public void executeGoogleToNotes(BaseDAO fromDao, BaseDAO toDao) {
		// BaseDAO fromDao = new GoogleCalendarDAO();
		// BaseDAO toDao = new NotesCalendarDAO();

		Settings mySets = Factory.getSettings();

		Log l = Factory.getLog();

		Factory.getLog().info("Start " + fromDao.getDirection() + " to " + toDao.getDirection() + " synchronization.");

		BaseDoc entry = fromDao.getFirstEntry();
		while (entry != null) {

			String uid = entry.getRefId();
			if (uid == null) {
				// This if statement is for suppressing warning messages
				// "Recurrence calenar entry is not supported"
				// Without this if statement, warning message would appear on every time to sync.
				if (entry.getLastUpdated().after(mySets.getSyncLastDateTime())) {
					if (mySets.getSyncDirection().equals(fromDao.getDirection()) || mySets.getSyncDirection().equals(Constants.BI_DIRECTION)) {
						// check if entry is recurrence.
						if (entry.getRecur() != null) {
							if (fromDao instanceof GoogleCalendarDAO) {
								l.warn("Insert warning:Recurrence calendar entry is not supported to sync Google to Notes.");
								l.warn("  Title : " + entry.getTitle());
								entry = fromDao.getNextEntry();
								continue;
							}
							// Google don't accept too much number of recurrence date.
							if (entry.getRecur().getRdate() != null) {
								if (entry.getRecur().getRdate().length > 100) {
									Factory.getLog().warn("This type of entry is not supported by GooCalSync : " + entry.getTitle());
									entry = fromDao.getNextEntry();
									continue;
								}
							}
						}
						Factory.getLog().debug("executing insert: " + entry.getTitle());
						insert(toDao, entry);
					}
				}
			} else {
				BaseDoc toEntry = toDao.select(uid);
				if (toEntry == null) {
					if (!mySets.getSyncDirection().equals(Constants.NOTES_TO_GOOGLE)) {
						Factory.getLog().debug("executing delete " + entry.getTitle());
						delete(fromDao, entry);
					}
				} else {
					if (entry.getLastUpdated().after(mySets.getSyncLastDateTime())) {
						if (mySets.getSyncDirection().equals(Constants.BI_DIRECTION)) {
							if (fromDao instanceof GoogleCalendarDAO) {
								// When both of google and notes calendar are updated, notes
								// calendar is put before google.
								// This is GooCalSync rule.
								// So, execute update only when Notes calendar entry would not be
								// updated since last synchronization.

								// check if entry is recurrence.
								if (mySets.getSyncLastDateTime().before(toEntry.getLastUpdated())) {
									entry = fromDao.getNextEntry();
									continue;
								}
								if (entry.getRecur() != null) {
									l.warn("Update warning:Recurrence calendar entry is not supported to sync Google to Notes.");
									l.warn("  Title : " + entry.getTitle());
									entry = fromDao.getNextEntry();
									continue;
								}
								Factory.getLog().debug("executing update (BI_DIRECTION): " + entry.getTitle());
								update(toDao, entry);
							} else {
								Factory.getLog().debug("executing update (BI_DIRECTION): " + entry.getTitle());
								update(toDao, entry);
							}
						} else if (mySets.getSyncDirection().equals(fromDao.getDirection())) {
							// check if entry is recurrence.
							if (fromDao instanceof GoogleCalendarDAO && entry.getRecur() != null) {
								l.warn("Update warning:Recurrence calendar entry is not supported to sync Google to Notes.");
								l.warn("  Title : " + entry.getTitle());
								entry = fromDao.getNextEntry();
								continue;
							}

							Factory.getLog().debug("executing update (" + fromDao.getDirection() + "): " + entry.getTitle());
							update(toDao, entry);

						} else {
							// In the case of NOTES_TO_GOOGLE, do nothing.
						}
					}
				}
			}
			entry = fromDao.getNextEntry();
		}

	}

	private void insert(BaseDAO dao, BaseDoc entry) {
		String insert = dao.insert(entry);
		IDTable.insert(entry.getId(), insert);
	}

	private void delete(BaseDAO dao, BaseDoc entry) {
		dao.delete(entry.getId());
		IDTable.delete(entry.getId());
	}

	private void update(BaseDAO dao, BaseDoc entry) {
		dao.update(entry);
	}
}
