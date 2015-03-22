package sep.conquest.model.requests;

/**
 * The enumeration for message-types of the broadcast- and bluetooth-
 * communication.
 * 
 * @author Andreas Wilhelm
 */
public enum MessageType {

  /**
   * Specifies a message in order to control the drive-direction of a robot.
   */
  CONTROL_DIR,

  /**
   * Specifies a message in order to control the speed of a robot.
   */
  CONTROL_SPEED,

  /**
   * Specifies a message to provide a status update of robots.
   */
  STATUS_UPDATE,

  /**
   * Specifies a message in case of a collision of robots.
   */
  COLLISION,

  /**
   * Specifies a message in case of enabling or disabling the manual control of
   * a participant.
   */
  CONTROL_ENABLE,

  /**
   * Specifies a message in case of a failure.
   */
  FAILURE,

  /**
   * Specifies a message which represents a drive-intent of a robot.
   */
  INTENT,

  /**
   * Specifies a message to introduce a participant to others.
   */
  HELLO,

  /**
   * Specifies an abyss-message of a robot.
   */
  PUCK_ABYSS,

  /**
   * Reset e-puck logic.
   */
  REQUEST_RESET,

  /**
   * Get e-puck status.
   */
  REQUEST_STATUS,

  /**
   * Turn e-puck by the specified degrees.
   */
  REQUEST_TURN,

  /**
   * Move e-puck in view direction until a node is found.
   */
  REQUEST_MOVE,

  /**
   * Change the e-puck speed.
   */
  REQUEST_SET_SPEED,

  /**
   * Set the specified LEDs.
   */
  REQUEST_SET_LED,

  /**
   * Request acknowledged.
   */
  RESPONSE_OK,

  /**
   * Current e-puck status.
   */
  RESPONSE_STATUS,

  /**
   * e-puck has hit a node.
   */
  RESPONSE_HIT_NODE,

  /**
   * e-puck has detected a collision while moving.
   */
  RESPONSE_COLLISION,

  /**
   * e-puck has detected an abyss while moving.
   */
  RESPONSE_ABYSS,
  
  /**
   * e-puck has rejected a command.
   */
  RESPONSE_REJECT,
  
  /**
   * Specifies a message in case of a collision of robots during localization.
   */
  LOCALIZE_COLLISION,
  
  /**
   * Specifies a message for passing the localization process to the next client.
   */
  PASS_LOCALIZATION,
  
  /**
   * Specifies a message for anouncing the finished localization.
   */
  FINISHED_LOCALIZATION;
}
