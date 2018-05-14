package eu.odalic.extrarelatable.services.odalic.values;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Preconditions;

/**
 * Odalic domain class adapted for REST API.
 *
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "columnRelationPosition")
public final class ColumnRelationPositionValue {

  private ColumnPositionValue first;

  private ColumnPositionValue second;

  public ColumnRelationPositionValue() {}
  
  public ColumnRelationPositionValue(final int firstIndex, final int secondIndex) {
	  this.first = new ColumnPositionValue(firstIndex);
	  this.second = new ColumnPositionValue(secondIndex);
  }

  /**
   * @return the first
   */
  @XmlElement
  @Nullable
  public ColumnPositionValue getFirst() {
    return this.first;
  }

  /**
   * @return the second
   */
  @XmlElement
  @Nullable
  public ColumnPositionValue getSecond() {
    return this.second;
  }

  /**
   * @param first the first to set
   */
  public void setFirst(final ColumnPositionValue first) {
    Preconditions.checkNotNull(first, "The first cannot be null!");

    this.first = first;
  }

  /**
   * @param second the second to set
   */
  public void setSecond(final ColumnPositionValue second) {
    Preconditions.checkNotNull(second, "The second cannot be null!");

    this.second = second;
  }

  @Override
  public String toString() {
    return "ColumnRelationPositionValue [first=" + this.first + ", second=" + this.second + "]";
  }
}
