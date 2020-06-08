package kathy.proyectofinal.progra1;

import java.util.Date;

public class Usuario {

		private int id;
		
		private String nombre;
		private String telefono;
		private String correo;

		public Usuario() {

		}

		public Usuario(int id, String carne, String nombre, String telefono, String correo) {
			this.id = id;
			
			this.nombre = nombre;
			this.telefono = telefono;
			this.correo = correo;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public String getTelefono() {
			return telefono;
		}

		public void setTelefono(String telefono) {
			this.telefono = telefono;
		}

		public String getCorreo() {
			return correo;
		}

		public void setCorreo(String correo) {
			this.correo = correo;
		}

		public String toString() {
			return "Persona [id=" + id + ", carne: " +  ", nombre = " + nombre.trim() + ", telefono = " + telefono.trim() + ", correo = " + correo.trim() + "]";
		}

		public void setFechaNacimiento(Date date) {
			// TODO Auto-generated method stub
			
		}

		public byte[] getBytesFechaNacimiento() {
			// TODO Auto-generated method stub
			return null;
		}

		public Date getFechaNacimiento() {
			// TODO Auto-generated method stub
			return null;
		}

		public void setBytesFechaNacimiento(byte[] bFecha) {
			// TODO Auto-generated method stub
			
		}

		public void setBytesNombre(byte[] bNombre) {
			// TODO Auto-generated method stub
			
		}

		public void setCarne(int readInt) {
			// TODO Auto-generated method stub
			
		}

		public Object getCarne() {
			// TODO Auto-generated method stub
			return null;
		}

	}
