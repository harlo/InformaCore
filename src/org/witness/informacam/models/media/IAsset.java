package org.witness.informacam.models.media;

import java.io.Serializable;

import org.json.JSONObject;
import org.witness.informacam.InformaCam;
import org.witness.informacam.models.Model;
import org.witness.informacam.utils.Constants.App.Storage;

@SuppressWarnings("serial")
public class IAsset extends Model implements Serializable {
	public String path = null;
	public int source = Storage.Type.IOCIPHER;
	
	public IAsset(String path) {
		super();
		this.path = path;
		
		if(InformaCam.getInstance().user.preferences.encryptOriginals) {
			this.source = Storage.Type.IOCIPHER;
		} else {
			this.source = Storage.Type.FILE_SYSTEM;
		}
	}
	
	public IAsset(String path, int source) {
		super();
		
		this.path = path;
		this.source = source;
	}
	
	public IAsset(IAsset asset) {
		super();
		inflate(asset.asJson());
	}
	
	public IAsset(JSONObject asset) {
		super();
		inflate(asset);
	}
	
	public void get() {
		switch(this.source) {
		case Storage.Type.IOCIPHER:
			break;
		case Storage.Type.FILE_SYSTEM:
			break;
		case Storage.Type.APPLICATION_ASSET:
			break;
		case Storage.Type.CONTENT_RESOLVER:
			break;
		}
	}

}
