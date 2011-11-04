package uk.ac.ox.oucs.oxam.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PaperFileServiceImpl {

	private final static Log LOG = LogFactory.getLog(PaperFileServiceImpl.class);
	
	private Location location;
	
	public void setFileSystemLocation(Location location) {
		this.location = location;
	}
	
	public PaperFile get(String year, String term, String paperCode, String extension) {
		return new PaperFileImpl(year, term, paperCode, extension, location);
	}
	
	public boolean exists(PaperFile paperFile) {
		PaperFileImpl impl = castToImpl(paperFile);
		String path = impl.getPath();
		return new File(path).exists();
	}
	
	public void deposit(PaperFile paperFile, Callback<OutputStream> callback) {
		PaperFileImpl impl = castToImpl(paperFile);
		String path = impl.getPath();
		File file = new File(path);
		try {
			// Create containing directory
			createPath(file);
			file.createNewFile();
			callback.callback(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			LOG.error("Creation should have failed for: "+ file.getAbsolutePath(), e);
		} catch (IOException e) {
			LOG.error("Problem creation directory/file for: "+ file.getAbsolutePath(), e);
		}
	}

	private void createPath(File file) throws IOException {
		if (file.exists()) {
			return;
		}
		File directory = file.getParentFile();
		if (!directory.mkdirs() && !directory.exists()) {
			throw new IOException("Failed to create directory: "+ directory);
		}
	}

	private PaperFileImpl castToImpl(PaperFile paperFile) {
		if (!(paperFile instanceof PaperFileImpl)) {
			throw new IllegalArgumentException("PaperFile must have been retrieved from this service.");
		}
		return (PaperFileImpl)paperFile;
	}
}
