package eu.odalic.extrarelatable.api.rest.responses;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>The REST API {@link Reply} type.</p>
 * 
 * <p>Adapted from Odalic with permission.</p>
 *
 * @author Václav Brodec
 *
 * @see Message the message format
 */
@XmlType
@XmlEnum(String.class)
@XmlRootElement
public enum ReplyType {
  /**
   * Reply contains actual data.
   */
  @XmlEnumValue("DATA") DATA,

  /**
   * Reply contains no actual data, only a message.
   */
  @XmlEnumValue("MESSAGE") MESSAGE
}
