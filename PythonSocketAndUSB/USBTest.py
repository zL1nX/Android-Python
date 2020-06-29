from USBUtil import USB
import signal

flag = None
usb_dev = USB()
usb_dev.dev_open()
usb_dev.turn_acc()
usb_dev.set_endpoint_in()


def signal_handler(signal, frame):
    global interrupted
    interrupted = True


signal.signal(signal.SIGINT, signal_handler)
interrupted = False
while True:
    flag = usb_dev.flag_read()
    if flag == 1:
        print("[USB] Encryption Started. ")
    elif flag == 0:
        print("[USB] Encryption Stopped. ")

    if interrupted:
        print("[USB] GoodBye")
        break


