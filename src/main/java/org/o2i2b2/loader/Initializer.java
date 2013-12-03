package org.o2i2b2.loader;

import java.text.MessageFormat;

import org.o2i2b2.dao.objects.MetadataTableAccess;
import org.o2i2b2.i2b2.schema.O2I2B2;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

public class Initializer implements Runnable {

	private JdbcTemplate metadataJdbcTemplate;
	private JdbcTemplate starSchemaJdbcTemplate;
	private O2I2B2 o2i2b2;
	private String sourceId;
	private MetadataTableAccess metadataTableAccess;
	
	private String message = "";
	
	private boolean complete = false;
	
	private boolean interuptMe;


	private int step = 0;
	
	private boolean skipClear;
	
	@Override
	public void run() {
		
		if (!skipClear) runClear();
		if (interuptMe) return;
		runInit();
		if (interuptMe) return;
		
		complete = true;
	}

	private void runClear() {
		step++;
		message = MessageFormat.format(App.messages.getString("app.initializer.status.clear.metadata"), sourceId);
		ImportUtils.executeUpdate(metadataJdbcTemplate, String.format("DROP INDEX %s_fullname_idx", metadataTableAccess.getTableName()), null);
		ImportUtils.executeUpdate(metadataJdbcTemplate, String.format("DROP TABLE %s", metadataTableAccess.getTableName()), null);
		if (interuptMe) return;
		step++;
		message = MessageFormat.format(App.messages.getString("app.initializer.status.clear.data"), o2i2b2.getOrgName(), "schemes");
		ImportUtils.executeUpdate(metadataJdbcTemplate, "DELETE FROM schemes WHERE c_name = ?", new Object[]{metadataTableAccess.getTableName() + ":"});
		if (interuptMe) return;
		step++;
		message = MessageFormat.format(App.messages.getString("app.initializer.status.clear.data"), o2i2b2.getOrgName(), "table_access");
		ImportUtils.executeUpdate(metadataJdbcTemplate, "DELETE FROM table_access WHERE c_table_name = ?", new Object[]{metadataTableAccess.getTableName()});
		if (interuptMe) return;
		step++;
		message = MessageFormat.format(App.messages.getString("app.initializer.status.clear.data"), o2i2b2.getOrgName(), "concept_dimension");
		ImportUtils.executeUpdate(starSchemaJdbcTemplate, "DELETE FROM concept_dimension WHERE sourcesystem_cd = ?", new Object[]{sourceId});
		if (interuptMe) return;
		step++;
		message = MessageFormat.format(App.messages.getString("app.initializer.status.clear.data"), o2i2b2.getOrgName(), "provider_dimension");
		ImportUtils.executeUpdate(starSchemaJdbcTemplate, "DELETE FROM provider_dimension WHERE sourcesystem_cd = ?", new Object[]{sourceId});
		if (interuptMe) return;
		step++;
		message = MessageFormat.format(App.messages.getString("app.initializer.status.clear.data"), o2i2b2.getOrgName(), "patient_dimension");
		ImportUtils.executeUpdate(starSchemaJdbcTemplate, "DELETE FROM patient_dimension WHERE sourcesystem_cd = ?", new Object[]{sourceId});
		if (interuptMe) return;
		step++;
		message = MessageFormat.format(App.messages.getString("app.initializer.status.clear.data"), o2i2b2.getOrgName(), "visit_dimension");
		ImportUtils.executeUpdate(starSchemaJdbcTemplate, "DELETE FROM visit_dimension WHERE sourcesystem_cd = ?", new Object[]{sourceId});
		if (interuptMe) return;
		step++;
		message = MessageFormat.format(App.messages.getString("app.initializer.status.clear.data"), o2i2b2.getOrgName(), "patient_mapping");
		ImportUtils.executeUpdate(starSchemaJdbcTemplate, "DELETE FROM patient_mapping WHERE sourcesystem_cd = ?", new Object[]{sourceId});
		if (interuptMe) return;
		step++;
		message = MessageFormat.format(App.messages.getString("app.initializer.status.clear.data"), o2i2b2.getOrgName(), "encounter_mapping");
		ImportUtils.executeUpdate(starSchemaJdbcTemplate, "DELETE FROM encounter_mapping WHERE sourcesystem_cd = ?", new Object[]{sourceId});
		if (interuptMe) return;
		step++;
		message = MessageFormat.format(App.messages.getString("app.initializer.status.clear.data"), o2i2b2.getOrgName(), "observation_fact");
		ImportUtils.executeUpdate(starSchemaJdbcTemplate, "DELETE FROM observation_fact WHERE sourcesystem_cd = ?", new Object[]{sourceId});
		step++;
	}
	
	private void runInit() {
		message = App.messages.getString("app.initializer.status.clear.init");

		if (!schemesRecordExists()) ImportUtils.executeUpdate(metadataJdbcTemplate, "INSERT INTO SCHEMES (C_KEY, C_NAME, C_DESCRIPTION) VALUES (?, ?, ?)", new Object[]{metadataTableAccess.getTableName() + ":", metadataTableAccess.getTableName(), o2i2b2.getOrgName()});
		if (!tableAccessRecordExists()) ImportUtils.executeUpdate(metadataJdbcTemplate, "INSERT INTO TABLE_ACCESS (C_TABLE_CD, C_TABLE_NAME, C_PROTECTED_ACCESS, C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_DIMTABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, C_ENTRY_DATE, C_CHANGE_DATE, C_STATUS_CD, VALUETYPE_CD) VALUES (?, ?, 'N', 0, ?, ?, 'N', 'CA', NULL, NULL, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, NULL, NULL, NULL, NULL)", new Object[]{metadataTableAccess.getTableCode(), metadataTableAccess.getTableName(), metadataTableAccess.getPath(), metadataTableAccess.getDisplayName(), metadataTableAccess.getPath(), metadataTableAccess.getDisplayName()});
		
		if (!ontologyTableExists()) {
			ImportUtils.executeUpdate(metadataJdbcTemplate, String.format("CREATE TABLE %s (C_HLEVEL NUMBER(22,0) NOT NULL, C_FULLNAME VARCHAR2(700) NOT NULL, C_NAME VARCHAR2(2000) NOT NULL, C_SYNONYM_CD CHAR(1) NOT NULL, C_VISUALATTRIBUTES CHAR(3) NOT NULL, C_TOTALNUM NUMBER(22,0) NULL, C_BASECODE VARCHAR2(50) NULL, C_METADATAXML CLOB NULL, C_FACTTABLECOLUMN VARCHAR2(50) NOT NULL, C_TABLENAME VARCHAR2(50) NOT NULL, C_COLUMNNAME VARCHAR2(50) NOT NULL, C_COLUMNDATATYPE VARCHAR2(50) NOT NULL, C_OPERATOR VARCHAR2(10) NOT NULL, C_DIMCODE VARCHAR2(700) NOT NULL, C_COMMENT CLOB NULL, C_TOOLTIP VARCHAR2(900) NULL, UPDATE_DATE DATE NOT NULL, DOWNLOAD_DATE DATE NULL, IMPORT_DATE DATE NULL, SOURCESYSTEM_CD VARCHAR2(50) NULL, VALUETYPE_CD VARCHAR2(50) NULL)", metadataTableAccess.getTableName()), null);
			ImportUtils.executeUpdate(metadataJdbcTemplate, String.format("CREATE INDEX %s_fullname_idx ON %s (c_fullname)", metadataTableAccess.getTableName(), metadataTableAccess.getTableName()), null);
			ImportUtils.executeUpdate(metadataJdbcTemplate, String.format("INSERT INTO %s (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (1, ?, ?, 'N', 'FA', NULL, NULL, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", metadataTableAccess.getTableName()), new Object[]{metadataTableAccess.getPath(), metadataTableAccess.getDisplayName(), metadataTableAccess.getPath(), metadataTableAccess.getDisplayName(), sourceId});
			ImportUtils.executeUpdate(metadataJdbcTemplate, String.format("INSERT INTO %s (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (2, ?, ?, 'N', 'FA', NULL, NULL, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", metadataTableAccess.getTableName()), new Object[]{metadataTableAccess.getPath() + "Providers\\", "Providers", metadataTableAccess.getPath() + "\\Providers\\", "Providers", sourceId});
		}
		step++;
	}
	
	private boolean ontologyTableExists() {
		try {
			return metadataJdbcTemplate.queryForInt(String.format("SELECT COUNT(*) FROM %s", metadataTableAccess.getTableName())) > -1 ? true : false;
		} catch (EmptyResultDataAccessException e) {
			// safe to ignore this...
		} catch (BadSqlGrammarException e) {
			// safe to ignore this...this is not a logical error; typically this will fail and will tell us to generate the metadataTableAccess.getTableName() table...
			System.out.println(String.format("INFO: %s metadata table does not exist...", metadataTableAccess.getTableName()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean schemesRecordExists() {
		try {
			return metadataJdbcTemplate.queryForInt("SELECT 1 FROM schemes WHERE c_name = ?", new Object[]{metadataTableAccess.getTableName()}) == 1 ? true : false;
		} catch (EmptyResultDataAccessException e) {
			// safe to ignore this...
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean tableAccessRecordExists() {
		try {
			return metadataJdbcTemplate.queryForInt("SELECT 1 FROM table_access WHERE c_table_name = ?", new Object[]{metadataTableAccess.getTableName()}) == 1 ? true : false;
		} catch (EmptyResultDataAccessException e) {
			// safe to ignore this...
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Initializer(JdbcTemplate metadataJdbcTemplate, JdbcTemplate starSchemaJdbcTemplate, O2I2B2 o2i2b2, String sourceId, MetadataTableAccess metadataTableAccess) {
		super();
		this.sourceId = sourceId;
		this.metadataJdbcTemplate = metadataJdbcTemplate;
		this.starSchemaJdbcTemplate = starSchemaJdbcTemplate;
		this.o2i2b2 = o2i2b2;
		this.metadataTableAccess = metadataTableAccess;
	}

	public JdbcTemplate getMetadataJdbcTemplate() {
		return metadataJdbcTemplate;
	}

	public void setMetadataJdbcTemplate(JdbcTemplate metadataJdbcTemplate) {
		this.metadataJdbcTemplate = metadataJdbcTemplate;
	}

	public JdbcTemplate getStarSchemaJdbcTemplate() {
		return starSchemaJdbcTemplate;
	}

	public void setStarSchemaJdbcTemplate(JdbcTemplate starSchemaJdbcTemplate) {
		this.starSchemaJdbcTemplate = starSchemaJdbcTemplate;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setInteruptMe(boolean interuptMe) {
		this.interuptMe = interuptMe;
	}

	public boolean isInteruptMe() {
		return interuptMe;
	}

	public void setO2i2b2(O2I2B2 o2i2b2) {
		this.o2i2b2 = o2i2b2;
	}

	public O2I2B2 getO2i2b2() {
		return o2i2b2;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getStep() {
		return step;
	}

	public void setSkipClear(boolean skipClear) {
		this.skipClear = skipClear;
	}

	public boolean isSkipClear() {
		return skipClear;
	}

	public void setMetadataTableAccess(MetadataTableAccess metadataTableAccess) {
		this.metadataTableAccess = metadataTableAccess;
	}

	public MetadataTableAccess getMetadataTableAccess() {
		return metadataTableAccess;
	}

}
