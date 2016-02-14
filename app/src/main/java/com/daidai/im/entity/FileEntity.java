package com.daidai.im.entity;

public class FileEntity {
	String file_name;
	int file_length;
	String file_suffix;
	//int file_id;
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public int getFile_length() {
		return file_length;
	}
	public void setFile_length(int file_length) {
		this.file_length = file_length;
	}
	public String getFile_suffix() {
		return file_suffix;
	}
	public void setFile_suffix(String file_suffix) {
		this.file_suffix = file_suffix;
	}

	/*public int getFile_id() {
		return file_id;
	}

	public void setFile_id(int file_id) {
		this.file_id = file_id;
	}*/
}
