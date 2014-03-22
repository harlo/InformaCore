package org.witness.informacam.models.j3m;

import java.io.Serializable;

import org.witness.informacam.InformaCam;
import org.witness.informacam.models.Model;
import org.witness.informacam.models.media.IAsset;
import org.witness.informacam.utils.Constants.App.Storage.Type;

@SuppressWarnings("serial")
public class IDCIMEntry extends Model implements Serializable {
	public String uri = null;
	
	public String name = null;
	public String authority = null;
	public String originalHash = null;
	public String bitmapHash = null;
	public String mediaType = null;
	
	public IAsset fileAsset = null;
	public IAsset thumbnail = null;
	public IAsset preview = null;
	public IAsset list_view = null;
	
	public IExif exif = null;

	public long size = 0L;
	public long timeCaptured = 0L;
	public String timezone = null;
	public long id = 0L;
	
	public IDCIMEntry() {
		super();
	}
	
	public IDCIMEntry(Object dcimEntry) {
		super();
		inflate(((Model) dcimEntry).asJson());
	}
	
	public boolean isAvailable() {
		boolean isAvailable = false;
		
		do {
			isAvailable = InformaCam.getInstance().ioService.isAvailable(fileAsset.path, fileAsset.source);
		} while(!isAvailable);
		
		return isAvailable;
	}
}