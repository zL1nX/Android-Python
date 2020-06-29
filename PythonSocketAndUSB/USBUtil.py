import usb.core
import usb.util
import usb.backend.libusb1
import time
import struct

ACCESSORY_VID = 0x18D1  #
ACCESSORY_PID = (0x2D00, 0x2D01, 0x2D04, 0x2D05)
MANUFACTURER = "HUAWEI"
MODEL_NAME = "app_name"
DESCRIPTION = "usb test"
VERSION = "0.1"
URL = "http://www.google.com"
SERIAL_NUMBER = "8UJDU19B20005930"


class USB:
    vid = 0  # 12d1 18d1
    pid = 0  # 107e 2d01
    dev = None
    ep_in = None

    def __init__(self, vid=0x18d1, pid=0x2d01):
        self.vid = vid
        self.pid = pid

    def dev_open(self):
        self.dev = usb.core.find(idVendor=self.vid)
        if self.dev is None:
            raise ValueError("[USB] No compatible device not found")
        print("[USB] Compatible device found ")

    def turn_acc(self):
        if self.dev.idProduct in ACCESSORY_PID:
            print("[USB] Device is in accessory mode")
        else:
            print("[USB] Device is not in accessory mode yet,  VID %04X" % self.vid)
            self.accessory()
            self.dev = usb.core.find(idVendor=ACCESSORY_VID)
            if self.dev is None:
                raise ValueError("[USB] No compatible device not found")
            if self.dev.idProduct in ACCESSORY_PID:
                print("[USB] Device is in accessory mode")
            else:
                raise ValueError("")

        tries = 5
        while True:
            try:
                if tries <= 0:
                    break
                self.dev.set_configuration()
                break
            except :
                print("[USB] Unable to set configuration, retrying")
                tries -= 1
                time.sleep(1)

    def accessory(self):
        version = self.dev.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_IN,
            51, 0, 0, 2)

        print("version is: %d" % struct.unpack('<H', version))

        assert self.dev.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            52, 0, 0, MANUFACTURER) == len(MANUFACTURER)

        assert self.dev.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            52, 0, 1, MODEL_NAME) == len(MODEL_NAME)

        assert self.dev.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            52, 0, 2, DESCRIPTION) == len(DESCRIPTION)

        assert self.dev.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            52, 0, 3, VERSION) == len(VERSION)

        assert self.dev.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            52, 0, 4, URL) == len(URL)

        assert self.dev.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            52, 0, 5, SERIAL_NUMBER) == len(SERIAL_NUMBER)

        self.dev.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            53, 0, 0, None)

    def set_endpoint_in(self):
        time.sleep(1)
        dev = usb.core.find(idVendor=ACCESSORY_VID)
        if dev is None:
            dev = usb.core.find(idVendor=self.vid)
        if dev is None:
            raise ValueError("[USB] Device set to accessory mode but VID %04X not found" % self.vid)

        cfg = dev.get_active_configuration()
        if_num = cfg[(0, 0)].bInterfaceNumber
        intf = usb.util.find_descriptor(cfg, bInterfaceNumber=if_num)
        self.dev = dev
        self.ep_in = usb.util.find_descriptor(
            intf,
            custom_match=lambda e: usb.util.endpoint_direction(e.bEndpointAddress) == usb.util.ENDPOINT_IN
        )

    def buffer_read(self, size=1024):
        data = None
        try:
            data = self.ep_in.read(size, timeout=1000)
        except usb.core.USBError as e:
            pass
        return data

    def flag_read(self):
        buffer = self.buffer_read()
        flag = None
        if buffer is not None:
            flag = buffer[0]
            print("[USB] Read Flag %d" % flag)
        return flag

    def usb_close(self):
        self.dev.close()
