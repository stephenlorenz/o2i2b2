package org.o2i2b2.loader;

import java.util.HashMap;

import org.o2i2b2.dao.objects.MetadataTableAccess;
import org.o2i2b2.i2b2.schema.O2I2B2;

public final class ImportData {

	private static boolean fatalError = false;
	private static String fatalErrorMessage = "";
	
	String importFilePath;

	String metadataDatabaseType;
	String metadataHost;
	String metadataPort;
	String metadataDatabase;
	String metadataUsername;
	String metadataPassword;

	String starSchemaDatabaseType;
	String starSchemaHost;
	String starSchemaPort;
	String starSchemaDatabase;
	String starSchemaUsername;
	String starSchemaPassword;

	boolean clearI2b2Ind = true;
	MetadataTableAccess metadataTableAccess;

	java.util.Map<String, MetadataTableAccess> metadataTableAccessHashMap = new HashMap<String, MetadataTableAccess>();

	private Parser parser = null;
	private O2I2B2 o2i2b2 = null;

	protected Parser getParser() {
		if (parser == null) parser = new Parser(importFilePath);
		return parser;
	}

	protected O2I2B2 getO2i2b2() {
		if (o2i2b2 == null) o2i2b2 = getParser().getO2i2b2Object();
		return o2i2b2;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("* i2b2 Import Details *\n");
		sb.append(App.messages.getString("app.text.import") + ":\t" + importFilePath + "\n");
		sb.append("Metadata " + App.messages.getString("app.wizard.label.db.type") + ":\t" + metadataDatabaseType + "\n");
		sb.append("Metadata " + App.messages.getString("app.wizard.label.db.hostnameOrIP") + ":\t" + metadataHost + "\n");
		sb.append("Metadata " + App.messages.getString("app.wizard.label.db.port") + ":\t" + metadataPort + "\n");
		sb.append("Metadata " + App.messages.getString("app.wizard.label.db.database") + ":\t" + metadataDatabase + "\n");
		sb.append("Metadata " + App.messages.getString("app.wizard.label.db.username") + ":\t" + metadataUsername + "\n");
		sb.append("Metadata " + App.messages.getString("app.wizard.label.db.password") + ":\t" + metadataPassword + "\n");
		sb.append("Star Schema " + App.messages.getString("app.wizard.label.db.type") + ":\t" + starSchemaDatabaseType + "\n");
		sb.append("Star Schema " + App.messages.getString("app.wizard.label.db.hostnameOrIP") + ":\t" + starSchemaHost + "\n");
		sb.append("Star Schema " + App.messages.getString("app.wizard.label.db.port") + ":\t" + starSchemaPort + "\n");
		sb.append("Star Schema " + App.messages.getString("app.wizard.label.db.database") + ":\t" + starSchemaDatabase + "\n");
		sb.append("Star Schema " + App.messages.getString("app.wizard.label.db.username") + ":\t" + starSchemaUsername + "\n");
		sb.append("Star Schema " + App.messages.getString("app.wizard.label.db.password") + ":\t" + starSchemaPassword + "\n");

		return sb.toString();
	}

	public static void setFatalError(boolean tmpFatalError) {
		fatalError = tmpFatalError;
	}

	public static boolean isFatalError() {
		return fatalError;
	}

	public static void setFatalErrorMessage(String fatalErrorMessage) {
		ImportData.fatalErrorMessage = fatalErrorMessage;
	}

	public static String getFatalErrorMessage() {
		return fatalErrorMessage;
	}

}
