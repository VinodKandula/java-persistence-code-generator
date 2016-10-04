/**
 * 
 */
package com.sivalabs.jpcg.core.metadata;

/**
 * @author Siva
 *
 */
public class ColumnMetadata {
	private String name;
	private String type;
	private int size;
	private boolean nullable;
	private int position;
	
	@Override
	public String toString() {
		return "Column [name=" + name + ", type=" + type + ", size=" + size + ", nullable=" + nullable + ", position="
				+ position + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public boolean isNullable() {
		return nullable;
	}
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
}
