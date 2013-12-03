package org.o2i2b2.objects;

import java.util.Date;

import org.o2i2b2.i2b2.schema.Patient;

public class PatientDb extends Patient {

	private Date birthdateDb;
	private Date deathdateDb;
	
	public Date getBirthdateDb() {
		return birthdateDb;
	}
	public void setBirthdateDb(Date birthdateDb) {
		this.birthdateDb = birthdateDb;
	}
	public Date getDeathdateDb() {
		return deathdateDb;
	}
	public void setDeathdateDb(Date deathdateDb) {
		this.deathdateDb = deathdateDb;
	}
	
}
