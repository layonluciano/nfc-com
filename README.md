# nfc-com
API used to communicate with Mifare 1K Classic NFC tags 

---

### Run the following commands to add project dependencies: 

* Run a system upgrade:
```
sudo apt-get update && sudo apt-get upgrade -y 
```

* Install some depedencies:
```
sudo apt-get install libccid pcscd libpcsclite-dev libpcsclite1 pcsc-tools 
```

* Download the library [libusb](http://downloads.sourceforge.net/libusb/libusb-1.0.20.tar.bz2) to allow communication with usb devices:

* Unzip, and run: 
``` 
./configure --disable-udev 
make
make install
```

* Download the library [acsccid](http://www.acs.com.hk/en/products/3/acr122u-usb-nfc-reader/) as ACR122U driver:

* Unzip, and run:
```
./configure
make 
make install
```
* Copy the file located at **src/92_pcscd_acsccid.rules** to the directory **/etc/udev/rules.d/**:
```
cp src/92_pcscd_acsccid.rules /etc/udev/rules.d/
```

* Unload the kernel mode:
``` sudo modprobe -r pn533 ```
or open the file **/etc/modprobe.d/blacklist.conf** and add the two lines:
```
blacklist pn533
blacklist nfc
```

* Start the service: 
``` 
sudo service pcscd restart 
```

* Then run:
``` 
pcsc_scan 
```
