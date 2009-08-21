package com.googlecode.syncnotes2google;

import java.io.IOException;

import com.googlecode.syncnotes2google.dao.BaseDAO;
import com.googlecode.syncnotes2google.dao.GoogleCalendarDAO;
import com.googlecode.syncnotes2google.dao.NotesCalendarDAO;

import lotus.domino.NotesException;

public class GooCalSync {

	public static void main(String[] args) throws IOException {

		Factory.getLog().info("GooCalSync v0.919 has started.");

		try {
			// Execute synchronization
			SyncService ss = new SyncService();
			BaseDAO googleDao = new GoogleCalendarDAO();
			BaseDAO notesDao = new NotesCalendarDAO();

			// ss.executeNotesToGoogle();
			ss.executeGoogleToNotes(notesDao, googleDao);
			ss.executeGoogleToNotes(googleDao, notesDao);

			// Update Last Sync Execution Date & Time
			Settings mySets = Factory.getSettings();
			mySets.setSyncLastDateTime(GooCalUtil.getNow());
			mySets.saveSetDoc();
		} finally {
			IDTable.save();
		}

		// Recycle Notes related objects.
		// If don't do this, Notes initialization failure would occur after several executions.
		// Factory.freeNotesObject();

		Factory.getLog().info("GooCalSync has ended.");

	}

}
