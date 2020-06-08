package kathy.proyectofinal.progra1;

import java.awt.EventQueue;
import java.io.File;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;

public class FrmProy extends JFrame {
	Scanner sc = new Scanner(System.in);
	RandomAccessFile Archivo;
	RandomAccessFile Entidad;
	RandomAccessFile Atributo;
	private List<Entidad>lista_Entidad = new ArrayList<>();
	private final int totalBytes = 83 , bytesEntidad = 47 , bytesAtributo = 43;
	private final static String formatoFecha = "dd/MM/yyyy";
	static DateFormat format = new SimpleDateFormat(formatoFecha);
	private JPanel contentPane;
	private JTable tbData;
	private DefaultTableModel tableModel;	

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrmProy frame = new FrmProy();
					frame.setVisible(true);
					if(frame.Abrir_Archivo()) {
						frame.menuDefinicion(true);
					}else {
						frame.menuDefinicion(false);
					}
					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private boolean Abrir_Archivo() {
		boolean respuesta = false;
		try {
			Entidad = new RandomAccessFile("Entidad.txt","rw");
			Atributo = new RandomAccessFile("Atributo.txt", "rw");
			
			long longitud = Entidad.length();
			if(longitud <= 0) {
				System.out.println("El archivo se encuentra vacio");
				respuesta = false;
			}
			if(longitud >= bytesEntidad) {
				
				Entidad.seek(0);
				Entidad e;
				while(longitud >= bytesEntidad) {
					e = new Entidad();
					e.setIndice(Entidad.readInt());
					byte[] bNombre = new byte[30];
					Entidad.read(bNombre);
					e.setBytesNombre(bNombre);
					e.setCantidad(Entidad.readInt());
					e.setBytes(Entidad.readInt());
					e.setPosicion(Entidad.readLong());
					Entidad.readByte();
					longitud -= bytesEntidad;
					
					long longitudAtributo = Atributo.length();
					if(longitudAtributo <=0) {
						System.out.println("No hay registros");
						respuesta = false;
						break;
					}
					Atributo.seek(e.getPosicion());
					Atributo a;
					longitudAtributo = e.getCantidad() * bytesAtributo;
					while(longitudAtributo >= bytesAtributo) {
						a = new Atributo();
						a.setIndice(Atributo.readInt());
						byte[]bNombreAtributo = new byte[30];
						Atributo.read(bNombreAtributo);
						a.setBytesNombre(bNombreAtributo);
						a.setValorTipoDato(Atributo.readInt());
						a.setLongitud(Atributo.readInt());
						a.setNombreTipoDato();
						Atributo.readByte();
						e.setAtributo(a);
						longitudAtributo -= bytesAtributo;
					}
					lista_Entidad.add(e);
				}
				if(lista_Entidad.size()>0) {
					respuesta = true;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return respuesta;
	}

	private void cerrarArchivo() throws Exception{
		if(Archivo != null && Entidad != null && Atributo != null) {
			Archivo.close();
			Entidad.close();
			Atributo.close();
		}
	}
	
	private boolean Grabar_Registro(Entidad entidad){
		boolean resultado = false;
		try {
			Archivo.seek(Archivo.length());
			boolean valido;
			byte[]bytesString;
			String tmpString = "";
			for(Atributo atributo : entidad.getAtributo()) {
				valido = false;
				System.out.println("Ingrese "+ atributo.getNombre().trim());
				while(!valido) {
					try {
						switch (atributo.getTipoDato()) {
						case INT:
							int tmpInt = sc.nextInt();
							Archivo.writeInt(tmpInt);
							sc.nextLine();
							break;
						case LONG:
							long tmpLong = sc.nextLong();
							Archivo.writeLong(tmpLong);
							break;
						case STRING:
							int longitud = 0;
							do {
								tmpString = sc.nextLine();
								longitud = tmpString.length();
								if(longitud <= 1| longitud > atributo.getLongitud()) {
									System.out.println("La longitud de " + atributo.getNombre().trim()
											+ " no es valida [1 - "+atributo.getLongitud()+"]");
								}
							}while(longitud <=0 || longitud > atributo.getLongitud());
							
							bytesString = new byte[atributo.getLongitud()];
							
							for(int i = 0; i < tmpString.length(); i++) {
								bytesString[i] = (byte) tmpString.charAt(i);
							}
							Archivo.write(bytesString);
							break;
						case DOUBLE:
							double tmpDouble = sc.nextDouble();
							Archivo.writeDouble(tmpDouble);
							break;
						case FLOAT:
							float tmpFloat = sc.nextFloat();
							Archivo.writeFloat(tmpFloat);
							break;
						case DATE:
							Date date = null;
							tmpString = "";
							while (date == null) {
								System.out.println("Formato de fecha: " +formatoFecha);
								tmpString = sc.nextLine();
								date = strintToDate(tmpString);
							}
							bytesString = new byte[atributo.getBytes()];
							for (int i = 0; i < tmpString.length(); i++) {
								bytesString[i] = (byte) tmpString.charAt(i);
							}
							Archivo.write(bytesString);
							break;
						case CHAR:
							do {
								tmpString = sc.nextLine();
								longitud = tmpString.length();
								if(longitud < 1 || longitud > 1) {
									System.out.println("Solo se permite un caracter");
								}
							}while(longitud < 1 || longitud > 1);
								byte caracter = (byte) tmpString.charAt(0);
								Archivo.writeByte(caracter);
								break;
							}
							valido = true;
						}catch(Exception e) {
							System.out.println(
									"Error "+e.getMessage()+ " al capturar tipo de dato, vuelva a ingresar el valor: ");
							sc.hasNextLine();
						}
					}
				}
				Archivo.write("\n".getBytes());
				resultado = true;
			}catch(Exception e) {
				resultado = false;
				System.out.println("Error al agregar el registro "+e.getMessage());
			}
			return resultado;
		}
	
	public Date strintToDate(String strFecha) {
		Date date = null;
		try {
			date = format.parse(strFecha);
		} catch (Exception e) {
			date = null;
			System.out.println("Error en fecha: " + e.getMessage());
		}
		return date;
	}
	
	public String dateToString(Date date) {
		String strFecha;
		strFecha = format.format(date);
		return strFecha;
	}
	
	private String formarNombreFichero(String nombre) {
		return nombre.trim() + ".dat";
	}
	
	private boolean Ingresar_Entidad() {
		boolean resultado = false;
		String auxNombre;
		int longitud = 0;
		//sc.nextLine();
		try {
			Entidad entidad = new Entidad();
			entidad.setIndice(lista_Entidad.size() +1);
			do {
				auxNombre = JOptionPane.showInputDialog(null,"Ingrese el nombre de la entidad");
				longitud = auxNombre.length();
				if(longitud<2 || longitud > 30) {
					JOptionPane.showMessageDialog(null, "La cantidad de los caracteres no es correcta(3 - 30)");
				}else {
					if(auxNombre.contains(" ")){
						JOptionPane.showMessageDialog(null, "El nombre no puede contener espacios, sustituya por guion bajo(underscore)");
						longitud = 0;
					}
				}
			}while (longitud < 2 || longitud > 30);
			entidad.setNombre(auxNombre);
			JOptionPane.showMessageDialog(null,"Atributo de la entidad");
			int bndDetener = 0;
			do {
				Atributo atributo = new Atributo();
				atributo.setIndice(entidad.getIndice());
				longitud = 0;
				auxNombre = JOptionPane.showInputDialog(null, "Escriba el nombre del atributo No. "+(entidad.getCantidad()+1));
				do {
					
					longitud = auxNombre.length();
					if(longitud < 2 || longitud > 30) {
						JOptionPane.showMessageDialog(null, "La cantida de los caracteres no es correcta (3-30)");
					}else {
						if(auxNombre.contains(" ")) {
							JOptionPane.showMessageDialog(null, "El nombre no puede contener espacios, sustituya por guion bajo");
							longitud = 0;
						}
					}
				}while ( longitud < 2 || longitud > 30);
				atributo.setNombre(auxNombre);
				int valor = Integer.parseInt(JOptionPane.showInputDialog(null, "Seleccione el tipo de dato:"
						+"\n"+TipoDato.INT.getValue()+".........."+TipoDato.INT.name()
						+"\n"+TipoDato.LONG.getValue()+".........."+TipoDato.LONG.name()
						+"\n"+TipoDato.STRING.getValue()+".........."+TipoDato.STRING.name()
						+"\n"+TipoDato.DOUBLE.getValue()+".........."+TipoDato.DOUBLE.name()
						+"\n"+TipoDato.FLOAT.getValue()+".........."+TipoDato.FLOAT.name()
						+"\n"+TipoDato.DATE.getValue()+".........."+TipoDato.DATE.name()
						+"\n"+TipoDato.CHAR.getValue()+".........."+TipoDato.CHAR.name()));
				atributo.setValorTipoDato(valor);
				if(atributo.isRequiereLongitud()) {
					int lg = Integer.parseInt(JOptionPane.showInputDialog(null,"Ingrese la longitud"));
					atributo.setLongitud(lg);
				}else {
					atributo.setLongitud(0);
				}
				atributo.setNombreTipoDato();
				entidad.setAtributo(atributo);
				bndDetener = Integer.parseInt(JOptionPane.showInputDialog(null," Si desea agregar otro atributo ingrese cualquier numero"
						+ " de lo cantrario 0"));
			}while(bndDetener != 0);
			JOptionPane.showMessageDialog(null,"Los datos a registrar son: ");
			Mostrar_Entidad(entidad);
			longitud = Integer.parseInt(JOptionPane.showInputDialog(null,"Presione 1 para guardar 0 para cancelar"));
			
			if (longitud ==1 ) {			
				entidad.setPosicion(Atributo.length());
				Atributo.seek(Atributo.length());
				for(Atributo atributo : entidad.getAtributo()) {
					Atributo.writeInt(atributo.getIndice());;
					Atributo.write(atributo.getBytesNombre());
					Atributo.writeInt(atributo.getValorTipoDato());
					Atributo.writeInt(atributo.getLongitud());
					Atributo.write("\n".getBytes());
				}
				Entidad.writeInt(entidad.getIndice());
				Entidad.write(entidad.getBytesNombre());
				Entidad.writeInt(entidad.getCantidad());
				Entidad.writeInt(entidad.getBytes());
				Entidad.writeLong(entidad.getPosicion());
				Entidad.write("\n".getBytes());
				lista_Entidad.add(entidad);
				resultado = true;
			} else {
				JOptionPane.showMessageDialog(null, "Entidad no guardada");
				resultado = false;
			}
		}catch(Exception e) {
			resultado = false;
			e.printStackTrace();
		}
		return resultado;
	}
	
	private void Modificar_Entidad() {
		try {
			int indice = 0;
			while (indice < 1 || indice > lista_Entidad.size()) {
				for(Entidad entidad : lista_Entidad) {
					JOptionPane.showInputDialog(null,entidad.getIndice()+ "........" + entidad.getNombre());
					//System.out.println(entidad.getIndice() +" ........ " + entidad.getNombre());
				}
				System.out.println("Seleccione la entidad que desea modificar");
				indice = sc.nextInt();
				sc.nextLine();
			}
			Entidad entidad = new Entidad();
			for(Entidad e : lista_Entidad) {
				if(indice == e.getIndice()) {
					entidad = e;
					break;
				}
			}
			String nombreFichero = formarNombreFichero(entidad.getNombre());
			Archivo = new RandomAccessFile(nombreFichero, "rw");
			long longitudDatos = Archivo.length();
			
			if(longitudDatos >0) {
				System.out.println("No es posible modificar la entidad debido a que ya tiene datos asociados");
			}else {
				
				boolean bndEncontrado = false, bndModificado = false;
				
				Entidad.seek(0);
				long longitud = Entidad.length();
				int registros = 0, salir = 0, i;
				Entidad e;
				byte[] tmpBytes;
				Archivo.close();
				while(longitud > totalBytes) {
					e= new Entidad();
					e.setIndice(Entidad.readInt());
					tmpBytes = new byte[30];
					Entidad.read(tmpBytes);
					e.setBytesNombre(tmpBytes);
					e.setCantidad(Entidad.readInt());
					e.setBytes(Entidad.readInt());
					e.setPosicion(Entidad.readLong());
					if(entidad.getIndice() == e.getIndice()) {
						System.out.println("Si no desea modificar el campo presione enter");
						System.out.println("Ingrese el nombre");
						String tmpStr;
						sc.nextLine();
						int len = 0;
						long posicion;
						do {
							tmpStr = sc.nextLine();
							len = tmpStr.length();
							if(len == 1 || len > 30) {
								System.out.println("La longitud del nombre no es valida [2-30");
							}
						}while (len == 1 || len > 30);
						if(len > 0) {
							e.setNombre(tmpStr);
							posicion = registros * totalBytes;
							Archivo.seek(posicion);
							
							
							Archivo.write(e.getBytesNombre());
							bndModificado = true;
						}
						for (Entidad el : lista_Entidad) {
							System.out.println("Modificando entidad" + e);
							System.out.println(el.getNombre().trim());
						}
						
						break;
					}
					registros++;
					
					longitud -= totalBytes;
				}
			}
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	private void Mostrar_Entidad(Entidad entidad) {
		
		System.out.println("Indice:" + entidad.getIndice());
		System.out.println("Nombre:" + entidad.getNombre());
		System.out.println("Cantidad de Atributo: "+entidad.getCantidad());
		System.out.println("Atributo:");
		int i = 1;
		for(Atributo atributo : entidad.getAtributo()) {
			System.out.println("\tNo. " +i);
			System.out.println("\tNombre: "+atributo.getNombre());
			System.out.println("\tTipo de dato: " +atributo.getNombreTipoDato());
			
			System.out.println("\tLongitud: " + atributo.getLongitud());
			tableModel.addRow(new Object[] {i, entidad.getNombre(),atributo.getNombre(), atributo.getNombreTipoDato(),atributo.getLongitud()});
			
			
			i++;
		}
	}
	
	public void listar_Registros(Entidad entidad) {
		try {
			long longitud = Archivo.length();
			if(longitud <= 0) {
				JOptionPane.showMessageDialog(null,"No hay registros");
				return;
			}
			Archivo.seek(0);
			byte[]tmpArrayByte;
			String linea = "";
			for(Atributo atributo : entidad.getAtributo()) {
				linea += atributo.getNombre().toString().trim()+ "\t\t";
				tableModel = new DefaultTableModel();
				tableModel.addColumn(linea);
			}
			System.out.println(linea);
			while (longitud >= entidad.getBytes()) {
				linea = "";
				for (Atributo atributo : entidad.getAtributo()) {
					switch (atributo.getTipoDato()) {
					case INT:
						int tmpInt = Archivo.readInt();
						linea += String.valueOf(tmpInt) + "\t\t";
						break;
					case LONG:
						long tmpLong = Archivo.readLong();
						linea += String.valueOf(tmpLong) + "\t\t";
						break;
					case STRING:
						tmpArrayByte = new byte[atributo.getLongitud()];
						Archivo.read(tmpArrayByte);
						String tmpString = new String(tmpArrayByte);
						linea += tmpString.trim() + "\t\t";
						break;
					case DOUBLE:
						double tmpDouble = Archivo.readDouble();
						linea += String.valueOf(tmpDouble) + "\t\t";
						break;
					case FLOAT:
						float tmpFloat = Archivo.readFloat();
						linea += String.valueOf(tmpFloat) + "\t\t";
						break;
					case DATE:
						tmpArrayByte = new byte[atributo.getBytes()];
						Archivo.read(tmpArrayByte);
						tmpString = new String(tmpArrayByte);
						linea += tmpString.trim() + "\t\t";
						break;
					case CHAR:
						char tmpChar = (char) Archivo.readByte();
						linea += tmpChar + "\t\t";
						break;
					}
				}
				Archivo.readByte();
				tableModel.addRow(new Object[] {linea });
				longitud -= entidad.getBytes();
				System.out.println(linea+ " " +longitud);
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	private boolean Borrar_Archivos() {
		boolean res = false;
		try {
			File file;
			for(Entidad entidad : lista_Entidad) {
				file = new File(entidad.getNombre().trim() + ".dat");
				if(file.exists()) {
					file.delete();
				}
				file = null;
			}
			file = new File("Atributo.txt");
			if(file.exists()) {
				file.delete();
			}
			file = null;
			file = new File("Entidad.txt");
			if(file.exists()) {
				file.delete();
			}
			file = null;
			res = true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private void iniciar(int indice) {
		int opcion = 0;
		String nombreFichero = "";
		try {
			Entidad entidad = null;
			for(Entidad e : lista_Entidad) {
				if (indice == e.getIndice()) {
					nombreFichero = (e.getNombre()+ ".data");
					entidad = e;
					break;
				}
			}
			Archivo = new RandomAccessFile(nombreFichero, "rw");
			System.out.println("Bienvenido (a)");
			Atributo a = entidad.getAtributo().get(0);
			do {
				try {
					System.out.println("Seleccione su opcion");
					System.out.println("1.\t\tAgregar");
					System.out.println("2.\t\tListar");
					System.out.println("3.\t\tBuscarr");
					System.out.println("4.\t\tModificar");
					System.out.println("0.\t\tMenu anterior");
					opcion = sc.nextInt();
					switch(opcion) {
					case 0:
						System.out.println("");
						break;
					case 1:
						Grabar_Registro(entidad);
						break;
					case 2:
						listar_Registros(entidad);
						break;
					case 3:
						System.out.println("Se hara la busqueda en la primera columna: ");
						System.out.println("Ingrese " + a.getNombre().trim() +"a buscar");
						break;
					case 4:
						System.out.println("Ingrese el carne a modificar: ");
						break;
						default:
							System.out.println("Opcion no valida");
							break;
					}
				}catch(Exception e) {
					System.out.println("Error: " +e.getMessage());
				}
			}while(opcion !=0);
			Archivo.close();
		}catch (Exception e) {
			System.out.println("Error: " +e.getMessage());
		}
	}

	private void menuDefinicion(boolean mostrarAgregarRegistro) throws Exception{
		int opcion = 1;
		while (opcion !=0) {
			opcion = Integer.parseInt(JOptionPane.showInputDialog(null, "Elija su opcion"
					+"\n1..........Agregar Entidad"
					+"\n2..........Modificar Entidad"
					+"\n3..........Listar Entidad"
					+"\n4..........Agregar Registros"
					+"\n5..........Borrar bases de datos"
					+"\n0..........Salir"));
			switch(opcion) {
			case 0:
				JOptionPane.showMessageDialog(null, "Gracias por usar la aplicacion");
				break;
			case 1:
				if(Ingresar_Entidad()) {
					JOptionPane.showMessageDialog(null, "Entidad agregada con exito");
					mostrarAgregarRegistro = true;
				}
				break;
			case 2:
				Modificar_Entidad();
				break;
			case 3:
				if(lista_Entidad.size() >0) {
					while(tableModel.getRowCount() > 0) {
						tableModel.removeRow(0);
					}
					int tmpInt = 0;
					tmpInt = Integer.parseInt(JOptionPane.showInputDialog(null, "Desea imprimir los detalles?"
							+ " Si, presione 1. No, presione 0?"));
					if(tmpInt == 1) {
						for (Entidad entidad : lista_Entidad) {
							Mostrar_Entidad(entidad);
						}
					}else {
						for (Entidad entidad : lista_Entidad) {
							System.out.println("Indice: " + entidad.getIndice());
							System.out.println("Nombre: " + entidad.getNombre());
							System.out.println("Cantidad de Atributo: " + entidad.getCantidad());
						}
					}
				}else {
					System.out.println("No hay Entidad registradas");
				}
				break;
			case 4:
				int indice = 0;
				while (indice < 1 || indice > lista_Entidad.size()) {
					for(Entidad entidad : lista_Entidad) {
						Integer.parseInt(JOptionPane.showInputDialog(null,entidad.getIndice()+"......." + entidad.getNombre()));
					}
					System.out.println("Seleccione la entidad que desea trabajar");
					indice = sc.nextInt();
				}
				iniciar(indice);
				break;
			case 5:
				int confirmar = 0;
				confirmar = Integer.parseInt(JOptionPane.showInputDialog(null, "Esta seguro de querer borrar la base de datos, de ser si presione 1 de lo contrario 0"));
				if(confirmar == 1) {
					cerrarArchivo();
					if(Borrar_Archivos()) {
						lista_Entidad = null;
						lista_Entidad = new ArrayList<>();
						mostrarAgregarRegistro = false;

						System.out.println("Archivos borrados");
					}
				}
				break;
				default:
				System.out.println("Opcion no valida");
				break;
			}
		}
	}
	
	public FrmProy() {
		setTitle("Kathy Galicia");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 561, 344);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(245, 222, 179));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		tableModel = new DefaultTableModel();
		tableModel.addColumn("INDICE");
		tableModel.addColumn("ENTIDAD");
		tableModel.addColumn("NOMBRE");
		tableModel.addColumn("DATO");
		tableModel.addColumn("LONGITUD");
		tbData = new JTable();
		tbData.setModel(tableModel);
		JScrollPane scrollPane = new JScrollPane(tbData);
		scrollPane.setBounds(10, 81, 525, 213);
		contentPane.add(scrollPane);
		
		JLabel lblNewLabel = new JLabel("BASO DE DATOS");
		lblNewLabel.setFont(new Font("Cooper Black", Font.PLAIN, 30));
		lblNewLabel.setBounds(143, 11, 293, 55);
		contentPane.add(lblNewLabel);
	}
}
