package org.javautil.oralit.text;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUtil {
	
	public static List<String> readAllLines(String filePath) throws IOException {
		  Charset charset = Charset.forName("ISO-8859-1");
		Path traceFilePath = Paths.get(filePath);
		List<String> lines = Files.readAllLines(traceFilePath, charset);
		return lines;
	}
	
	public static void emitStringList(List<String> lines, OutputStream os) throws IOException {
		 OutputStreamWriter bos = new OutputStreamWriter(os);
		  for (String line : lines) {
		        bos.write(line);
		  }
}

	public static String getAsString(String filePath) throws IOException {
			return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
	}
}
