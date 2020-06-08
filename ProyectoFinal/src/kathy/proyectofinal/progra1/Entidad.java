package kathy.proyectofinal.progra1;

import java.util.ArrayList;
import java.util.List;

public class Entidad {
	
	private int indice;
	private String nombre;
	private int cantidad;
	private long posicion; //posicion donde inician sus Atributo
	private byte[] bytesNombre;
	private int bytes = 1; //inicia en uno que representa el cambio de linea
	
	private List<Atributo> Atributo;

	/**
	 * @return the indice
	 */
	public int getIndice() {
		return indice;
	}

	/**
	 * @param indice the indice to set
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
		bytesNombre = new byte[30]; //arreglo de bytes de longitud 30
		//convertir caracter por caracter a byte y agregarlo al arreglo
		for (int i = 0; i < nombre.length(); i++) {
			bytesNombre[i] = (byte)nombre.charAt(i);
		}
	}
	
	public byte[] getBytesNombre() {
		return bytesNombre;
	}
	
	public void setBytesNombre(byte[] bytesNombre) {
		this.bytesNombre = bytesNombre;
		nombre = new String(bytesNombre);
	}

	/**
	 * @return the cantidad
	 */
	public int getCantidad() {
		return cantidad;
	}

	/**
	 * @param cantidad the cantidad to set
	 */
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	/**
	 * @return the Atributo
	 */
	public List<Atributo> getAtributo() {
		return Atributo;
	}

	/**
	 * @param Atributo the Atributo to set
	 */
	public void setAtributo(List<Atributo> Atributo) {
		this.Atributo = Atributo;
	}
	
	public void setAtributo(Atributo atributo) {
		if (this.Atributo == null) {
			this.Atributo = new ArrayList<>();
		}
		this.Atributo.add(atributo);
		this.cantidad = this.Atributo.size();
	}
	
	public void removeAtributo(Atributo atributo) {
		if (this.Atributo != null) {
			if (this.Atributo.size() > 0) {
				this.Atributo.remove(atributo);
				this.cantidad = this.Atributo.size();
			}
		}
	}

	/**
	 * @return the posicion
	 */
	public long getPosicion() {
		return posicion;
	}

	/**
	 * @param posicion the posicion to set
	 */
	public void setPosicion(long posicion) {
		this.posicion = posicion;
	}
	
	/**
	 * @return the bytes
	 */
	public int getBytes() {	
		bytes = 1;
		for (Atributo atributo : Atributo) {
			bytes += atributo.getBytes();
		}
		return bytes;
	}
	
	public void setBytes(int bytes) {
		this.bytes = bytes;
	}

	public Object getAtributo() {
		// TODO Auto-generated method stub
		return null;
	}

}
