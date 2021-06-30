float Valor;
String Analog_Signal;
String Digital_Output;
char Dato;
String Canal;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  pinMode(A0,INPUT);
  pinMode(A1,INPUT);
  pinMode(A2,INPUT);
  pinMode(A3,INPUT);
  pinMode(7,OUTPUT);
  pinMode(6,OUTPUT);
  pinMode(5,OUTPUT);
  pinMode(4,OUTPUT);
  pinMode(A8,INPUT);
  pinMode(A9,INPUT);
  pinMode(A10,INPUT);
  pinMode(A11,INPUT);
}

void loop() {
  
  if(Serial.available()>0){
    Dato = Serial.read();
    Canal.concat(Dato);
  }
  else{ 
    Analog_Signal = Canal.substring(0,3);
    Digital_Output = Canal.substring(3,7);

    if(Canal != ""){
      digitalWrite(7,Digital_Output.substring(0,1).toInt());
      digitalWrite(6,Digital_Output.substring(1,2).toInt());
      digitalWrite(5,Digital_Output.substring(2,3).toInt());
      digitalWrite(4,Digital_Output.substring(3,4).toInt());
    }
    
    if(Analog_Signal == "AI1"){
      Valor = analogRead(A0)*5/1024.0;
      Serial.print(Valor);   
    }
    else if(Analog_Signal == "AI2"){
      Valor = analogRead(A1)*5/1024.0;
      Serial.print(Valor);   
    }
    else if(Analog_Signal == "AI3"){
      Valor = analogRead(A2)*5/1024.0;
      Serial.print(Valor);   
    }
    else if(Analog_Signal == "AI4"){
      Valor = analogRead(A3)*5/1024.0;
      Serial.print(Valor);   
    }
    else if(Analog_Signal == "DI1"){
      Valor = digitalRead(A11);
      Serial.print(Valor);
    }
    else if(Analog_Signal == "DI2"){
      Valor = digitalRead(A10);
      Serial.print(Valor);
    }
    else if(Analog_Signal == "DI3"){
      Valor = digitalRead(A9);
      Serial.print(Valor);
    }
    else if(Analog_Signal == "DI4"){
      Valor = digitalRead(A8);
      Serial.print(Valor);
    }
    
    Canal="";    
    delay(10);
    }  
}
