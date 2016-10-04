/**
 * 
 */
package com.sivalabs.jpcg.core.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Siva
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TableConfig {
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String singularClassName;
	@XmlAttribute
	private String pluralClassName;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSingularClassName() {
		return singularClassName;
	}
	public void setSingularClassName(String singularClassName) {
		this.singularClassName = singularClassName;
	}
	public String getPluralClassName() {
		return pluralClassName;
	}
	public void setPluralClassName(String pluralClassName) {
		this.pluralClassName = pluralClassName;
	}
	
}
