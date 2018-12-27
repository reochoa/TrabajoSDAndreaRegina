package comun;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Archivo {
	private String fileName;
	private Date fechaModificacion;
	private EstadoArchivo estado;
	private String hash;

	public Archivo(String fileName, Date fechaModificacion, EstadoArchivo estado, String hash) {
		super();
		this.fileName = fileName;
		this.fechaModificacion = fechaModificacion;
		this.estado = estado;
		this.hash = hash;
	}
	
	public Archivo(String fileName, Date fechaModificacion, String hash) {
		super();
		this.fileName = fileName;
		this.fechaModificacion = fechaModificacion;
		this.estado = EstadoArchivo.nuevo;
		this.hash = hash;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public EstadoArchivo getEstado() {
		return estado;
	}

	public void setEstado(EstadoArchivo estado) {
		this.estado = estado;
	}

	public static Map<String, Archivo> leerArchivos(String path) {
		Map<String, Archivo> archivos = new HashMap<>();
		try {
			File carpetaPersonal = new File(path);
			if (carpetaPersonal.isDirectory() && carpetaPersonal.canRead()) {
				List<File> files = Arrays.asList(carpetaPersonal.listFiles());
				// Hash, algoritmo SHA-1 (identificador unico del archivo)
				MessageDigest messageDigestSha = MessageDigest.getInstance("SHA-1");

				byte[] buffer = new byte[1024];
				for (File file : files) {
					FileInputStream fis = new FileInputStream(file);
					int nread = 0;
					while ((nread = fis.read(buffer)) != -1) {
						messageDigestSha.update(buffer, 0, nread);
					}

					byte[] result = messageDigestSha.digest();
					StringBuffer sb = new StringBuffer();
					// Pasar HASH a base 64 (como se suele representar)
					for (int i = 0; i < result.length; i++) {
						sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
					}

					Archivo archivo = new Archivo(file.getName(), new Date(file.lastModified()), EstadoArchivo.nuevo,
							sb.toString());
					archivos.put(file.getName(), archivo);
				}
			}
		} catch (NoSuchAlgorithmException | IOException ex) {
			ex.printStackTrace();
		}
		finally {
			return archivos;
		}
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		stb.append("fileName: ").append(fileName);
		stb.append(", hash: ").append(hash);
		stb.append(", fecha: ").append(fechaModificacion);
		stb.append(", estado: ").append(estado);
		return stb.toString();
	}
	
	

}
