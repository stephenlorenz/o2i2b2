package org.o2i2b2.dao;

public class ConceptNumeric {

	private int conceptId;
	private Double hiAbsolute;
	private Double hiCritical;
	private Double hiNormal;
	private Double lowAbsolute;
	private Double lowCritical;
	private Double lowNormal;
	private String units;
	private int precise;
	public int getConceptId() {
		return conceptId;
	}
	public void setConceptId(int conceptId) {
		this.conceptId = conceptId;
	}
	public Double getHiAbsolute() {
		return hiAbsolute;
	}
	public void setHiAbsolute(Double hiAbsolute) {
		this.hiAbsolute = hiAbsolute;
	}
	public Double getHiCritical() {
		return hiCritical;
	}
	public void setHiCritical(Double hiCritical) {
		this.hiCritical = hiCritical;
	}
	public Double getHiNormal() {
		return hiNormal;
	}
	public void setHiNormal(Double hiNormal) {
		this.hiNormal = hiNormal;
	}
	public Double getLowAbsolute() {
		return lowAbsolute;
	}
	public void setLowAbsolute(Double lowAbsolute) {
		this.lowAbsolute = lowAbsolute;
	}
	public Double getLowCritical() {
		return lowCritical;
	}
	public void setLowCritical(Double lowCritical) {
		this.lowCritical = lowCritical;
	}
	public Double getLowNormal() {
		return lowNormal;
	}
	public void setLowNormal(Double lowNormal) {
		this.lowNormal = lowNormal;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public int getPrecise() {
		return precise;
	}
	public void setPrecise(int precise) {
		this.precise = precise;
	}
	
	
}
