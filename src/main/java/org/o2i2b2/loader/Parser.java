package org.o2i2b2.loader;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.math.NumberUtils;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.o2i2b2.dao.objects.MetadataTableAccess;
import org.o2i2b2.i2b2.schema.Concept;
import org.o2i2b2.i2b2.schema.Encounter;
import org.o2i2b2.i2b2.schema.Meta;
import org.o2i2b2.i2b2.schema.O2I2B2;
import org.o2i2b2.i2b2.schema.Observation;
import org.o2i2b2.i2b2.schema.Patient;
import org.o2i2b2.i2b2.schema.Provider;
import org.o2i2b2.i2b2.schema.Source;
import org.o2i2b2.objects.PatientDb;
import org.o2i2b2.xsd.Analysis;
import org.o2i2b2.xsd.ConvertingUnits;
import org.o2i2b2.xsd.Counts;
import org.o2i2b2.xsd.Enums;
import org.o2i2b2.xsd.ExcludingUnits;
import org.o2i2b2.xsd.MultiplyingFactor;
import org.o2i2b2.xsd.UnitValues;
import org.o2i2b2.xsd.Units;
import org.o2i2b2.xsd.ValueMetadata;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class Parser implements Runnable {

	private JdbcTemplate metadataJdbcTemplate;
	private JdbcTemplate starSchemaJdbcTemplate;
	private O2I2B2 o2i2b2;
	private String sourceId;
	private MetadataTableAccess metadataTableAccess;
	private String importPath;

	private String message = App.messages.getString("app.parser.status.default");

	private boolean interuptMe;

	private int importCounter = 0;

	private boolean complete = false;

	private int step = 0;
	private int steps = 0;

	private String loincSourceId = null; 

	@SuppressWarnings("unchecked")
	@Override
	public void run() {

		try {
			XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(new java.io.FileInputStream(importPath));

			String parentElementName = "";

			//set up JAXB contexts
			JAXBContext jaxbContextSource = JAXBContext.newInstance(Source.class);
			Unmarshaller unmarshallerSource = jaxbContextSource.createUnmarshaller();

			JAXBContext jaxbContextMeta = JAXBContext.newInstance(Meta.class);
			Unmarshaller unmarshallerMeta = jaxbContextMeta.createUnmarshaller();

			JAXBContext jaxbContextConcept = JAXBContext.newInstance(Concept.class);
			Unmarshaller unmarshallerConcept = jaxbContextConcept.createUnmarshaller();

			JAXBContext jaxbContextPatient = JAXBContext.newInstance(Patient.class);
			Unmarshaller unmarshallerPatient = jaxbContextPatient.createUnmarshaller();

			JAXBContext jaxbContextProvider = JAXBContext.newInstance(Provider.class);
			Unmarshaller unmarshallerProvider = jaxbContextProvider.createUnmarshaller();

			JAXBContext jaxbContextEncounter = JAXBContext.newInstance(Encounter.class);
			Unmarshaller unmarshallerEncounter = jaxbContextEncounter.createUnmarshaller();

			JAXBContext jaxbContextObservation = JAXBContext.newInstance(Observation.class);
			Unmarshaller unmarshallerObservation = jaxbContextObservation.createUnmarshaller();

			while (reader.hasNext()) {
				XMLEvent event = (XMLEvent) reader.next();

				if (event.isStartElement()) {
					String name = event.asStartElement().getName().toString();
					Element element = new Element(name);

					Iterator<Attribute> iterator = event.asStartElement().getAttributes();
					while (iterator.hasNext()) {
						Attribute attribute = (Attribute) iterator.next();
						element.setAttribute(attribute.getName().toString(), attribute.getValue());
					}

					StringWriter stringWriter = new StringWriter();
					XMLOutputter outputter = new XMLOutputter();
				    outputter.output(element, stringWriter);
					
				    String elementString = stringWriter.toString();
					System.out.println(name + " :: " + elementString);
					InputStream elementInputStream = new ByteArrayInputStream(elementString.getBytes());

					if (name.equalsIgnoreCase("sources")) {
						step++;
						importCounter = 0;
						message = App.messages.getString("app.parser.status.sources");
						parentElementName = name;
					} else if (name.equalsIgnoreCase("ontology")) {
						step++;
						importCounter = 0;
						message = App.messages.getString("app.parser.status.ontologies");
						parentElementName = name;
					} else if (name.equalsIgnoreCase("concepts")) {
						step++;
						importCounter = 0;
						message = App.messages.getString("app.parser.status.concepts");
						parentElementName = name;
					} else if (name.equalsIgnoreCase("patients")) {
						step++;
						importCounter = 0;
						message = App.messages.getString("app.parser.status.patients");
						parentElementName = name;
					} else if (name.equalsIgnoreCase("providers")) {
						step++;
						importCounter = 0;
						message = App.messages.getString("app.parser.status.providers");
						parentElementName = name;
					} else if (name.equalsIgnoreCase("encounters")) {
						step++;
						importCounter = 0;
						message = App.messages.getString("app.parser.status.encounters");
						parentElementName = name;
					} else if (name.equalsIgnoreCase("observations")) {
						step++;
						importCounter = 0;
						message = App.messages.getString("app.parser.status.observations");
						parentElementName = name;
					}

					if (name.equalsIgnoreCase("source") && parentElementName.equalsIgnoreCase("sources")) {
						step++;
						Source source = (Source) unmarshallerSource.unmarshal(elementInputStream);
						System.out.println("  Source ==> " + source.getName());
						if (source.getHl7() != null && source.getHl7().equalsIgnoreCase("LN") || source.getName().equalsIgnoreCase("LOINC")) loincSourceId = source.getId();
						try {
							persistSource(source);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else	if (name.equalsIgnoreCase("meta")) {
						step++;
						try {
							persistMeta((Meta) unmarshallerMeta.unmarshal(elementInputStream));
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else	if (name.equalsIgnoreCase("concept")) {
						step++;
						try {
							persistConcept((Concept) unmarshallerConcept.unmarshal(elementInputStream));							
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else	if (name.equalsIgnoreCase("patient")) {
						step++;
						persistPatient((Patient) unmarshallerPatient.unmarshal(elementInputStream));
					} else	if (name.equalsIgnoreCase("provider")) {
						step++;
						persistProvider((Provider) unmarshallerProvider.unmarshal(elementInputStream));
					} else	if (name.equalsIgnoreCase("encounter")) {
						step++;
						persistEncounter((Encounter) unmarshallerEncounter.unmarshal(elementInputStream));
					} else	if (name.equalsIgnoreCase("observation")) {
						step++;
						persistObservation((Observation) unmarshallerObservation.unmarshal(elementInputStream));
					}
				}
				if (interuptMe || ImportData.isFatalError()) return;

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		message = App.messages.getString("app.parser.status.complete");
		complete = true;
	}

	public int countSteps() {
		try {
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileReader(importPath));

			xmlStreamReader.nextTag();
			xmlStreamReader.require(START_ELEMENT, null, "o2i2b2");

			String parentElementName = null;

			while (true) {
				int event = xmlStreamReader.next();
				if (event == XMLStreamConstants.END_DOCUMENT) {
					xmlStreamReader.close();
					break;
				}
				if (event == XMLStreamConstants.START_ELEMENT) {
					if (xmlStreamReader.getLocalName().equalsIgnoreCase("sources")) {
						steps++;
						parentElementName = xmlStreamReader.getLocalName();
					} else if (xmlStreamReader.getLocalName().equalsIgnoreCase("ontology")) {
						steps++;
						parentElementName = xmlStreamReader.getLocalName();
					} else if (xmlStreamReader.getLocalName().equalsIgnoreCase("concepts")) {
						steps++;
						parentElementName = xmlStreamReader.getLocalName();
					} else if (xmlStreamReader.getLocalName().equalsIgnoreCase("patients")) {
						steps++;
						parentElementName = xmlStreamReader.getLocalName();
					} else if (xmlStreamReader.getLocalName().equalsIgnoreCase("providers")) {
						steps++;
						parentElementName = xmlStreamReader.getLocalName();
					} else if (xmlStreamReader.getLocalName().equalsIgnoreCase("encounters")) {
						steps++;
						parentElementName = xmlStreamReader.getLocalName();
					} else if (xmlStreamReader.getLocalName().equalsIgnoreCase("observations")) {
						steps++;
						parentElementName = xmlStreamReader.getLocalName();
					}

					if (xmlStreamReader.getLocalName().equalsIgnoreCase("source") && parentElementName.equalsIgnoreCase("sources")) {
						steps++;
					} else	if (xmlStreamReader.getLocalName().equalsIgnoreCase("meta")) {
						steps++;
					} else	if (xmlStreamReader.getLocalName().equalsIgnoreCase("concept")) {
						steps++;
					} else	if (xmlStreamReader.getLocalName().equalsIgnoreCase("patient")) {
						steps++;
					} else	if (xmlStreamReader.getLocalName().equalsIgnoreCase("provider")) {
						steps++;
					} else	if (xmlStreamReader.getLocalName().equalsIgnoreCase("encounter")) {
						steps++;
					} else	if (xmlStreamReader.getLocalName().equalsIgnoreCase("observation")) {
						steps++;
					}
				}
				if (interuptMe || ImportData.isFatalError()) return steps;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

		System.out.println("Steps discovered: " + steps);

		return steps;
	}

	private void persistSource(Source source) {

		message = MessageFormat.format(App.messages.getString("app.parser.status.sources"), " (" + ++importCounter + ")");

		System.out.println("  Source ==> " + source.getName() + " :: " + source.getDescription());
		
		if (!conceptSourceExists(source.getName())) {
			ImportUtils.executeUpdate(metadataJdbcTemplate, "INSERT INTO SCHEMES (C_KEY, C_NAME, C_DESCRIPTION) VALUES (?, ?, ?)", new Object[]{source.getName() + ":", source.getName(), source.getDescription().length() > 99 ? source.getDescription().substring(0, 99) : source.getDescription()});
		}
	}

	private void persistMeta(Meta meta) {

		message = MessageFormat.format(App.messages.getString("app.parser.status.ontologies"), " (" + ++importCounter + ")");
		String path = ImportUtils.formatPath(meta.getPath(), metadataTableAccess, sourceId);

		System.out.println("  Meta ==> " + meta.getName() + " :: " + path);
		
		if (!conceptExists(path, meta.getName(), "")) {
			if (meta.getLeaf() != null && meta.getLeaf().equalsIgnoreCase("true")) {
				if (!conceptDimensionExists(path)) {
					Object[] params = new Object[]{path, meta.getConceptCode(), meta.getTooltip().length() > 900 ? meta.getTooltip().substring(0, 900) : meta.getTooltip(), sourceId};
					ImportUtils.executeUpdate(starSchemaJdbcTemplate, "INSERT INTO CONCEPT_DIMENSION (CONCEPT_PATH, CONCEPT_CD, NAME_CHAR, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
				}

				Object[] params = new Object[]{computeLevel(path), path, meta.getName(), path, meta.getTooltip(), sourceId};
				ImportUtils.executeUpdate(metadataJdbcTemplate, String.format("INSERT INTO %s (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (?, ?, ?, 'N', 'LA', NULL, NULL, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", metadataTableAccess.getTableName()), params);
			} else {
				Object[] params = new Object[]{computeLevel(path), path, meta.getName(), path, meta.getTooltip(), sourceId};
				ImportUtils.executeUpdate(metadataJdbcTemplate, String.format("INSERT INTO %s (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (?, ?, ?, 'N', 'FA', NULL, NULL, NULL, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", metadataTableAccess.getTableName()), params);
			}
		}
	}

	private int computeLevel(String path) {
		String[] limiterCount = path.split("\\\\");
		return limiterCount.length - 1;
	}

	private void persistProvider(Provider provider) {
		String path = ImportUtils.formatPath(provider.getPath(), metadataTableAccess, sourceId);

		System.out.println("  Provider ==> " + provider.getId() + " :: " + path);
		message = MessageFormat.format(App.messages.getString("app.parser.status.providers"), " (" + ++importCounter + ")");

		if (!conceptExists(path, provider.getLastFirstName(), "")) {
			Object[] params = new Object[]{computeLevel(path), path, provider.getLastFirstName(), path, provider.getLastFirstName(), sourceId};
			ImportUtils.executeUpdate(metadataJdbcTemplate, String.format("INSERT INTO %s (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (?, ?, ?, 'N', 'LA', NULL, NULL, NULL, 'provider_id', 'provider_dimension', 'provider_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", metadataTableAccess.getTableName()), params);
		}

		Object[] params = new Object[]{provider.getId(), path, provider.getLastFirstName(), sourceId};
		ImportUtils.executeUpdate(starSchemaJdbcTemplate, "INSERT INTO PROVIDER_DIMENSION (PROVIDER_ID, PROVIDER_PATH, NAME_CHAR, PROVIDER_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
	}

	private void persistPatient(Patient patient) {

		if (patient.getId().equals("4003")) {
			System.out.println("pause here...");
		}

		System.out.println("  Patient ==> " + patient.getId());
		message = MessageFormat.format(App.messages.getString("app.parser.status.patients"), " (" + ++importCounter + ")");
		int patientNum = getPatientNumFromPatientMapping(patient.getId(), sourceId);
		if (patientNum > 0) {
			Object[] params = new Object[]{patientNum, ImportUtils.formatDate(patient.getBirthdate()), ImportUtils.formatDate(patient.getDeathdate()), patient.getSex(),  new Integer("" + patient.getAge()), patient.getLanguage(), patient.getRace(), patient.getMaritalStatus(), patient.getReligion(), patient.getZip(), sourceId};
			ImportUtils.executeUpdate(starSchemaJdbcTemplate, "INSERT INTO PATIENT_DIMENSION (PATIENT_NUM, VITAL_STATUS_CD, BIRTH_DATE, DEATH_DATE, SEX_CD, AGE_IN_YEARS_NUM, LANGUAGE_CD, RACE_CD, MARITAL_STATUS_CD, RELIGION_CD, ZIP_CD, STATECITYZIP_PATH, PATIENT_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, 'N', ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
		}
	}

	private int getPatientNumFromPatientMapping(String patientId, String sourceId) {
		try {
			return starSchemaJdbcTemplate.queryForInt("SELECT patient_num FROM patient_mapping WHERE patient_ide = ? AND patient_ide_source = ?", new Object[]{patientId, sourceId});
		} catch (EmptyResultDataAccessException e) {
			int patientNum = generatePatientNum();
			Object[] params = new Object[]{patientId, sourceId, patientNum, sourceId};
			ImportUtils.executeUpdate(starSchemaJdbcTemplate, "INSERT INTO PATIENT_MAPPING (PATIENT_IDE, PATIENT_IDE_SOURCE, PATIENT_NUM, PATIENT_IDE_STATUS, UPLOAD_DATE, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, 'A', SYSDATE, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
			return patientNum;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	private int getEncounterNumFromEncounterMapping(String encounterId, String patientId, String sourceId) {
		try {
			return starSchemaJdbcTemplate.queryForInt("SELECT encounter_num FROM encounter_mapping WHERE encounter_ide = ? AND encounter_ide_source = ?", new Object[]{encounterId, sourceId});
		} catch (EmptyResultDataAccessException e) {

			int encounterNum = generateEncounterNum();
			Object[] params = new Object[]{encounterId, sourceId, encounterNum, patientId, sourceId, sourceId};
			ImportUtils.executeUpdate(starSchemaJdbcTemplate, "INSERT INTO ENCOUNTER_MAPPING (ENCOUNTER_IDE, ENCOUNTER_IDE_SOURCE, ENCOUNTER_NUM, PATIENT_IDE, PATIENT_IDE_SOURCE, ENCOUNTER_IDE_STATUS, UPLOAD_DATE, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, ?, ?, 'A', SYSDATE, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
			return encounterNum;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	private void persistEncounter(Encounter encounter) {
		System.out.println("  Encounter ==> " + encounter.getId());
		message = MessageFormat.format(App.messages.getString("app.parser.status.encounters"), " (" + ++importCounter + ")");
		int encounterNum = getEncounterNumFromEncounterMapping(encounter.getId(), encounter.getPatientId(), sourceId);
		if (encounterNum > 0) {
			int patientNum = getPatientNumFromPatientMapping(encounter.getPatientId(), sourceId);
			Object[] params = new Object[]{encounterNum, patientNum, ImportUtils.formatDate(encounter.getTimestamp()), ImportUtils.formatDate(encounter.getTimestamp()), "O", encounter.getLocation(), "", sourceId};
			ImportUtils.executeUpdate(starSchemaJdbcTemplate, "INSERT INTO VISIT_DIMENSION (ENCOUNTER_NUM, PATIENT_NUM, ACTIVE_STATUS_CD, START_DATE, END_DATE, INOUT_CD, LOCATION_CD, LOCATION_PATH, VISIT_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, 'A', ?, ?, ?, ?, ?, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);

			// TODO: persist demographics as of this encounter...
			PatientDb patient = getPatient(patientNum);

			if (patient != null) {
				if (patient.getSex() != null) {
					params = new Object[]{encounterNum, patientNum, ImportUtils.formatSexCd(patient.getSex()), "@", new Date(), "@", "@", "@", "", "", "@", new Date(), "@", sourceId};
					ImportUtils.executeUpdate(starSchemaJdbcTemplate, "INSERT INTO OBSERVATION_FACT (ENCOUNTER_NUM, PATIENT_NUM, CONCEPT_CD, PROVIDER_ID, START_DATE, MODIFIER_CD, VALTYPE_CD, TVAL_CHAR, NVAL_NUM, VALUEFLAG_CD, QUANTITY_NUM, INSTANCE_NUM, UNITS_CD, END_DATE, LOCATION_CD, CONFIDENCE_NUM, OBSERVATION_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, ?, ?, ?, NULL, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
				}

				if (patient.getBirthdateDb() != null) {
					params = new Object[]{encounterNum, patientNum, ImportUtils.formatAge(ImportUtils.formatDate(encounter.getTimestamp()), patient.getBirthdateDb()), "@", new Date(), "@", "@", "@", "", "", "@", new Date(), "@", sourceId};
					ImportUtils.executeUpdate(starSchemaJdbcTemplate, "INSERT INTO OBSERVATION_FACT (ENCOUNTER_NUM, PATIENT_NUM, CONCEPT_CD, PROVIDER_ID, START_DATE, MODIFIER_CD, VALTYPE_CD, TVAL_CHAR, NVAL_NUM, VALUEFLAG_CD, QUANTITY_NUM, INSTANCE_NUM, UNITS_CD, END_DATE, LOCATION_CD, CONFIDENCE_NUM, OBSERVATION_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, ?, ?, ?, NULL, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
					params = new Object[]{encounterNum, patientNum, "DEM|DATE:birth", "@", new Date(), "@", "D", ImportUtils.formatDateBdChar(patient.getBirthdateDb()), ImportUtils.formatDateBdNum(patient.getBirthdateDb()), "", "@", new Date(), "@", sourceId};
					ImportUtils.executeUpdate(starSchemaJdbcTemplate, "INSERT INTO OBSERVATION_FACT (ENCOUNTER_NUM, PATIENT_NUM, CONCEPT_CD, PROVIDER_ID, START_DATE, MODIFIER_CD, VALTYPE_CD, TVAL_CHAR, NVAL_NUM, VALUEFLAG_CD, QUANTITY_NUM, INSTANCE_NUM, UNITS_CD, END_DATE, LOCATION_CD, CONFIDENCE_NUM, OBSERVATION_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, ?, ?, ?, NULL, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
				} 

				if (patient.getDeathdateDb() != null) {
					params = new Object[]{encounterNum, patientNum, "DEM|DATE:death", "@", new Date(), "@", "D", ImportUtils.formatDateBdChar(patient.getDeathdateDb()), ImportUtils.formatDateBdNum(patient.getDeathdateDb()), "", "@", new Date(), "@", sourceId};
					ImportUtils.executeUpdate(starSchemaJdbcTemplate, "INSERT INTO OBSERVATION_FACT (ENCOUNTER_NUM, PATIENT_NUM, CONCEPT_CD, PROVIDER_ID, START_DATE, MODIFIER_CD, VALTYPE_CD, TVAL_CHAR, NVAL_NUM, VALUEFLAG_CD, QUANTITY_NUM, INSTANCE_NUM, UNITS_CD, END_DATE, LOCATION_CD, CONFIDENCE_NUM, OBSERVATION_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, ?, ?, ?, NULL, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
				}

				params = new Object[]{encounterNum, patientNum, ImportUtils.formatVitalCd(patient.getDeathdateDb()), "@", new Date(), "@", "@", "@", "", "", "@", new Date(), "@", sourceId};
				ImportUtils.executeUpdate(starSchemaJdbcTemplate, "INSERT INTO OBSERVATION_FACT (ENCOUNTER_NUM, PATIENT_NUM, CONCEPT_CD, PROVIDER_ID, START_DATE, MODIFIER_CD, VALTYPE_CD, TVAL_CHAR, NVAL_NUM, VALUEFLAG_CD, QUANTITY_NUM, INSTANCE_NUM, UNITS_CD, END_DATE, LOCATION_CD, CONFIDENCE_NUM, OBSERVATION_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, ?, ?, ?, NULL, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);

				if (patient.getMaritalStatus() != null) {
					//					TODO: implement me...
				}
				if (patient.getRace() != null) {
					//					TODO: implement me...
				}
				if (patient.getLanguage() != null) {
					//					TODO: implement me...
				}

			}



		}
	}

	@SuppressWarnings("unchecked")
	private PatientDb getPatient(int patientNum) {
		try {
			return (PatientDb) starSchemaJdbcTemplate.queryForObject("SELECT vital_status_cd, birth_date, death_date, sex_cd, age_in_years_num, language_cd, race_cd, marital_status_cd, religion_cd, zip_cd, statecityzip_path, patient_blob FROM patient_dimension WHERE patient_num = ?", new Object[]{patientNum}, new RowMapper() {

				public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException {
					PatientDb patient = new PatientDb();
					patient.setBirthdateDb(resultSet.getDate(2));
					patient.setDeathdateDb(resultSet.getDate(3));
					patient.setSex(resultSet.getString(4));
					patient.setLanguage(resultSet.getString(6));
					patient.setRace(resultSet.getString(7));
					patient.setMaritalStatus(resultSet.getString(8));
					patient.setReligion(resultSet.getString(9));
					return patient;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			System.out.println(String.format("Failed query: SELECT vital_status_cd, birth_date, death_date, sex_cd, age_in_years_num, language_cd, race_cd, marital_status_cd, religion_cd, zip_cd, statecityzip_path, patient_blob FROM patient_dimension WHERE patient_num = %s", patientNum));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void persistObservation(Observation observation) {
		System.out.println("  Observation ==> " + observation.getConceptCode());
		message = MessageFormat.format(App.messages.getString("app.parser.status.observations"), " (" + ++importCounter + ")");
		int patientNum = getPatientNumFromPatientMapping(observation.getPatientId(), sourceId);
		int encounterNum = getEncounterNumFromEncounterMapping(observation.getEncounterId(), observation.getPatientId(), sourceId);
		Object[] paramsNumeric = new Object[]{encounterNum, patientNum, observation.getConceptCode(), observation.getProviderId(), ImportUtils.formatDate(observation.getDatetime()), "@", (NumberUtils.isNumber(observation.getValue()) ?  "N" : "@"), (NumberUtils.isNumber(observation.getValue()) ?  "E" : "@"), observation.getValue(), "", "@", ImportUtils.formatDate(observation.getDatetime()), observation.getLocation(), sourceId};
		ImportUtils.executeUpdate(starSchemaJdbcTemplate, "INSERT INTO OBSERVATION_FACT (ENCOUNTER_NUM, PATIENT_NUM, CONCEPT_CD, PROVIDER_ID, START_DATE, MODIFIER_CD, VALTYPE_CD, TVAL_CHAR, NVAL_NUM, VALUEFLAG_CD, QUANTITY_NUM, INSTANCE_NUM, UNITS_CD, END_DATE, LOCATION_CD, CONFIDENCE_NUM, OBSERVATION_BLOB, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, ?, ?, ?, NULL, NULL, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", paramsNumeric);
	}

	private void persistConcept(Concept concept) {
		String metadataXml = "";

		if (concept.getDataType().equalsIgnoreCase("Numeric")) metadataXml = generateMetadataXml(concept);

		message = MessageFormat.format(App.messages.getString("app.parser.status.concepts"), " (" + ++importCounter + ")");
		String path = ImportUtils.formatPath(concept.getPath(), metadataTableAccess, sourceId);

		System.out.println("  Concept ==> " + concept.getName() + " :: " + path);

		//		if (concept.getName().equals("METHOD OF FAMILY PLANNING")) {
		//			System.out.println("stop here...");
		//		}
		//		

		if (concept.getSynonym() == null || !concept.getSynonym().equalsIgnoreCase("true")) {
			if (!conceptDimensionExists(path)) {
				Object[] params = new Object[]{path, concept.getCode(), concept.getDescription().length() > 900 ? concept.getDescription().substring(0, 900) : concept.getDescription(), sourceId};
				ImportUtils.executeUpdate(starSchemaJdbcTemplate, "INSERT INTO CONCEPT_DIMENSION (CONCEPT_PATH, CONCEPT_CD, NAME_CHAR, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID) VALUES (?, ?, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", params);
			}

			if (!conceptExists(path, concept.getName(), concept.getCode())) {
				Object[] params = new Object[]{computeLevel(path), path, concept.getName(), "N", concept.getCode(), metadataXml, path, concept.getDescription().length() > 900 ? concept.getDescription().substring(0, 900) : concept.getDescription(), sourceId};					
				ImportUtils.executeUpdate(metadataJdbcTemplate, String.format("INSERT INTO %s (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (?, ?, ?, ?, 'LA', NULL, ?, ?, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", metadataTableAccess.getTableName()), params);
			}
		} else {
			if (!conceptExists(path, concept.getName(), concept.getCode())) {
				Object[] params = new Object[]{computeLevel(path), path, concept.getName(), "Y", concept.getCode(), metadataXml, path, concept.getDescription().length() > 900 ? concept.getDescription().substring(0, 900) : concept.getDescription(), sourceId};					
				ImportUtils.executeUpdate(metadataJdbcTemplate, String.format("INSERT INTO %s (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_TOTALNUM, C_BASECODE, C_METADATAXML, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_COMMENT, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, VALUETYPE_CD) VALUES (?, ?, ?, ?, 'LA', NULL, ?, ?, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', ?, NULL, ?, SYSDATE, SYSDATE, SYSDATE, ?, NULL)", metadataTableAccess.getTableName()), params);
			}
		}
	}

	private boolean conceptSourceExists(String name) {
		try {
			return metadataJdbcTemplate.queryForInt("SELECT 1 FROM schemes WHERE c_name = ?", new Object[]{name}) == 1 ? true : false;
		} catch (EmptyResultDataAccessException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean conceptExists(String path, String name, String basecode) {
		try {
			return metadataJdbcTemplate.queryForInt(String.format("SELECT 1 FROM %s WHERE c_fullname = ? AND c_name = ? AND c_basecode = ?", metadataTableAccess.getTableName()), new Object[]{path, name, basecode}) == 1 ? true : false;
		} catch (EmptyResultDataAccessException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean conceptDimensionExists(String path) {
		try {
			return starSchemaJdbcTemplate.queryForInt("SELECT 1 FROM CONCEPT_DIMENSION WHERE concept_path = ?", new Object[]{path}) == 1 ? true : false;
		} catch (EmptyResultDataAccessException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private int generatePatientNum() {
		return starSchemaJdbcTemplate.queryForInt("SELECT SQ_UP_PATDIM_PATIENTNUM.NEXTVAL FROM dual");
	}

	private int generateEncounterNum() {
		return starSchemaJdbcTemplate.queryForInt("SELECT SQ_UP_ENCDIM_ENCOUNTERNUM.NEXTVAL FROM dual");
	}

	private String generateMetadataXml(Concept concept) {

		try {
			JAXBContext context = JAXBContext.newInstance(ValueMetadata.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

			ValueMetadata valueMetadata = new ValueMetadata(); 
			valueMetadata.setVersion(new BigDecimal("3.02"));
			valueMetadata.setCreationDateTime(new Date().toString());
			valueMetadata.setTestID(concept.getId());
			valueMetadata.setTestName(concept.getName());
			valueMetadata.setDataType(getMetadataDataType(concept));
			valueMetadata.setCodeType("GRP");
			valueMetadata.setLoinc(getLoincCode(concept));
			valueMetadata.setFlagstouse(getFlagstouse(concept));
			valueMetadata.setOktousevalues("Y");
			valueMetadata.setMaxStringLength(null);
			valueMetadata.setLowofLowValue(getRangeValue(concept.getLowAbsolute()));
			valueMetadata.setHighofLowValue(getRangeValue(concept.getLowCritical()));
			valueMetadata.setLowofHighValue(getRangeValue(concept.getHiNormal()));
			valueMetadata.setHighofHighValue(getRangeValue(concept.getHiCritical()));

			UnitValues unitValues = new UnitValues();
			unitValues.addNormalUnit(concept.getUnits());
			unitValues.addEqualUnit(concept.getUnits());
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	private BigInteger getRangeValue(BigDecimal rangeValue) {
		// TODO: this should maybe return a decimal value but the XSD needs to change to accommodate
		return rangeValue != null ? new BigInteger("" + rangeValue.intValue()) : null;
	}

	private String getLoincCode(Concept concept) {
		if (concept.getSource() != null) {
			for (Source source : concept.getSource()) {
				if (source.getId().equals(loincSourceId)) return source.getCode();
			}
		}
		return "";
	}

	private String getFlagstouse(Concept concept) {
		String flagsToUse = "";

		if (concept.getHiAbsolute() != null || concept.getLowAbsolute() != null) flagsToUse += "H"; 
		if (concept.getHiNormal() != null || concept.getLowNormal() != null) flagsToUse += "N";
		if (concept.getHiCritical() != null || concept.getLowCritical() != null) flagsToUse += "L";

		return flagsToUse;
	}

	private String getMetadataDataType(Concept concept) {
		/*
		 * Possible DataTypes:
		 * 
		 * Integer � domain of all integers
		 * PosFloat � domain of all positive real numbers
		 * Float � domain of all real numbers 
		 * Enum � domain of enumerated values
		 * String � domain of free text, NOT enumerated text values, which would be the Enum data type.
		 */
		if (concept.getPrecision() == null) return "Integer";
		return concept.getPrecision().intValue() == 0 ? "Integer" : "Float"; 
	}


	public Parser(String importPath) {
		this.setImportPath(importPath);
	}

	public O2I2B2 getO2i2b2Object() {
		O2I2B2 o2i2b2 = new O2I2B2();

		try {
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileReader(importPath));
			xmlStreamReader.nextTag();
			xmlStreamReader.require(START_ELEMENT, null, "o2i2b2");

			o2i2b2.setOrgCode(xmlStreamReader.getAttributeValue("", "orgCode"));
			o2i2b2.setOrgName(xmlStreamReader.getAttributeValue("", "orgName"));
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return o2i2b2;
	}

	public void setImportPath(String importPath) {
		this.importPath = importPath;
	}

	public String getImportPath() {
		return importPath;
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

	public O2I2B2 getO2i2b2() {
		return o2i2b2;
	}

	public void setO2i2b2(O2I2B2 o2i2b2) {
		this.o2i2b2 = o2i2b2;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getStep() {
		return step;
	}

	public void setMetadataTableAccess(MetadataTableAccess metadataTableAccess) {
		this.metadataTableAccess = metadataTableAccess;
	}

	public MetadataTableAccess getMetadataTableAccess() {
		return metadataTableAccess;
	}

}
