package comun;

import java.util.Date;

public class Archivo {
	private String fileName;
	private Date fechaModificacion;
	private Date fechaCreacion;
	private EstadoArchivo estado;
	
	public Archivo(String fileName, Date fechaModificacion, Date fechaCreacion, EstadoArchivo estado) {
		this.fileName = fileName;
		this.fechaModificacion = fechaModificacion;
		this.fechaCreacion = fechaCreacion;
		this.estado = estado;
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
	public Date getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public EstadoArchivo getEstado() {
		return estado;
	}
	public void setEstado(EstadoArchivo estado) {
		this.estado = estado;
	}
	
	
}
