package org.o2i2b2.loader;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.junit.Test;
import org.o2i2b2.dao.objects.MetadataTableAccess;
import org.o2i2b2.utils.LogHelper;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;

public class ImportUtils {

	private transient static Logger logger = Logger.getLogger("org.o2i2b2.loader.ImportUtils");

	private static SimpleDateFormat datetimeFormatBdChar = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static SimpleDateFormat datetimeFormatBdNum = new SimpleDateFormat("yyyyMMdd.HHmm");

	private static SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static Pattern datetimePattern = Pattern.compile("^\\d{4}[-]\\d{2}[-]\\d{2}[ ]\\d{2}[:]\\d{2}[:]\\d{2}$");
	private static Pattern datePattern = Pattern.compile("^\\d{4}[-]\\d{2}[-]\\d{2}$");
	
	public static final int EXECUTE_SQL_SUCCESS = 0;
	public static final int EXECUTE_SQL_ERROR = 1;
	public static final int EXECUTE_SQL_FAILURE = 2;
	
	public static String getSourceId(String orgCode) {
		return orgCode.trim().toUpperCase().replaceAll("[\\W]", "_");
	}

	@Test
	public void testStripSourceIdPrefix() {
		String test = stripSourceIdPrefix("\\test\\drug", "test");  
		System.out.println(test);
		assertTrue(test.equals("drug"));  
	}
	
	public static String formatPath(String path, MetadataTableAccess metadataTableAccess, String sourceId) {
		path = stripSourceIdPrefix(path, sourceId);
		return (metadataTableAccess.getPath() + path).replaceAll("\\\\\\\\", "\\\\");
	}
	
	private static String stripSourceIdPrefix(String path, String sourceId) {
		String pathParts[] = path.split("\\\\");
		if (pathParts.length > 2) {
			if (ImportUtils.getSourceId(pathParts[1]).equals(sourceId)) {
				StringBuilder newPath = new StringBuilder();
				for (int i = 2;i < pathParts.length; i++) newPath.append(pathParts[i] + "\\");
				return newPath.toString();
			} else {
				StringBuilder newPath = new StringBuilder();
				for (int i = 1;i < pathParts.length; i++) newPath.append(pathParts[i] + "\\");
				return newPath.toString();
			}
			
		} else if (pathParts.length == 2 && pathParts[1].equals(sourceId)) {
			return "";
		}
		return path;
	}
	
	public static int formatInt(String number) {
		try {
			return new Integer(number);
		} catch (Exception e) {}
		return -1;
	}
	
	public static String formatAge(Date birthdate) {
		return formatAge(getAgeFromDate(null, birthdate));
	}
	
	public static String formatAge(Date snapshotDate, Date birthdate) {
		return formatAge(getAgeFromDate(snapshotDate, birthdate));
	}
	
	public static String formatAge(int age) {
		return "DEM|AGE:" + age;
	}
	
	public static int getAgeFromDate(Date snapshotDate, Date birthdate) {
		try {
			Calendar dateOfBirth = new GregorianCalendar();
			dateOfBirth.setTime(birthdate);
			Calendar asOfDate = Calendar.getInstance();
			
			if (snapshotDate != null) asOfDate.setTime(snapshotDate);
			
			int age = asOfDate.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
			dateOfBirth.add(Calendar.YEAR, age);
			if (asOfDate.before(dateOfBirth)) age--;
			return age;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static String formatSexCd(String sex) {
		if (sex != null) {
			if (sex.equalsIgnoreCase("f")) return "DEM|SEX:f";
			else if (sex.equalsIgnoreCase("m")) return "DEM|SEX:m";
		}
		return "DEM|SEX:u";
	}
	
	public static String formatVitalCd(Date deathdate) {
		return deathdate == null ? "DEM|VITAL:n" : "DEM|VITAL:y";
	}
	
	public static String formatVitalCd(boolean aliveInd) {
		return aliveInd ? "DEM|VITAL:n" : "DEM|VITAL:y";
	}
	

	
	public static Date formatDate(String dateString) {
		if (dateString != null && dateString.trim().length() >= 10) {
			try {
				if (datetimePattern.matcher(dateString).matches()) {
					return datetimeFormat.parse(dateString);
				} else if (datePattern.matcher(dateString).matches()) {
					return dateFormat.parse(dateString);
				}
			} catch (Exception e) {
				logger.error(new LogHelper(e));
			}
		}
		return null;
	}
	
	public static String formatDateBdChar(Date birthdate) {
		return birthdate != null ? datetimeFormatBdChar.format(birthdate) : null;
	}
	
	public static String formatDateBdNum(Date birthdate) {
		return birthdate != null ? datetimeFormatBdNum.format(birthdate) : null;
	}
	
	
	public static int executeUpdate(JdbcTemplate jdbcTemplate, String sql, Object[] params) {
		try {
			if (logger.isEnabledFor(Priority.DEBUG)) {
				logger.debug("executeUpdate: sql: " + sql);
				if (params != null) {
					int paramCounter = 0;
					for (Object param : params) {
						logger.debug("executeUpdate: param[" + paramCounter++ + "]: " + param);
					}
				}
			}
			jdbcTemplate.update(sql, params);
			return EXECUTE_SQL_SUCCESS;
		} catch (UncategorizedSQLException e) {
			if (e.getMessage().indexOf("ORA-01653") > 0 || e.getMessage().indexOf("ORA-12952") > 0) { // ORA-01653: unable to extend table [x] in tablespace...  or ORA-12952: The request exceeds the maximum allowed database size of 4 GB
				System.out.println("abort import...");
				ImportData.setFatalError(true);
				ImportData.setFatalErrorMessage(e.getMessage());
				return EXECUTE_SQL_FAILURE;
			}
		} catch (Exception e) {
			logger.error("executeUpdate: sql: " + sql);
			if (params != null) {
				int paramCounter = 0;
				for (Object param : params) {
					logger.error("executeUpdate: param[" + paramCounter++ + "]: " + param);
				}
			}
			logger.error(new LogHelper(e));
		}
		return EXECUTE_SQL_ERROR;
	}
}
