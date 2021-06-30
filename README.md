# Interfaces - Hardware & Software communication
This repository store the related information to the course INTERFACES in the Universidad del Valle, this course is focus on develop a software application that interact with the hardware itself.

Designe by: Kevin Jofroit Joven Noriega.

This application was designed using Java in a Eclipse enviorement.
The programming of the hardware was using the Arduino MEGA.

The requeriments of the project are the following:

- Analog input 4 channels.
- Digital input 4 channels.
- Digital output 4 channels.
- Change of the sample frequency.
- Change of the windows size.
- Send the information in a continuous way.
- Plot the realted information about the selected channel.
- Storage all the information in the process.

The following image show the graphics interface that is compose by diferent buttons, that are explain like follow:

- PORT: Show the currently channels available where is connect some device.
- SET: Define the channel to send/recive the information.
- SAMPLE TIME: Stablish a sample time to the analog signal, this value depends of the velocity of the device.
- WINDOW: The size of the samples to plot in the interface.
- INPUT TYPE: Define the input type signal.
- ANALOG INPUT: Define the analog channel.
- DIGITAL INPUT: Define the digital channel.
- DIGITAL OUTPUTS: Turn on/off the output channels.
- RECOMMENDATION BOX: Show relevant information about error in the exacution or configuration of the device. e.g. String in the SAMPLE TIME.
- PLOT: Plot the information related with the channel.
- STOP: The the plot of information.
- SAVE: Save the data in the desktop with a predefined title.
- SAVE: Save the data in a ADDRESS specific and TITLE define.

![Tux, the Linux mascot](https://user-images.githubusercontent.com/59969678/123896719-af201780-d927-11eb-81c2-328fd7740325.png)

COM4 are stablish to do the communication.

![Tux, the Linux mascot](https://user-images.githubusercontent.com/59969678/123897166-88161580-d928-11eb-95b8-7a9566b0be23.png)

Plotting information related to the Analog channel 2, with a sample time of 0.1 [s] and window size 50.
The output channel related with RED & BLUE are turn on.

![Tux, the Linux mascot](https://user-images.githubusercontent.com/59969678/123897170-88aeac00-d928-11eb-9360-8edb59d39e14.png)

![Tux, the Linux mascot](https://user-images.githubusercontent.com/59969678/123900732-3b820880-d92f-11eb-91ba-f7069555c8af.png)

Plotting information related to the Analog channel 3, with a sample time of 0.1 [s] and window size 50.
The output channel related with RED & BLUE are turn on.

![Tux, the Linux mascot](https://user-images.githubusercontent.com/59969678/123897172-88aeac00-d928-11eb-98c7-a6ad2ae51fad.png)

Plotting information related to the Digital channel 2, with a sample time of 0.1 [s] and window size 100.
The output channel related with RED & BLUE are turn on.

![Tux, the Linux mascot](https://user-images.githubusercontent.com/59969678/123897175-89474280-d928-11eb-9f05-730de94dd7d6.png)


















