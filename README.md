# Android-Python-Communication

## What does this repo do?

- C/S Structure with a Android Client and Python Server
- The Server and Client use TCP Socket to establish a communication
- Python Server
  - Get the user input and send the instructions to Client
  - Save the generated plaintext and ciphertext
- Android Client
  - Receive the instructions and do the encryption (AES/SM4)
  - return the ciphertext
- Meanwhile, a python USB listener would monitor the usb cable of the PC carrying out the Python Server
- The listener will receive 1/0 when the the client encryption algorithm started/stopped
  
## How does these code work
- First, please check the required environment (Python3.7/Android 10.0 SDK 29/libusb) and install the Android Client apk into your phone
- Second, plug your android phone into your computer. Then use `lsusb` command in terminal to remember the vid and pid of your equipment
  - Run the USBTest.py and you probably get some errors such as `No compatible devices found`. Also, your phone could receive a alert and you can just ignore it. 
  - Don't panic and run `libusb` again, then you will find that the vid and pid changed (maybe 18d1 and 2d01 or 2d00)
  - Change the `__init__` arguments into your vid and pid
  - Run the USBTest.py again and it should tell you the device already in the accessory mode
- Third, Run the ServerTest.py and your client app.
  - Make sure your phone and your PC are in the same local network
  - Type the private IP address and the port (default by 9090)
  - The server will tell you what to do.
- The Server Command is:
  - `aes/enc/10` for `running the aes encryption for 10 times`
  - `aes/enc` for `running the aes encryption for 1 time`
  - `stop` for stop
- While your server and client are interacting, the USB listener will listen the USB data sliently
 
