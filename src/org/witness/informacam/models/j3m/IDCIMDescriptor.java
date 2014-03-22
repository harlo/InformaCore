package org.witness.informacam.models.j3m;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.witness.informacam.InformaCam;
import org.witness.informacam.intake.Intake;
import org.witness.informacam.models.Model;
import org.witness.informacam.models.media.IAsset;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.App.Storage;
import org.witness.informacam.utils.Constants.Logger;
import org.witness.informacam.utils.Constants.Models;
import org.witness.informacam.utils.Constants.Models.IMedia.MimeType;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class IDCIMDescriptor extends Model {	
	public List<IDCIMEntry> shortDescription = new ArrayList<IDCIMEntry>();
	public List<IDCIMEntry> intakeList = new ArrayList<IDCIMEntry>();

	private long startTime = 0L;
	private long timeOffset = 0L;
	private String parentId = null;
	private InformaCam informaCam = InformaCam.getInstance();

	private final static String LOG = Storage.LOG;

	public IDCIMDescriptor(String parentId) {
		startTime = System.currentTimeMillis()/1000;
		this.parentId = parentId;
	}

	public IDCIMSerializable asDescriptor() {
		return new IDCIMSerializable(shortDescription);
	}

	public void addEntry(Uri authority, boolean isThumbnail) {
		final IDCIMEntry entry = new IDCIMEntry();
		entry.authority = authority.toString();
		
		String sortBy = "date_added DESC";

		if(isThumbnail) {
			sortBy = null;
			entry.mediaType = Models.IDCIMEntry.THUMBNAIL;
		}

		Cursor cursor = InformaCam.getInstance().getContentResolver().query(authority, null, null, null, sortBy);

		if(cursor != null && cursor.moveToFirst()) {
			entry.fileAsset = new IAsset(cursor.getString(cursor.getColumnIndexOrThrow(MediaColumns.DATA)), Storage.Type.FILE_SYSTEM);

			if(!isThumbnail) {
				entry.timeCaptured = cursor.getLong(cursor.getColumnIndexOrThrow(MediaColumns.DATE_ADDED));
				if(entry.timeCaptured < startTime) {
					Logger.d(LOG, "this media occured too early to count");
					cursor.close();

					return;
				}

				entry.mediaType = cursor.getString(cursor.getColumnIndexOrThrow(MediaColumns.MIME_TYPE));

				if(entry.mediaType.equals(MimeType.VIDEO_3GPP)) {
					entry.mediaType = MimeType.VIDEO;
				}
			}

			String pattern = "^([a-zA-Z0-9]+)([a-zA-Z0-9_]*)\\.(jpg|mp4){1}$";
			
			entry.id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaColumns._ID));
			if(!isThumbnail) {
				IDCIMEntry clone = new IDCIMEntry(entry);

				if(!shortDescription.contains(clone)) {
					shortDescription.add(clone);
				}
			}
			cursor.close();
			
			entry.exif = new IExif();
		
			if (informaCam.informaService != null && informaCam.informaService.getCurrentLocation() != null)
			{
				entry.exif.location = informaCam.informaService.getCurrentLocation().geoCoordinates;
			}

			intakeList.add(entry);
			
			
		}
	}

	public void startSession() {
		InformaCam informaCam = InformaCam.getInstance();
		timeOffset = informaCam.informaService.getTimeOffset();
		
		Logger.d(LOG, "starting dcim session");
	}

	public void stopSession() {
		// start up intake queue
		if(!intakeList.isEmpty()) {
			InformaCam informaCam = InformaCam.getInstance();

			Intent intakeIntent = new Intent(informaCam, Intake.class);

			intakeIntent.putExtra(Codes.Extras.RETURNED_MEDIA, new IDCIMSerializable(intakeList));
			
			List<String> cacheFiles = informaCam.informaService.getCacheFiles();
			intakeIntent.putExtra(Codes.Extras.INFORMA_CACHE, cacheFiles.toArray(new String[cacheFiles.size()]));
			
			intakeIntent.putExtra(Codes.Extras.TIME_OFFSET, timeOffset);
			if(parentId != null) {
				intakeIntent.putExtra(Codes.Extras.MEDIA_PARENT, parentId);
			}

			informaCam.startService(intakeIntent);
			Logger.d(LOG, "saved a dcim descriptor");
		} else {
			Logger.d(LOG, "there were no entries.");
		}
	}

	public static class IDCIMSerializable extends Model implements Serializable {
		private static final long serialVersionUID = 3688700992408456583L;
		
		public List<IDCIMEntry> dcimList;

		public IDCIMSerializable(List<IDCIMEntry> dcimList) {
			super();
			this.dcimList = dcimList;
		}
	}
}
