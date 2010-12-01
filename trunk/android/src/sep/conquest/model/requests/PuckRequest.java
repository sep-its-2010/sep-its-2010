package sep.conquest.model.requests;

import java.util.UUID;

import sep.conquest.model.IRequest;


/**
 * The PuckRequest class represents message objects that are sent by 
 * robots to the android application. It contains some information
 * about the corresponding type.
 * 
 * @author Andreas Wilhelm (Florian Lorenz)
 *
 */
public class PuckRequest implements IRequest {
    
    /**
     * The byte-array of the corresponding request-message.
     */
    private byte[] message = new byte[32];
    
    private MessageType type;
    
    /*
     * Represents the byte-Code for the messageType Response_Ok
     */
    private static final short resOk = (short) 0x81FF;
    
    /*
     * Represents the byte-Code for the messageType Response_Status
     */
    private static final short resStatus = (short) 0x82FF;
    
    /*
     * Represents the byte-Code for the messageType Response_HitNode
     */
    private static final short resHitNode = (short) 0x83FF;
    
    /*
     * Represents the byte-Code for the messageType Response_Collision
     */
    private static final short resCollision = (short) 0x84FF;
    
    /*
     * Represents the byte-Code for the messageType Response_Abyss
     */
    private static final short resAbyss = (short) 0x85FF;
    
    /**
     * The constructor expects an request message to identify the type of the
     * message as well as its corresponding data. It stores the specific
     * message-type.
     * 
     * @param message The input-message to determine the type and data.
     */
    public PuckRequest(byte[] message) {
    	for (int i = 0; i < 32; i++) {
			this.message[i] = message[i];
		}
		short messageType;
		messageType = (short) (((message[1] & 0xFF) << 8) | message[0] & 0xFF);
		switch (messageType) {
		case resOk:
			type = MessageType.RESPONSE_OK;
		case resAbyss:
			type = MessageType.RESPONSE_ABYSS;
		case resCollision:
			type = MessageType.RESPONSE_COLLISION;
		case resHitNode:
			type = MessageType.RESPONSE_HIT_NODE;
		case resStatus:
			type = MessageType.RESPONSE_STATUS;
		}	
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return type;
    }
    
    /**
     * Returns the byte-array of the robot-message.
     * 
     * @return The InputStream.
     */
    public byte[] getMessage() {
        return message;
    }

	/* (non-Javadoc)
	 * @see sep.conquest.model.IRequest#getReceiver()
	 */
	public UUID[] getReceiver() {
		return new UUID[0];
	}

	/* (non-Javadoc)
	 * @see sep.conquest.model.IRequest#getSender()
	 */
	public UUID getSender() {
		return UUID.randomUUID();
	}
}
