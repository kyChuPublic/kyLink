/**
 * @file    kyLinkDecoder.java
 * @author  kyChu
 * @version V1.0.0
 * @date    2017/06/08
 * @brief   kylink decode method.
 */
package kyLink.decoder;

import java.util.ArrayList;

import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;
import kyLink.CRC.CalculateCRC;

public final class kyLinkDecoder {
	public static enum DECODE_STATE {
		DECODE_STATE_UNSYNCED,
		DECODE_STATE_GOT_STX1,
		DECODE_STATE_GOT_STX2,
		DECODE_STATE_GOT_DEVID,
		DECODE_STATE_GOT_MSGID,
		DECODE_STATE_GOT_LEN_L,
		DECODE_STATE_GOT_LEN_H,
		DECODE_STATE_GOT_DATA,
		DECODE_STATE_GOT_CRC_L,
	}

	private kyLinkPackage rPacket = new kyLinkPackage();
	public kyLinkPackage RecPackage = new kyLinkPackage();

	private int _rxlen = 0;
	private DECODE_STATE _decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;

	private ArrayList<kyLinkDecodeEventListener> Listeners = new ArrayList<kyLinkDecodeEventListener>();

	private void publishEvent(kyLinkDecodeEvent event) {
		for(kyLinkDecodeEventListener listener : Listeners) {
			listener.getNewPackage(event);
		}
	}

	public final DECODE_STATE rx_decode(byte data) throws CloneNotSupportedException {
		switch(_decode_state) {
			case DECODE_STATE_UNSYNCED:
				if(data == kyLinkPackage.kySTX1) {
					_decode_state = DECODE_STATE.DECODE_STATE_GOT_STX1;
					rPacket.stx1 = kyLinkPackage.kySTX1;
				}
			break;
			case DECODE_STATE_GOT_STX1:
				if(data == kyLinkPackage.kySTX2) {
					_decode_state = DECODE_STATE.DECODE_STATE_GOT_STX2;
					rPacket.stx2 = kyLinkPackage.kySTX2;
				}
				else
					_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
			break;
			case DECODE_STATE_GOT_STX2:
				rPacket.dev_id = data;
				_decode_state = DECODE_STATE.DECODE_STATE_GOT_DEVID;
			break;
			case DECODE_STATE_GOT_DEVID:
				rPacket.msg_id = data;
				_decode_state = DECODE_STATE.DECODE_STATE_GOT_MSGID;
			break;
			case DECODE_STATE_GOT_MSGID:
				rPacket.length = data;
				_decode_state = DECODE_STATE.DECODE_STATE_GOT_LEN_L;
			break;
			case DECODE_STATE_GOT_LEN_L:
				rPacket.length = (short) (((short)data << 8) | (rPacket.length & 0xFF));
				if(rPacket.length <= kyLinkPackage.MAIN_DATA_CACHE) {
					_rxlen = 0;
					_decode_state = DECODE_STATE.DECODE_STATE_GOT_LEN_H;
				} else {
					_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
				}
			break;
			case DECODE_STATE_GOT_LEN_H:
				rPacket.rData[_rxlen ++] = data;
				if(_rxlen == rPacket.length) {
					_decode_state = DECODE_STATE.DECODE_STATE_GOT_DATA;
				}
				if(_rxlen > rPacket.rData.length) {
					_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
					for(kyLinkDecodeEventListener listener : Listeners) {
						listener.lenOverFlow(null);
					}
				}
			break;
			case DECODE_STATE_GOT_DATA:
				rPacket.crc = data;
				_decode_state = DECODE_STATE.DECODE_STATE_GOT_CRC_L;
			break;
			case DECODE_STATE_GOT_CRC_L:
				rPacket.crc = (short) (((short)data << 8) | (rPacket.crc & 0xFF));
				if(CalculateCRC.ComputeCRC16(rPacket.getCRCBuffer(), rPacket.length + 4) == rPacket.crc) {
					synchronized(new String("")) {//critical
						RecPackage = (kyLinkPackage)rPacket.PackageCopy();
						kyLinkDecodeEvent event = new kyLinkDecodeEvent(RecPackage);
						publishEvent(event);
					}
				} else {
					for(kyLinkDecodeEventListener listener : Listeners) {
						listener.badCRCEvent(null);
					}
				}
				_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
			break;
			default:
				_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
			break;
		}
		return _decode_state;
	}

	public void addDecodeListener(kyLinkDecodeEventListener listener) {
		Listeners.add(listener);
	}

	public void removeDecodeListener(kyLinkDecodeEventListener listener) {
		Listeners.remove(listener);
	}
}

/**
 * @ End of file.
 */
