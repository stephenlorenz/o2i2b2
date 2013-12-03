package org.o2i2b2.openmrs.export;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.o2i2b2.dao.ConceptNumeric;
import org.o2i2b2.objects.ConceptSource;
import org.o2i2b2.utils.LogHelper;
import org.o2i2b2.xsd.Analysis;
import org.o2i2b2.xsd.ConvertingUnits;
import org.o2i2b2.xsd.Counts;
import org.o2i2b2.xsd.Enums;
import org.o2i2b2.xsd.ExcludingUnits;
import org.o2i2b2.xsd.MultiplyingFactor;
import org.o2i2b2.xsd.UnitValues;
import org.o2i2b2.xsd.Units;
import org.o2i2b2.xsd.ValueMetadata;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class ConceptMigrator {

	private static JdbcTemplate sourceJdbcTemplate;
	private static JdbcTemplate metadataJdbcTemplate;
	private static JdbcTemplate starSchemaJdbcTemplate;

	private static final String ORG_CODE = "PIH";
	private static final String ORG_NAME = "Partners In Health";

	// TODO: Somehow find a way to retrieve these from the database instead of static references...
	private static final int OPENMRS_CONCEPT_CLASS_QUESTION = 7;
	private static final int OPENMRS_CONCEPT_DATATYPE_CODED = 2;
	
	private transient Logger logger = Logger.getLogger(this.getClass().getName());

	private int tmpCounter = 0;
	private HashMap<Integer, Integer> patientMapping = new HashMap<Integer, Integer>();
	private HashSet<String> conceptIdList = new HashSet<String>(); 
	
	private static void init() {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
		sourceJdbcTemplate = (JdbcTemplate) appContext.getBean("sourceJdbcTemplate");
		metadataJdbcTemplate = (JdbcTemplate) appContext.getBean("metadataJdbcTemplate");
		starSchemaJdbcTemplate = (JdbcTemplate) appContext.getBean("starSchemaJdbcTemplate");
	}

	public static void main(String[] args) {
		init();
		new ConceptMigrator();
	}

	public ConceptMigrator() {
		refreshAll();
	}

	private void refreshAll() {
		deleteSome();
		createSome();
//		deleteAll();
//		createAll();
		logger.info("Loading Concept Classes....");
		logger.info("Loaded " + loadConceptClasses() + " Concept Classes.");
		logger.info("Loading Simple Concepts....");
		logger.info("Loaded " + loadSimpleConcepts() + " Simple Concepts.");
		logger.info("Loading Coded Concepts....");
		logger.info("Loaded " + loadCodedConcepts() + " Coded Concepts.");
//		logger.info("Loading Providers....");
//		logger.info("Loaded " + loadProviders() + " Providers.");
//		logger.info("Loading Patients....");
//		logger.info("Loaded " + loadPatients() + " Patients.");
//		logger.info("Loading Visits....");
//		logger.info("Loaded " + loadVisits() + " Visits.");
//		logger.info("Loading Observation Facts....");
//		logger.info("Loaded " + loadObservationFacts() + " Observation Facts.");

//		System.out.println(generateMetadataXml(5497, "CD4 COUNT"));
	}
	
	@SuppressWarnings({ "unchecked" })
	private int loadConceptClasses() {
		tmpCounter = 0;
		try {
			sourceJdbcTemplate.query("SELECT name, description FROM concept_class",
					new Object[]{},
					new RowMapper() {
				@Override
				public Object mapRow(ResultSet resultSet, int rowNum)
				throws SQLException {
					tmpCounter++;
					String conceptClass = resultSet.getString(1);
					String conceptDescription = resultSet.getString(2);
					Object[] params = new Object[]{"\\" + ORG_CODE + "\\" + conceptClass + "\\", conceptClass, "\\" + ORG_CODE + "\\" + conceptClass + "\\", conceptClass + ": " + conceptDescription, ORG_CODE};
					executeUpdate(metadataJdbcTemplate, "INSERT INTO PIH (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (2, ?, ?, 'N', 'FA', NULL, NULL, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
					return null;
				}
			});
			
		} catch(Exception e) {
			logger.debug(new LogHelper(e));
		}
		return tmpCounter;
	}
	@SuppressWarnings({ "unchecked" })
	private int loadSimpleConcepts() {
		tmpCounter = 0;
		try {
			sourceJdbcTemplate.query("SELECT DISTINCT concept.concept_id AS id, concept_name.name AS name, concept_description.description AS description, concept_class.name AS className, concept_datatype.name AS dataType, concept_name_type AS nameType FROM concept_name, concept, concept_description, concept_datatype, concept_class WHERE concept.concept_id = concept_name.concept_id AND concept.concept_id = concept_description.concept_id AND concept.datatype_id = concept_datatype_id AND concept.class_id = concept_class.concept_class_id AND concept_description.description <> '' AND concept_description.locale = 'en' AND concept_name.locale = 'en' AND concept_description_id = (SELECT MAX(concept_description_id) FROM concept_description d2 WHERE concept.concept_id = d2.concept_id AND d2.description <> '' AND d2.locale = 'en') AND concept_name.name <> '' AND concept.class_id <> ? AND concept.datatype_id <> ? ORDER BY 1",
					new Object[]{OPENMRS_CONCEPT_CLASS_QUESTION, OPENMRS_CONCEPT_DATATYPE_CODED},
					new RowMapper() {
				@Override
				public Object mapRow(ResultSet resultSet, int rowNum)
				throws SQLException {
					tmpCounter++;
					int conceptId = resultSet.getInt(1);
					String conceptName = resultSet.getString(2);
					String description = resultSet.getString(3);
					String className = resultSet.getString(4);
					String dataType = resultSet.getString(5);
					String nameType = resultSet.getString(6);
					
					String metadataXml = "";
					
					if (dataType.equalsIgnoreCase("Numeric")) metadataXml = generateMetadataXml(conceptId, conceptName);

					ConceptSource conceptSource = getConceptSource(conceptId);
					
					String conceptCode = generateConceptCode(conceptId, -1);
					String conceptPath = "\\" + ORG_CODE + "\\" + className + "\\" + WordUtils.capitalizeFully(conceptName) + " (" + conceptCode + ")" + "\\";
					String conceptTooltip = conceptName + ": " + description;

					if (nameType != null && nameType.equalsIgnoreCase("FULLY_SPECIFIED")) {
						Object[] params = new Object[]{conceptPath, conceptCode, conceptTooltip, ORG_CODE};
						executeUpdate(starSchemaJdbcTemplate, "INSERT INTO CONCEPT_DIMENSION (CONCEPT_PATH, CONCEPT_CD, NAME_CHAR, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);

						params = new Object[]{conceptPath, conceptName, "N", conceptCode, metadataXml, conceptPath, conceptTooltip, ORG_CODE};					
						executeUpdate(metadataJdbcTemplate, "INSERT INTO PIH (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (3, ?, ?, ?, 'LA', NULL, ?, ?, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
					} else {
						Object[] params = new Object[]{conceptPath, conceptName, "Y", conceptCode, conceptPath, conceptTooltip, ORG_CODE};					
						executeUpdate(metadataJdbcTemplate, "INSERT INTO PIH (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (3, ?, ?, ?, 'LA', NULL, ?, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
					}
					return null;
				}
			});
			
		} catch(Exception e) {
			logger.error(new LogHelper(e));
		}
		return tmpCounter;
	}

	@SuppressWarnings({ "unchecked" })
	private int loadCodedConcepts() {
		tmpCounter = 0;
		try {
			
			sourceJdbcTemplate.query("SELECT DISTINCT obs.concept_id, value_coded, question.name, answer.name, question.concept_name_type, answer.concept_name_type, concept_class.name FROM obs, concept, concept_name question, concept_name answer, concept_class WHERE obs.concept_id = concept.concept_id AND  obs.concept_id = question.concept_id AND  obs.value_coded = answer.concept_id AND  concept.class_id = concept_class.concept_class_id AND concept.class_id = ? AND concept.datatype_id = ?", new Object[]{OPENMRS_CONCEPT_CLASS_QUESTION, OPENMRS_CONCEPT_DATATYPE_CODED},
					new RowMapper() {
				@Override
				public Object mapRow(ResultSet resultSet, int rowNum)
				throws SQLException {
					tmpCounter++;
					int questionConceptId = resultSet.getInt(1);
					int answerConceptId = resultSet.getInt(2);
					String question = resultSet.getString(3);
					String answer = resultSet.getString(4);
					String questionNameType = resultSet.getString(5);
					String answerNameType = resultSet.getString(6);
					String className = resultSet.getString(7);
					
					ConceptSource conceptSource = getConceptSource(questionConceptId);
					
					String conceptCode = generateConceptCode(questionConceptId, answerConceptId);
					String conceptName = (question + ": " + answer).toUpperCase();
					String conceptPath = "\\" + ORG_CODE + "\\" + className + "\\" + conceptName + " (" + conceptCode + ")" + "\\";
					String conceptTooltip = conceptName;

					logger.error(conceptCode + ": " + conceptName);					
					logger.error(conceptPath);					
					
					if (questionNameType != null && questionNameType.equalsIgnoreCase("FULLY_SPECIFIED")) {
						Object[] params = new Object[]{conceptPath, conceptCode, conceptTooltip, ORG_CODE};
						executeUpdate(starSchemaJdbcTemplate, "INSERT INTO CONCEPT_DIMENSION (CONCEPT_PATH, CONCEPT_CD, NAME_CHAR, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);

						params = new Object[]{conceptPath, conceptName, "N", conceptCode, "", conceptPath, conceptTooltip, ORG_CODE};					
						executeUpdate(metadataJdbcTemplate, "INSERT INTO PIH (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (3, ?, ?, ?, 'LA', NULL, ?, ?, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
					} else {
						Object[] params = new Object[]{conceptPath, conceptName, "Y", conceptCode, conceptPath, conceptTooltip, ORG_CODE};					
						executeUpdate(metadataJdbcTemplate, "INSERT INTO PIH (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (3, ?, ?, ?, 'LA', NULL, ?, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
					}
					return null;
				}
			});
			
		} catch(Exception e) {
			logger.error(new LogHelper(e));
		}
		return tmpCounter;
	}

	
	@SuppressWarnings("unchecked")
	private String generateMetadataXml(int conceptId, String conceptName) {
		
		try {
			ConceptNumeric conceptNumeric = (ConceptNumeric) sourceJdbcTemplate.queryForObject("SELECT * FROM concept_numeric WHERE concept_id = ?", new Object[]{conceptId},
				    new RowMapper() {
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	ConceptNumeric conceptNumeric = new ConceptNumeric();
		            conceptNumeric.setConceptId(rs.getInt(1));
		            conceptNumeric.setHiAbsolute(rs.getString(2) != null ? rs.getDouble(2) : null);
		            conceptNumeric.setHiCritical(rs.getString(3) != null ? rs.getDouble(3) : null);
		            conceptNumeric.setHiNormal(rs.getString(4) != null ? rs.getDouble(4) : null);
		            conceptNumeric.setLowAbsolute(rs.getString(5) != null ? rs.getDouble(5) : null);
		            conceptNumeric.setLowCritical(rs.getString(6) != null ? rs.getDouble(6) : null);
		            conceptNumeric.setLowNormal(rs.getString(7) != null ? rs.getDouble(7) : null);
		            conceptNumeric.setUnits(rs.getString(8));
		            conceptNumeric.setPrecise(rs.getInt(9));
		            return conceptNumeric;
		        }
		    });

			if (conceptNumeric != null) {
				JAXBContext context = JAXBContext.newInstance(ValueMetadata.class);
		        Marshaller marshaller = context.createMarshaller();
		        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

		        ValueMetadata valueMetadata = new ValueMetadata(); 
		        valueMetadata.setVersion(new BigDecimal("3.02"));
		        valueMetadata.setCreationDateTime(new Date().toString());
		        valueMetadata.setTestID("" + conceptId);
		        valueMetadata.setTestName(conceptName);
		        valueMetadata.setDataType(getMetadataDataType(conceptNumeric));
		        valueMetadata.setCodeType("GRP");
		        valueMetadata.setLoinc(getLoincCode(conceptNumeric));
		        valueMetadata.setFlagstouse(getFlagstouse(conceptNumeric));
		        valueMetadata.setOktousevalues("Y");
		        valueMetadata.setMaxStringLength(null);
		        valueMetadata.setLowofLowValue(getRangeValue(conceptNumeric.getLowAbsolute()));
		        valueMetadata.setHighofLowValue(getRangeValue(conceptNumeric.getLowCritical()));
		        valueMetadata.setLowofHighValue(getRangeValue(conceptNumeric.getHiNormal()));
		        valueMetadata.setHighofHighValue(getRangeValue(conceptNumeric.getHiCritical()));
		        
		        UnitValues unitValues = new UnitValues();
		        unitValues.addNormalUnit(conceptNumeric.getUnits());
		        unitValues.addEqualUnit(conceptNumeric.getUnits());
		        unitValues.setExcludingUnits(new ExcludingUnits());
		        ConvertingUnits convertingUnits = new ConvertingUnits();
		        convertingUnits.setUnits(new Units());
		        convertingUnits.setMultiplyingFactor(new MultiplyingFactor());	        
		        unitValues.setConvertingUnits(convertingUnits);
		        valueMetadata.setUnitValues(unitValues);
		        
		        Analysis analysis = new Analysis();
		        analysis.setCounts(new Counts());
		        analysis.setEnums(new Enums());
		        analysis.setNew(new org.o2i2b2.xsd.New());
		        valueMetadata.setAnalysis(analysis);
		        
		        StringWriter stringWriter = new StringWriter();
		        
		        marshaller.marshal(valueMetadata, stringWriter);

				return stringWriter.toString();
			}
		} catch (EmptyResultDataAccessException e) {
			// It's safe to ignore this exception.
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}

	private BigInteger getRangeValue(Double rangeValue) {
		return rangeValue != null ? new BigInteger("" + rangeValue.intValue()) : null;
	}

	private String getLoincCode(ConceptNumeric conceptNumeric) {
		try {
			String sourceCode = sourceJdbcTemplate.queryForObject("SELECT source_code FROM concept_map, concept_source WHERE concept_map.source = concept_source.concept_source_id AND hl7_code = 'LN' AND concept_map.concept_id = ?", new Object[]{conceptNumeric.getConceptId()}, String.class);
			if (sourceCode != null && sourceCode.trim().length() > 0) return sourceCode;
		} catch (EmptyResultDataAccessException e) {
			// It's safe to ignore this exception.
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return "0";
	}
	
	@SuppressWarnings("unchecked")
	private ConceptSource getConceptSource(int conceptId) {
		try {
			return (ConceptSource) sourceJdbcTemplate.queryForObject("SELECT DISTINCT source, name, source_code, hl7_code FROM concept_map, concept_source WHERE concept_map.source = concept_source.concept_source_id AND concept_map.concept_id = ? LIMIT 1", new Object[]{conceptId}, new RowMapper() {
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	ConceptSource conceptSource = new ConceptSource();
		        	conceptSource.setSystemId(rs.getInt(1));
		        	conceptSource.setSourceName(rs.getString(2));
		        	conceptSource.setSourceCode(rs.getString(3));
		        	conceptSource.setSystemHl7Code(rs.getString(4));
		            return conceptSource;
		        }
		    });
		} catch (EmptyResultDataAccessException e) {
			// It's safe to ignore this exception.
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}
	
	private String getFlagstouse(ConceptNumeric conceptNumeric) {
		String flagsToUse = "";
		
		if (conceptNumeric.getHiAbsolute() != null || conceptNumeric.getLowAbsolute() != null) flagsToUse += "H"; 
		if (conceptNumeric.getHiNormal() != null || conceptNumeric.getLowNormal() != null) flagsToUse += "N";
		if (conceptNumeric.getHiCritical() != null || conceptNumeric.getLowCritical() != null) flagsToUse += "L";
		
		return flagsToUse;
	}
	
	private String getMetadataDataType(ConceptNumeric conceptNumeric) {
        /*
         * Possible DataTypes:
         * 
         * Integer � domain of all integers
         * PosFloat � domain of all positive real numbers
         * Float � domain of all real numbers 
         * Enum � domain of enumerated values
         * String � domain of free text, NOT enumerated text values, which would be the Enum data type.
         */
		return conceptNumeric.getPrecise() == 0 ? "Integer" : "Float"; 
	}
	
	private String generateConceptCode(int questionConceptId, int answerConceptId) {
		int suffix = 0;
		String conceptCodeBase = contructConceptCode(questionConceptId, answerConceptId, suffix++);
		while (conceptIdList.contains(conceptCodeBase)) conceptCodeBase = contructConceptCode(questionConceptId, answerConceptId, suffix++);
		conceptIdList.add(conceptCodeBase);
		return formatConceptCode(conceptCodeBase);
	}
		
	private String contructConceptCode(int questionConceptId, int answerConceptId, int suffix) {
		return questionConceptId + (answerConceptId > 0 ? "_" + answerConceptId : "") + "." + suffix;
	}
	
	private String formatConceptCode(String conceptCodeBase) {
		return ORG_CODE + ":" + conceptCodeBase;
	}
	
	@SuppressWarnings({ "unchecked" })
	private int loadProviders() {

		tmpCounter = 0;
		try {
			sourceJdbcTemplate.query("SELECT users.user_id AS id, system_id AS system_id, given_name AS first_name, middle_name, family_name AS last_name, prefix FROM users, user_role LEFT JOIN person_name ON person_id = user_role.user_id AND person_name.voided = 0 WHERE role = 'Provider' AND users.user_id = user_role.user_id",
					new Object[]{},
					new RowMapper() {
				@SuppressWarnings("unused")
				@Override
				public Object mapRow(ResultSet resultSet, int rowNum)
				throws SQLException {
					tmpCounter++;
					String id = resultSet.getString(1);
					String systemId = resultSet.getString(2);
					String firstName = resultSet.getString(3);
					String middleName = resultSet.getString(4);
					String lastName = resultSet.getString(5);
					String prefix = resultSet.getString(6);
					
					String providerName = (lastName + ", " + firstName + " " + middleName).trim();
					String providerPath = ORG_CODE + "\\Providers\\" + providerName;
					String providerPathMeta = "\\" + ORG_CODE + "\\Providers\\" + providerName + "\\";
					String providerId = ORG_CODE + ":" + id;
					
					Object[] params = new Object[]{providerPathMeta, providerName, providerPathMeta, providerName, ORG_CODE};
					executeUpdate(metadataJdbcTemplate, "INSERT INTO PIH (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (3, ?, ?, 'N', 'LA', NULL, NULL, NULL, 'provider_id', 'provider_dimension', 'provider_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
					
					params = new Object[]{providerId, providerPath, providerName, ORG_CODE};
					executeUpdate(starSchemaJdbcTemplate, "INSERT INTO PROVIDER_DIMENSION (PROVIDER_ID, PROVIDER_PATH, NAME_CHAR, PROVIDER_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);

					return null;
				}
			});
			
		} catch(Exception e) {
			logger.error(new LogHelper(e));
		}
		return tmpCounter;
	}
		
	@SuppressWarnings({ "unchecked" })
	private int loadPatients() {

		tmpCounter = 0;
		try {
			sourceJdbcTemplate.query("select patient_id, gender, birthdate, death_date, tribe.name AS tribe from person, patient LEFT JOIN tribe ON tribe.tribe_id = patient.tribe where patient_id = person_id",
					new Object[]{},
					new RowMapper() {
				@Override
				public Object mapRow(ResultSet resultSet, int rowNum)
				throws SQLException {
					tmpCounter++;
					int id = resultSet.getInt(1);
					String gender = resultSet.getString(2);
					Date birthdate = resultSet.getDate(3);
					Date deathDate = resultSet.getDate(4);
					String tribe = resultSet.getString(5);
					
					int age = getAgeFromDate(birthdate);

					int patientNum = generatePatientNum();
					patientMapping.put(id, patientNum);
					
					Object[] params = new Object[]{id, ORG_CODE, patientNum, ORG_CODE};
					executeUpdate(starSchemaJdbcTemplate, "INSERT INTO PATIENT_MAPPING (PATIENT_IDE, PATIENT_IDE_SOURCE, PATIENT_NUM, PATIENT_IDE_STATUS, UPLOAD_DATE, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, 'A', SYSDATE, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
					
					params = new Object[]{patientNum, birthdate, deathDate, gender, age, "", tribe, "", "", "", "", ORG_CODE};
					executeUpdate(starSchemaJdbcTemplate, "INSERT INTO PATIENT_DIMENSION (PATIENT_NUM, VITAL_STATUS_CD, BIRTH_DATE, DEATH_DATE, SEX_CD, AGE_IN_YEARS_NUM, LANGUAGE_CD, RACE_CD, MARITAL_STATUS_CD, RELIGION_CD, ZIP_CD, STATECITYZIP_PATH, PATIENT_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, 'N', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);

					return null;
				}
			});
			
		} catch(Exception e) {
			logger.error(new LogHelper(e));
		}
		return tmpCounter;
	}
	
	@SuppressWarnings({ "unchecked" })
	private int loadVisits() {

		tmpCounter = 0;
		try {
			sourceJdbcTemplate.query("SELECT DISTINCT encounter_id, patient_id, provider_id, location.name, encounter_datetime FROM encounter, location WHERE encounter.location_id = location.location_id",
					new Object[]{},
					new RowMapper() {
				@SuppressWarnings("unused")
				@Override
				public Object mapRow(ResultSet resultSet, int rowNum)
				throws SQLException {
					tmpCounter++;
					int encounterId = resultSet.getInt(1);
					int patientIde = resultSet.getInt(2);
					int providerId = resultSet.getInt(3);
					String locaiton =  resultSet.getString(4);
					Date encounterTimestamp = resultSet.getTimestamp(5);

					int patientNum = getPatientNum(patientIde);
					
					Object[] params = new Object[]{encounterId, patientNum, encounterTimestamp, encounterTimestamp, "O", locaiton, "", ORG_CODE};
					executeUpdate(starSchemaJdbcTemplate, "INSERT INTO VISIT_DIMENSION (ENCOUNTER_NUM, PATIENT_NUM, ACTIVE_STATUS_CD, START_DATE, END_DATE, INOUT_CD, LOCATION_CD, LOCATION_PATH, VISIT_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, 'A', ?, ?, ?, ?, ?, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);

					return null;
				}
			});
			
		} catch(Exception e) {
			logger.error(new LogHelper(e));
		}
		return tmpCounter;
	}
	
	@SuppressWarnings({ "unchecked" })
	private int loadObservationFacts() {

		tmpCounter = 0;
		try {
			sourceJdbcTemplate.query("SELECT DISTINCT obs.encounter_id, patient_id, obs.concept_id, provider_id, obs_datetime, location.name, concept.datatype_id, concept_datatype.name, concept.class_id, concept_class.name, concept_name.name, obs_group_id, accession_number, " + 
                                     "value_group_id, value_boolean, value_coded, value_drug, value_datetime, value_numeric, value_modifier, value_text " +
                                     "FROM encounter, obs, location, concept, concept_datatype, concept_class, concept_name " +
                                     "WHERE encounter.location_id = location.location_id " +
                                     "AND   obs.person_id = patient_id " +
                                     "AND   obs.encounter_id = encounter.encounter_id " +
                                     "AND   obs.concept_id = concept.concept_id " +
                                     "AND   obs.concept_id = concept_name.concept_id " +
                                     "AND   concept_name.concept_name_type = 'FULLY_SPECIFIED' " +
                                     "AND   concept.datatype_id = concept_datatype.concept_datatype_id " +
                                     "AND   concept.class_id = concept_class.concept_class_id " +
                                     "AND   concept.datatype_id IN (1, 2)",
					new Object[]{},
					new RowMapper() {
				@SuppressWarnings("unused")
				@Override
				public Object mapRow(ResultSet resultSet, int rowNum)
				throws SQLException {
					tmpCounter++;
					int encounterId = resultSet.getInt(1);
					int patientIde = resultSet.getInt(2);
					int conceptId = resultSet.getInt(3);
					int providerId = resultSet.getInt(4);
					Date datetime = resultSet.getTimestamp(5);
					String location = resultSet.getString(6);
					int conceptDatatypeId = resultSet.getInt(7);
					String conceptDatatypeName = resultSet.getString(8);
					int conceptClassId = resultSet.getInt(9);
					String conceptClassName = resultSet.getString(10);
					String conceptName = resultSet.getString(11);
					int obsGroupId = resultSet.getInt(12);
					String accessionNumber = resultSet.getString(13);
					int valueGroupId = resultSet.getInt(14);
					int valueBoolean = resultSet.getInt(15);
					int valueCoded = resultSet.getInt(16);
					int valueDrug = resultSet.getInt(17);
					Date valueDatetime = resultSet.getDate(18);
					Double valueNumeric = resultSet.getDouble(19);
					String valueModifier = resultSet.getString(20);
					String valueText = resultSet.getString(21);
					
					int patientNum = getPatientNum(patientIde);
					
					String conceptCode = null;
					
					logger.error(tmpCounter + ": encounterId: " + encounterId);
					
					Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
					calendar.setTime(datetime);   // assigns calendar to given date 
					if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.SECOND) == 0) {
						Random randomGenerator = new Random();
						int randomInt = randomGenerator.nextInt(100);
						calendar.add(Calendar.MILLISECOND, randomInt);
						datetime = calendar.getTime();
					}
					
					switch (conceptDatatypeId) {
					case 1:
						// Numeric: Numeric value, including integer or float (e.g., creatinine, weight)
						String value = valueNumeric.toString();
						conceptCode = formatConceptCode(contructConceptCode(conceptId, 0, 0));
						Object[] paramsNumeric = new Object[]{encounterId, patientNum, conceptCode, providerId, datetime, "@", "N", "E", value, "", "@", datetime, location, ORG_CODE};
						executeUpdate(starSchemaJdbcTemplate, "INSERT INTO OBSERVATION_FACT (ENCOUNTER_NUM, PATIENT_NUM, CONCEPT_CD, PROVIDER_ID, START_DATE, MODIFIER_CD, VALTYPE_CD, TVAL_CHAR, NVAL_NUM, VALUEFLAG_CD, QUANTITY_NUM, INSTANCE_NUM, UNITS_CD, END_DATE, LOCATION_CD, CONFIDENCE_NUM, OBSERVATION_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, ?, ?, ?, NULL, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", paramsNumeric);
						break;
					case 2:
						// Coded: Value determined by term dictionary lookup (i.e., term identifier)
						conceptCode = formatConceptCode(contructConceptCode(conceptId, valueCoded, 0));
						Object[] paramsCoded = new Object[]{encounterId, patientNum, conceptCode, providerId, datetime, "@", "@", "@", "", "", "@", datetime, location, ORG_CODE};
						executeUpdate(starSchemaJdbcTemplate, "INSERT INTO OBSERVATION_FACT (ENCOUNTER_NUM, PATIENT_NUM, CONCEPT_CD, PROVIDER_ID, START_DATE, MODIFIER_CD, VALTYPE_CD, TVAL_CHAR, NVAL_NUM, VALUEFLAG_CD, QUANTITY_NUM, INSTANCE_NUM, UNITS_CD, END_DATE, LOCATION_CD, CONFIDENCE_NUM, OBSERVATION_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, ?, ?, ?, NULL, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", paramsCoded);
						break;
					case 3:
						// Text: Free text
						// value = valueText;
						// params = new Object[]{encounterId, patientNum, conceptCode, providerId, datetime, "@", "@", value, "", "", "@", datetime, location, ORG_CODE};
						// executeUpdate(starSchemaJdbcTemplate, "INSERT INTO OBSERVATION_FACT (ENCOUNTER_NUM, PATIENT_NUM, CONCEPT_CD, PROVIDER_ID, START_DATE, MODIFIER_CD, VALTYPE_CD, TVAL_CHAR, NVAL_NUM, VALUEFLAG_CD, QUANTITY_NUM, INSTANCE_NUM, UNITS_CD, END_DATE, LOCATION_CD, CONFIDENCE_NUM, OBSERVATION_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, ?, ?, ?, NULL, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
						break;
					case 4:
						// NA: Not associated with a datatype (e.g., term answers, sets) 
						break;
					case 5:
						// Document: Pointer to a binary or text-based document (e.g., clinical document, RTF, XML, EKG, image, etc.) stored in complex_obs table 
						break;
					case 6:
						// Date: Absolute date 
						break;
					case 7:
						// Time: Absolute time of day 
						break;
					case 8:
						// Datetime: Absolute date and time 
						break;
					case 9:
						// Boolean: Boolean value (yes/no, true/false) 
						break;
					case 10:
						// Rule: Value derived from other data 
						break;
					case 11:
						// Structured Numeric: Complex numeric values possible (ie, <5, 1-10, etc.) 
						break;
					case 12:
						// Complex: Complex value.  Analogous to HL7 Embedded Datatype
						break;
					default:
						break;
					}

					return null;
				}
			});
			
		} catch(Exception e) {
			logger.error(new LogHelper(e));
		}
		return tmpCounter;
	}
	
	private int getPatientNum(int patientId) {
		try {
//			return patientMapping.get(patientId);
			return starSchemaJdbcTemplate.queryForInt("SELECT patient_num FROM patient_mapping WHERE patient_ide = ?", new Object[]{patientId});
		} catch (Exception e) {
			logger.error(new LogHelper(e));
		}
		return 0;
	}
	
	private int generatePatientNum() {
		return 20000000 + starSchemaJdbcTemplate.queryForInt("SELECT SQ_UP_PATDIM_PATIENTNUM.NEXTVAL FROM dual");
	}
	
	private int getAgeFromDate(Date birthdate) {
		try {
			Calendar dateOfBirth = new GregorianCalendar();
			dateOfBirth.setTime(birthdate);
			Calendar today = Calendar.getInstance();
			int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
			dateOfBirth.add(Calendar.YEAR, age);
			if (today.before(dateOfBirth)) age--;
			return age;
		} catch (Exception e) {
			logger.error(new LogHelper(e));
		}
		return -1;
	}
		
	private void deleteAll() {
		executeUpdate(metadataJdbcTemplate, "DROP TABLE " + ORG_CODE , null);
		executeUpdate(metadataJdbcTemplate, "DELETE FROM SCHEMES WHERE C_KEY = ?", new Object[]{ORG_CODE + ":"});
		executeUpdate(metadataJdbcTemplate, "DELETE FROM TABLE_ACCESS WHERE C_TABLE_CD = ?", new Object[]{ORG_CODE});
		executeUpdate(starSchemaJdbcTemplate, "DELETE FROM CONCEPT_DIMENSION WHERE SOURCESYSTEM_CD = ?", new Object[]{ORG_CODE});
		executeUpdate(starSchemaJdbcTemplate, "DELETE FROM PROVIDER_DIMENSION WHERE SOURCESYSTEM_CD = ?", new Object[]{ORG_CODE});
		executeUpdate(starSchemaJdbcTemplate, "DELETE FROM PATIENT_DIMENSION WHERE SOURCESYSTEM_CD = ?", new Object[]{ORG_CODE});
		executeUpdate(starSchemaJdbcTemplate, "DELETE FROM VISIT_DIMENSION WHERE SOURCESYSTEM_CD = ?", new Object[]{ORG_CODE});
		executeUpdate(starSchemaJdbcTemplate, "DELETE FROM PATIENT_MAPPING WHERE SOURCESYSTEM_CD = ?", new Object[]{ORG_CODE});
		executeUpdate(starSchemaJdbcTemplate, "DELETE FROM OBSERVATION_FACT WHERE SOURCESYSTEM_CD = ?", new Object[]{ORG_CODE});
	}

	private void deleteSome() {
		executeUpdate(metadataJdbcTemplate, "DROP TABLE " + ORG_CODE , null);
//		executeUpdate(metadataJdbcTemplate, "DELETE FROM SCHEMES WHERE C_KEY = ?", new Object[]{ORG_CODE + ":"});
//		executeUpdate(metadataJdbcTemplate, "DELETE FROM TABLE_ACCESS WHERE C_TABLE_CD = ?", new Object[]{ORG_CODE});
		executeUpdate(starSchemaJdbcTemplate, "DELETE FROM CONCEPT_DIMENSION WHERE SOURCESYSTEM_CD = ?", new Object[]{ORG_CODE});
//		executeUpdate(starSchemaJdbcTemplate, "DELETE FROM PROVIDER_DIMENSION WHERE SOURCESYSTEM_CD = ?", new Object[]{ORG_CODE});
//		executeUpdate(starSchemaJdbcTemplate, "DELETE FROM PATIENT_DIMENSION WHERE SOURCESYSTEM_CD = ?", new Object[]{ORG_CODE});
//		executeUpdate(starSchemaJdbcTemplate, "DELETE FROM PATIENT_MAPPING WHERE SOURCESYSTEM_CD = ?", new Object[]{ORG_CODE});
//		executeUpdate(starSchemaJdbcTemplate, "DELETE FROM VISIT_DIMENSION WHERE SOURCESYSTEM_CD = ?", new Object[]{ORG_CODE});
//		executeUpdate(starSchemaJdbcTemplate, "DELETE FROM OBSERVATION_FACT WHERE SOURCESYSTEM_CD = ?", new Object[]{ORG_CODE});
	}

	private void createAll() {
		executeUpdate(metadataJdbcTemplate, "CREATE TABLE " + ORG_CODE + " (C_HLEVEL NUMBER(22,0) NOT NULL, C_FULLNAME VARCHAR2(700) NOT NULL, C_NAME VARCHAR2(2000) NOT NULL, C_SYNONYM_CD CHAR(1) NOT NULL, C_VISUALATTRIBUTES CHAR(3) NOT NULL, C_TOTALNUM NUMBER(22,0) NULL, C_BASECODE VARCHAR2(50) NULL, C_METADATAXML CLOB NULL, C_FACTTABLECOLUMN VARCHAR2(50) NOT NULL, C_TABLENAME VARCHAR2(50) NOT NULL, C_COLUMNNAME VARCHAR2(50) NOT NULL, C_COLUMNDATATYPE VARCHAR2(50) NOT NULL, C_OPERATOR VARCHAR2(10) NOT NULL, C_DIMCODE VARCHAR2(700) NOT NULL, C_COMMENT CLOB NULL, C_TOOLTIP VARCHAR2(900) NULL, UPDATE_DATE DATE NOT NULL, DOWNLOAD_DATE DATE NULL, IMPORT_DATE DATE NULL, SOURCESYSTEM_CD VARCHAR2(50) NULL, VALUETYPE_CD VARCHAR2(50) NULL)", null);
		executeUpdate(metadataJdbcTemplate, "INSERT INTO SCHEMES (C_KEY, C_NAME, C_DESCRIPTION) VALUES (?, ?, ?)", new Object[]{ORG_CODE + ":", ORG_CODE, ORG_NAME});
		executeUpdate(metadataJdbcTemplate, "INSERT INTO TABLE_ACCESS (C_TABLE_CD, C_TABLE_NAME, C_PROTECTED_ACCESS, C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_DIMTABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, C_ENTRY_DATE, C_CHANGE_DATE, C_STATUS_CD, VALUETYPE_CD) VALUES (?, ?, 'N', 0, ?, ?, 'N', 'CA', NULL, NULL, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, NULL, NULL, NULL, NULL)", new Object[]{ORG_CODE, ORG_CODE, "\\" + ORG_CODE + "\\", ORG_NAME, "\\" + ORG_CODE + "\\", ORG_NAME});
		executeUpdate(metadataJdbcTemplate, "INSERT INTO PIH (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (1, ?, ?, 'N', 'FA', NULL, NULL, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", new Object[]{"\\" + ORG_CODE + "\\", ORG_CODE, "\\" + ORG_CODE + "\\", ORG_CODE, ORG_CODE});
		executeUpdate(metadataJdbcTemplate, "INSERT INTO PIH (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (2, ?, ?, 'N', 'FA', NULL, NULL, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", new Object[]{"\\" + ORG_CODE + "\\Providers\\", "Providers", "\\" + ORG_CODE + "\\Providers\\", "Providers", ORG_CODE});
	}

	private void createSome() {
		executeUpdate(metadataJdbcTemplate, "CREATE TABLE " + ORG_CODE + " (C_HLEVEL NUMBER(22,0) NOT NULL, C_FULLNAME VARCHAR2(700) NOT NULL, C_NAME VARCHAR2(2000) NOT NULL, C_SYNONYM_CD CHAR(1) NOT NULL, C_VISUALATTRIBUTES CHAR(3) NOT NULL, C_TOTALNUM NUMBER(22,0) NULL, C_BASECODE VARCHAR2(50) NULL, C_METADATAXML CLOB NULL, C_FACTTABLECOLUMN VARCHAR2(50) NOT NULL, C_TABLENAME VARCHAR2(50) NOT NULL, C_COLUMNNAME VARCHAR2(50) NOT NULL, C_COLUMNDATATYPE VARCHAR2(50) NOT NULL, C_OPERATOR VARCHAR2(10) NOT NULL, C_DIMCODE VARCHAR2(700) NOT NULL, C_COMMENT CLOB NULL, C_TOOLTIP VARCHAR2(900) NULL, UPDATE_DATE DATE NOT NULL, DOWNLOAD_DATE DATE NULL, IMPORT_DATE DATE NULL, SOURCESYSTEM_CD VARCHAR2(50) NULL, VALUETYPE_CD VARCHAR2(50) NULL)", null);
//		executeUpdate(metadataJdbcTemplate, "INSERT INTO SCHEMES (C_KEY, C_NAME, C_DESCRIPTION) VALUES (?, ?, ?)", new Object[]{ORG_CODE + ":", ORG_CODE, ORG_NAME});
//		executeUpdate(metadataJdbcTemplate, "INSERT INTO TABLE_ACCESS (C_TABLE_CD, C_TABLE_NAME, C_PROTECTED_ACCESS, C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_DIMTABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, C_ENTRY_DATE, C_CHANGE_DATE, C_STATUS_CD, VALUETYPE_CD) VALUES (?, ?, 'N', 0, ?, ?, 'N', 'CA', NULL, NULL, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, NULL, NULL, NULL, NULL)", new Object[]{ORG_CODE, ORG_CODE, "\\" + ORG_CODE + "\\", ORG_NAME, "\\" + ORG_CODE + "\\", ORG_NAME});
		executeUpdate(metadataJdbcTemplate, "INSERT INTO PIH (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (1, ?, ?, 'N', 'FA', NULL, NULL, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", new Object[]{"\\" + ORG_CODE + "\\", ORG_CODE, "\\" + ORG_CODE + "\\", ORG_CODE, ORG_CODE});
		executeUpdate(metadataJdbcTemplate, "INSERT INTO PIH (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (2, ?, ?, 'N', 'FA', NULL, NULL, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", new Object[]{"\\" + ORG_CODE + "\\Providers\\", "Providers", "\\" + ORG_CODE + "\\Providers\\", "Providers", ORG_CODE});
	}

	private boolean executeUpdate(JdbcTemplate jdbcTemplate, String sql, Object[] params) {
		try {
			if (logger.isEnabledFor(Priority.DEBUG)) {
				logger.debug(this.getClass().getName() + ".executeUpdate: sql: " + sql);
				if (params != null) {
					int paramCounter = 0;
					for (Object param : params) {
						logger.debug(this.getClass().getName() + ".executeUpdate: param[" + paramCounter++ + "]: " + param);
					}
				}
			}
			jdbcTemplate.update(sql, params);
			return true;
		} catch (Exception e) {
			logger.error(this.getClass().getName() + ".executeUpdate: sql: " + sql);
			if (params != null) {
				int paramCounter = 0;
				for (Object param : params) {
					logger.error(this.getClass().getName() + ".executeUpdate: param[" + paramCounter++ + "]: " + param);
				}
			}
			logger.error(new LogHelper(e));
		}
		return false;
	}

}
