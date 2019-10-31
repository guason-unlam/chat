package com.unlam.chat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class WriteFile {
	PrintWriter pw;
	FileOutputStream arch;

	public WriteFile(String dest, Boolean append) throws FileNotFoundException {
		arch = new FileOutputStream(new File(dest));
		pw = new PrintWriter(arch, append);
	}

	public void writeToFile(String msg) {
		pw.println(msg);
	}

}
