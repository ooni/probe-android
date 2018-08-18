package org.openobservatory.ooniprobe.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.ooniprobe.common.Application;

import java.io.Serializable;

@Table(database = Application.class)
public class Url extends BaseModel implements Serializable {
	@PrimaryKey(autoincrement = true) public int id;
	@Column public String url;
	@Column public String category_code;
	@Column public String country_code;

	public Url() {
	}

	public Url(String url) {
		this.url = url;
	}
}
