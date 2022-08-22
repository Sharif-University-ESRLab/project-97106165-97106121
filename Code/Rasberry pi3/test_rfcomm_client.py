# should run in laptop

import bluetooth

def main():
    nearby_devices = bluetooth.discover_devices()
    if not nearby_devices:
        return
    bd_addr = nearby_devices[0]
    port = 1
    sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )
    sock.connect((bd_addr, port))
    sock.send("hello!!")
    sock.close()


if __name__ == "__main__":
    main()
