package Controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import java.lang.Math;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import java.util.TimerTask;
import static java.lang.Thread.currentThread;

import java.io.PrintWriter;

public class AltaController implements Initializable{

	@FXML
	public CheckBox Digital_O_1;
	public CheckBox Digital_O_2;
	public CheckBox Digital_O_3;
	public CheckBox Digital_O_4;
	public Button Boton_Graficar;
	public Button Boton_Stop;
	public Button Boton_Save;
	public Button Boton_Saveas;
	public Button Port_Boton;
	public RadioButton Analog_Boton;
	public RadioButton Digital_Boton;
	public LineChart<String,Number> Grafica;
	public ComboBox<String> Opciones;
	public ComboBox<String> Opciones_D;
	public ComboBox<String> Opciones_Puertos;
	public TextField Text_Muestreo;
	public TextField Text_Ventana;
	public TextField Saveas_Address;
	public TextField Saveas_Title;
	public Label Texto;
	public Label Reco;
	public ArrayList<Float> Valores = new ArrayList<Float>();
	public ScheduledExecutorService scheduledExecutorService;
	public SerialPort serial;
	public String Valor_Analog;
	public String Valor_Digital;
	public String Dato_Enviar;
	public String Dato_Analogico;
	public char LED_1;
	public char LED_2;
	public char LED_3;
	public char LED_4;
	UserThread userThread = new UserThread();
	boolean Graficando=false;
	boolean Port_Open = false;
	boolean Digital_Type;
	boolean Analog_Type;
	float Tiempo_Muestreo;
	int iterador = 0;
	int Ventana;
	XYChart.Series<String,Number> series = new XYChart.Series<String,Number>();
	
	
	/**
	 * This function is related with the Plot button, such as allow the execution of plotting the analog/digital informations.
	 * @throws InterruptedException
	 */	
	@FXML
	public void Graficar() throws InterruptedException{
		
		if(Port_Open == false){
			Reco.setText("Please connect a port.");
		}		
		else if(Text_Muestreo.getLength()>5){
			Reco.setText("Sample time has no a proper \nlength need to be less than 6.");
		}
		else if(!isFloat(Text_Muestreo.getText()) && !isInteger(Text_Muestreo.getText())){
			Reco.setText("Sample time cannot be a string.");
		}
		else if(Float.parseFloat(Text_Muestreo.getText())< 0.1){
			Reco.setText("Is not possible a sample time \n less than 0.1.");
		}
		else if(Text_Ventana.getLength()>3) {
			Reco.setText("Is not possible a window \n bigger than 999.");
		}
		else if(!isInteger(Text_Ventana.getText())){
			Reco.setText("Is not possible a window \n with strings or float.");
		}
		else if(Integer.parseInt(Text_Ventana.getText())<5){
			Reco.setText("Is not possible a window \n with a integer less than 5.");
		}
		else if(Digital_Type==false && Analog_Type == false){
			Reco.setText("You need select a type \n of signal.");
		}
		else {
						
			Ventana = Integer.parseInt(Text_Ventana.getText());
			iterador = 0;
			Valores.clear();
			series.getData().clear();
			Grafica.getData().clear();
			
			Boton_Graficar.setDisable(true);
			Boton_Save.setDisable(true);
			Boton_Saveas.setDisable(true);
			Boton_Stop.setDisable(false);
			
			Opciones.setDisable(true);
			Opciones_D.setDisable(true);
			Opciones_Puertos.setDisable(true);
			Analog_Boton.setDisable(true);
			Digital_Boton.setDisable(true);
			
			Text_Muestreo.setDisable(true);
			Text_Ventana.setDisable(true);
			Graficando = true;
			
			Tiempo_Muestreo = Float.parseFloat(Text_Muestreo.getText())*1000;
			
			if(Analog_Type == true) {
				
				Valor_Analog = Opciones.getValue();
				series.setName(Valor_Analog);
				
				if(Valor_Analog=="Analog Signal 1"){
					Dato_Analogico = "AI1";
				}
				else if(Valor_Analog=="Analog Signal 2"){
					Dato_Analogico = "AI2";
				}
				else if(Valor_Analog=="Analog Signal 3"){
					Dato_Analogico = "AI3";
				}
				else if(Valor_Analog=="Analog Signal 4"){
					Dato_Analogico = "AI4";
				}				
			}
			else if(Digital_Type == true) {
				
				Valor_Digital = Opciones_D.getValue();
				series.setName(Valor_Digital);
				
				if(Valor_Digital=="Digital Signal 1"){
					Dato_Analogico = "DI1";
				}
				else if(Valor_Digital=="Digital Signal 2"){
					Dato_Analogico = "DI2";
				}
				else if(Valor_Digital=="Digital Signal 3"){
					Dato_Analogico = "DI3";
				}
				else if(Valor_Digital=="Digital Signal 4"){
					Dato_Analogico = "DI4";
				}
			}
			
			Dato_Enviar = Dato_Analogico;
			
			Pedir.Modificar(Dato_Enviar);
			
		    Thread thread = new Thread(userThread, "T1");
		    thread.start();
		}
	}
	
	/**
	 * This function is related with the Stop button that allow the stop on the interface and make available the save buttons or 
	 * change the information or the input signal, as well as the sample time or the window.
	 * @throws InterruptedException
	 */
	@FXML
	public void Stop_Action() throws InterruptedException{
		Boton_Graficar.setDisable(false);
		Boton_Save.setDisable(false);
		Boton_Saveas.setDisable(false);
		Opciones.setDisable(true);
		Opciones_D.setDisable(true);
		Text_Muestreo.setDisable(false);
		Text_Ventana.setDisable(false);
		Opciones_Puertos.setDisable(false);
		Analog_Boton.setDisable(false);
		Digital_Boton.setDisable(false);
		Graficando = false;
	    userThread.stop();
	}
	
	/**
	 * This function is related with all the Check-box that have the digital output through the interface, to send the information
	 * about the states of the led.
	 * @throws InterruptedException
	 */
	@FXML
	public void Digital_Output() throws InterruptedException{
		
		if(Graficando == false){		
			Funcion_LEDS();		
			Dato_Enviar = "AAA" + LED_1 + LED_2 + LED_3 + LED_4;			
			Pedir.Modificar(Dato_Enviar);			
			try {
				serial.writeString(Dato_Enviar);
			} catch (SerialPortException e) {
				//e.printStackTrace();
			}		
		}
	}

	/**
	 * This function allow us to know which are the input that we want to measure, and block the others inputs.
	 * @throws InterruptedException
	 */
	@FXML
	public void Signal_Tipo() throws InterruptedException{
		
		if(Digital_Boton.isSelected()==true) {
			Digital_Boton.setSelected(true);
			Analog_Boton.setSelected(false);
			Opciones.setDisable(true);
			Opciones_D.setDisable(false);
			Digital_Type = true;
			Analog_Type = false;
			Opciones.setDisable(true);
			Opciones_D.setDisable(false);
		}		
		else if(Analog_Boton.isSelected()==true) {
			Digital_Boton.setSelected(false);
			Analog_Boton.setSelected(true);
			Opciones.setDisable(false);
			Opciones_D.setDisable(true);
			Digital_Type = false;
			Analog_Type = true;
			Opciones.setDisable(false);
			Opciones_D.setDisable(true);
		}
		else if(Analog_Boton.isSelected()==false || Digital_Boton.isSelected()==false) {
			Opciones.setDisable(true);
			Opciones_D.setDisable(true);
			Digital_Type = false;
			Analog_Type = false;
		}
		
	}
	
	/**
	 * This function has the only task to ask about the state of the input and know if the interface has selected a input.
	 */
	@FXML
	public void Can_Plot(){
		Valor_Analog = Opciones.getValue();
		Valor_Digital = Opciones_D.getValue();
		series.setName(Valor_Analog);
		
		if(Valor_Analog == "Analog Signal 1" || Valor_Analog =="Analog Signal 2" || Valor_Analog =="Analog Signal 3" || Valor_Analog =="Analog Signal 4" || Valor_Digital == "Digital Signal 1" || Valor_Digital == "Digital Signal 2" || Valor_Digital == "Digital Signal 3" || Valor_Digital == "Digital Signal 4") {
			Boton_Graficar.setDisable(false);
		}
	}
	
	/**
	 * This function is the heart of the interface, have the function that continuously send the information about the inputs 
	 * peripheries.
	 * 
	 * @author KevinJoven and AlejandraAnacona
	 *
	 */
	public class UserThread implements Runnable {
	   private volatile boolean exit = false;
	   public void run() {
		  exit = false;
		  String Variable = Pedir.Dar();

	      while(!exit) {
	    	  Platform.runLater(() ->{
					try {
						Funcion_LEDS();						
						serial.writeString(Variable+LED_1+LED_2+LED_3+LED_4);
						
						Grafica_Datos();
						Grafica_CSS();
					} catch (SerialPortException e) {
						//e.printStackTrace();
					}
				});

				try {
					Thread.sleep((long) Tiempo_Muestreo);
				}catch(InterruptedException iex){}
	      }
	   }
	   public void stop() {
	      exit = true;
	   }
	}

	/**
	 * Notice: this class is a intern class and is only use inside a main function. 
	 * This class has two methods one of this is to change the state of the variable that we send to the hardware and second is give
	 * that state.
	 * @author KevinJoven and AlejandraAnacona
	 *
	 */
	public static class Pedir {
		static String Valor = "";
		public static String Dar(){
			return Valor;
		}
		public static void Modificar(String Modificador) {
					
			Valor = Modificador;
		}
	}
	
	/**
	 * This function allow to know is the port is currently open or busy.
	 */
	@FXML
	public void set_Port(){
		
		if(Port_Open == true) {
			Reco.setText("This port is currently open.");
		}
		else {
			if(Opciones_Puertos.getValue() == null){
				Reco.setText("Please select a Port.");
			}
			else {
				serial = new SerialPort(Opciones_Puertos.getValue());
				try {
					serial.openPort();
					serial.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					serial.addEventListener(new Escuchar(serial),SerialPort.MASK_RXCHAR);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Port_Open = true;
				Digital_O_1.setDisable(false);
				Digital_O_2.setDisable(false);
				Digital_O_3.setDisable(false);
				Digital_O_4.setDisable(false);
		}
		}
	}
	
	/**
	 * This function is connected to the save button to save the information in a .txt file that has the values of the input data. 
	 */
	@FXML
	public void save(){
		try {
            PrintWriter writer = new PrintWriter("C:/Users/hp1/Desktop/Lab_Interfaces/texto_S2.txt", "UTF-8");
            writer.println("Time,Value");
            for(int i=0;i<Valores.size();i++) {
            	writer.println(i*Tiempo_Muestreo/1000+","+Valores.get(i));
            }
            writer.close();
        } catch (Exception e) {
            //e.printStackTrace();
        	Reco.setText("An error save the file, \n please close all your windows.");
        }
	}
	
	/**
	 * This function is connected to the save_as button to save the information in a .txt file that has the values of the input data,
	 * whit a address and name to defined by the user.
	 */
	@FXML
	public void saveas(){
		try {
            PrintWriter writer = new PrintWriter(Saveas_Address.getText()+"/"+Saveas_Title.getText()+".txt", "UTF-8");
            writer.println("Time,Value");
            for(int i=0;i<Valores.size();i++) {
            	writer.println(i*Tiempo_Muestreo/1000+","+Valores.get(i));
            }
            writer.close();
        } catch (Exception e) {
        	Reco.setText("An invalid Address or Title.");
        }
	}
	
	/**
	 * This function is call the initialize that runs in the first execution of the interface, we have here the habilitation and
	 * deshabilitation of diferent components.
	 * 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ObservableList<String> list = FXCollections.observableArrayList("Analog Signal 1","Analog Signal 2","Analog Signal 3","Analog Signal 4");
		Opciones.setItems(list);
		ObservableList<String> list2 = FXCollections.observableArrayList("Digital Signal 1","Digital Signal 2","Digital Signal 3","Digital Signal 4");
		Opciones_D.setItems(list2);
		
		Opciones.setDisable(true);
		Opciones_D.setDisable(true);
		
		Boton_Graficar.setDisable(true);
		Boton_Stop.setDisable(true);
		Boton_Save.setDisable(true);
		Boton_Saveas.setDisable(true);
		
		Digital_O_1.setDisable(true);
		Digital_O_2.setDisable(true);
		Digital_O_3.setDisable(true);
		Digital_O_4.setDisable(true);
		
		ObservableList<String> PuertosCombo = FXCollections.observableArrayList();
		String Puertos[] = SerialPortList.getPortNames();
		for(String n : Puertos){
			PuertosCombo.add(n);
		}
		Opciones_Puertos.setItems(PuertosCombo);
			
		}
	
	/**
	 * This class is the provider of heard the information that is send through the serial port.
	 * @author KevinJoven and AlejandraAnacona
	 *
	 */
	class Escuchar implements SerialPortEventListener{
		
		SerialPort serial;
		public Escuchar(SerialPort serial) {
			this.serial = serial;
		}
		@Override
		public void serialEvent(SerialPortEvent spe) {	
			try {
				String msg = "";
				msg = serial.readString(4);
				Valores.add(Float.parseFloat(msg));
			} catch (SerialPortException e) {
				e.printStackTrace();
			}		
		}
	}
	
	/**
	 * This function allow the combination and the change of different styles of CSS in the graphics design.
	 */
	public void Grafica_CSS() {
		
		//Valor_Analog = Opciones.getValue();
		
		if(Valor_Analog == "Analog Signal 1") {			
			Set<Node> nodes = Grafica.lookupAll(".series" + 0);
			for (Node n : nodes) {
			    n.setStyle("-fx-background-color: purple, white;\n"
			           + "    -fx-background-insets: 0, 2;\n"
			            + "    -fx-background-radius: 4px;\n"
			            + "    -fx-padding: 5px;");
			}
			series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: purple;");			
		}
		else if(Valor_Analog == "Analog Signal 2") {
			Set<Node> nodes = Grafica.lookupAll(".series" + 0);
			for (Node n : nodes) {
			    n.setStyle("-fx-background-color: blue, white;\n"
			           + "    -fx-background-insets: 0, 2;\n"
			            + "    -fx-background-radius: 4px;\n"
			            + "    -fx-padding: 5px;");
			}
			series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: blue;");
		}
		else if(Valor_Analog == "Analog Signal 3") {
			Set<Node> nodes = Grafica.lookupAll(".series" + 0);
			for (Node n : nodes) {
			    n.setStyle("-fx-background-color: red, white;\n"
			           + "    -fx-background-insets: 0, 2;\n"
			            + "    -fx-background-radius: 4px;\n"
			            + "    -fx-padding: 5px;");
			}
			series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: red;");
		}
		else if(Valor_Analog == "Analog Signal 4") {
			Set<Node> nodes = Grafica.lookupAll(".series" + 0);
			for (Node n : nodes) {
			    n.setStyle("-fx-background-color: green, white;\n"
			           + "    -fx-background-insets: 0, 2;\n"
			            + "    -fx-background-radius: 4px;\n"
			            + "    -fx-padding: 5px;");
			}
			series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: green;");
		}
		else if(Valor_Digital == "Digital Signal 1" || Valor_Digital == "Digital Signal 2" || Valor_Digital == "Digital Signal 3" || Valor_Digital == "Digital Signal 4") {
			Set<Node> nodes = Grafica.lookupAll(".series" + 0);
			for (Node n : nodes) {
			    n.setStyle("-fx-background-color: blue, white;\n"
			           + "    -fx-background-insets: 0, 2;\n"
			            + "    -fx-background-radius: 4px;\n"
			            + "    -fx-padding: 5px;");
			}
			series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: blue;");
		}		
	}

	/**
	 * This function depends of the selection of the digital or analog input to graphics in the interface to save the informations
	 * in the corresponding variables.
	 */
	public void Grafica_Datos() {

		if(Valor_Analog == "Analog Signal 1") {
			if(iterador > Ventana){
				Grafica.getData().clear();
				series.getData().clear();
				for(int j=0;j<Ventana;j++){
					series.getData().add(new XYChart.Data<String,Number>(String.valueOf((j+iterador-Ventana)*Tiempo_Muestreo/1000),(float)Valores.get(iterador-(Ventana+1)+j)));
				}
				Grafica.getData().add(series);
				iterador++;
			}
			else {
				for(int i=0;i<Valores.size();i++) {
					series.getData().add(new XYChart.Data<String,Number>(String.valueOf(i*Tiempo_Muestreo/1000),(float)Valores.get(i)));
				}
				iterador++;
				Grafica.getData().clear();
				Grafica.getData().add(series);
			}
		}
		else if(Valor_Analog == "Analog Signal 2") {
			if(iterador > Ventana){
				Grafica.getData().clear();
				series.getData().clear();
				for(int j=0;j<Ventana;j++){
					series.getData().add(new XYChart.Data<String,Number>(String.valueOf((j+iterador-Ventana)*Tiempo_Muestreo/1000),(float)Valores.get(iterador-(Ventana+1)+j)));
				}
				Grafica.getData().add(series);
				iterador++;
			}
			else {
				for(int i=0;i<Valores.size();i++) {
					series.getData().add(new XYChart.Data<String,Number>(String.valueOf(i*Tiempo_Muestreo/1000),(float)Valores.get(i)));
				}
				iterador++;
				Grafica.getData().clear();
				Grafica.getData().add(series);
			}
		}
		else if(Valor_Analog == "Analog Signal 3") {
			if(iterador > Ventana){
				Grafica.getData().clear();
				series.getData().clear();
				for(int j=0;j<Ventana;j++){
					series.getData().add(new XYChart.Data<String,Number>(String.valueOf((j+iterador-Ventana)*Tiempo_Muestreo/1000),(float)Valores.get(iterador-(Ventana+1)+j)));
				}
				Grafica.getData().add(series);
				iterador++;
			}
			else {
				for(int i=0;i<Valores.size();i++) {
					series.getData().add(new XYChart.Data<String,Number>(String.valueOf(i*Tiempo_Muestreo/1000),(float)Valores.get(i)));
				}
				iterador++;
				Grafica.getData().clear();
				Grafica.getData().add(series);
			}
		}
		else if(Valor_Analog == "Analog Signal 4") {
			if(iterador > Ventana){
				Grafica.getData().clear();
				series.getData().clear();
				for(int j=0;j<Ventana;j++){
					series.getData().add(new XYChart.Data<String,Number>(String.valueOf((j+iterador-Ventana)*Tiempo_Muestreo/1000),(float)Valores.get(iterador-(Ventana+1)+j)));
				}
				Grafica.getData().add(series);
				iterador++;
			}
			else {
				for(int i=0;i<Valores.size();i++) {
					series.getData().add(new XYChart.Data<String,Number>(String.valueOf(i*Tiempo_Muestreo/1000),(float)Valores.get(i)));
				}
				iterador++;
				Grafica.getData().clear();
				Grafica.getData().add(series);
			}
		}
		else if(Valor_Digital == "Digital Signal 1" || Valor_Digital == "Digital Signal 2" || Valor_Digital == "Digital Signal 3" || Valor_Digital == "Digital Signal 4") {
			if(iterador > Ventana){
				Grafica.getData().clear();
				series.getData().clear();
				for(int j=0;j<Ventana;j++){
					series.getData().add(new XYChart.Data<String,Number>(String.valueOf((j+iterador-Ventana)*Tiempo_Muestreo/1000),(float)Valores.get(iterador-(Ventana+1)+j)));
				}
				Grafica.getData().add(series);
				iterador++;
			}
			else {
				for(int i=0;i<Valores.size();i++) {
					series.getData().add(new XYChart.Data<String,Number>(String.valueOf(i*Tiempo_Muestreo/1000),(float)Valores.get(i)));
				}
				iterador++;
				Grafica.getData().clear();
				Grafica.getData().add(series);
			}
		}
	}
	
	/**
	 * This function supervised the state of the Check-box to send the information about the state of the led.
	 */
	public void Funcion_LEDS() {
		if(Digital_O_1.isSelected() == true) {
			LED_1 = '1';
		}
		else {
			LED_1 = '0';
		}
		
		if(Digital_O_2.isSelected() == true) {
			LED_2 = '1';
		}
		else {
			LED_2 = '0';
		}
		if(Digital_O_3.isSelected() == true) {
			LED_3 = '1';
		}
		else {
			LED_3 = '0';
		}
		if(Digital_O_4.isSelected() == true) {
			LED_4 = '1';
		}
		else {
			LED_4 = '0';
		}
	}

	/**
	 * This function help us to know if the variable is an integer.
	 * @param s (String)
	 * @return boolean
	 */
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	/**
	 * This function help us to know if the variable is an integer with certain radix.
	 * @param s (String)
	 * @param radix (int)
	 * @return boolean
	 */
	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	/**
	 * This function help us to know if the variable is a float.
	 * @param Valor (String)
	 * @return boolean
	 */
	public static boolean isFloat(String Valor) {
		try{
	        Float.parseFloat(Valor);
	        return true;
	    }catch(NumberFormatException e){
	        return false;
	    }		
	}
	
	// END OF THE CODE.
}