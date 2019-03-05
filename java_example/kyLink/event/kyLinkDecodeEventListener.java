/**
 * @file    kyLinkDecodeEventListener.java
 * @author  kyChu
 * @version V1.0.0
 * @date    2017/08/02
 * @brief   kylink decode event listener.
 */
package kyLink.event;

import java.util.EventListener;

public interface kyLinkDecodeEventListener extends EventListener {
	public void getNewPackage(kyLinkDecodeEvent event);
	public void badCRCEvent(kyLinkDecodeEvent event);
	public void lenOverFlow(kyLinkDecodeEvent event);
}

/**
 * @ End of file.
 */
