package org.o2i2b2.objects;

public class ConceptSource {

	private int systemId;
	private String systemHl7Code;

	private String sourceName;
	private String sourceCode;

	public int getSystemId() {
		return systemId;
	}
	public void setSystemId(int systemId) {
		this.systemId = systemId;
	}
	public String getSystemHl7Code() {
		return systemHl7Code;
	}
	public void setSystemHl7Code(String systemHl7Code) {
		this.systemHl7Code = systemHl7Code;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public String getSourceCode() {
		return sourceCode;
	}
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}
	
}
