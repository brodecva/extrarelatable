package eu.odalic.extrarelatable.services.odalic.values;

import static com.google.common.base.Preconditions.checkArgument;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Preconditions;

/**
 * Odalic domain class adapted for REST API.
 *
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "columnPosition")
public final class ColumnPositionValue {

  private int index;

  public ColumnPositionValue() {}
  
  public ColumnPositionValue(final int index) {
	  checkArgument(index >= 0, "The column position index must be nonnegative!");
	  
	  this.index = index;
  }

  /**
   * @return the index
   */
  @XmlElement
  public int getIndex() {
    return this.index;
  }

  /**
   * @param index the index to set
   */
  public void setIndex(final int index) {
    Preconditions.checkArgument(index >= 0, "The column position index must be nonnegative!");

    this.index = index;
  }

  @Override
  public String toString() {
    return "ColumnPositionValue [index=" + this.index + "]";
  }
}
