package eu.odalic.extrarelatable.services.odalic.values;

import java.io.Serializable;

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
@XmlRootElement(name = "entity")
public final class EntityValue implements Comparable<EntityValue>, Serializable {

  private static final long serialVersionUID = 5750987769573292984L;

  private String resource;

  private String label;

  private String prefixed;

  private PrefixValue prefix;

  private String tail;

  public EntityValue() {}

  /**
   * @return the label
   */
  @XmlElement
  @Nullable
  public String getLabel() {
    return this.label;
  }

  /**
   * @return the prefix
   */
  @XmlElement
  @Nullable
  public PrefixValue getPrefix() {
    return this.prefix;
  }

  /**
   * @return the prefixed form of the resource
   */
  @XmlElement
  @Nullable
  public String getPrefixed() {
    return this.prefixed;
  }

  /**
   * @return the resource ID
   */
  @XmlElement
  @Nullable
  public String getResource() {
    return this.resource;
  }

  /**
   * @return the tail
   */
  @XmlElement
  @Nullable
  public String getTail() {
    return this.tail;
  }

  /**
   * @param label the label to set
   */
  public void setLabel(final String label) {
    Preconditions.checkNotNull(label, "The label cannot be null!");

    this.label = label;
  }

  /**
   * @param prefix the prefix to set
   */
  public void setPrefix(final PrefixValue prefix) {
    this.prefix = prefix;
  }

  /**
   * @param prefixed the prefixed form of the resource to set
   */
  public void setPrefixed(final String prefixed) {
    this.prefixed = prefixed;
  }

  /**
   * @param resource the resource ID to set
   */
  public void setResource(final String resource) {
    Preconditions.checkNotNull(resource, "The resource cannot be null!");

    this.resource = resource;
  }

  /**
   * @param tail the tail to set
   */
  public void setTail(final String tail) {
    this.tail = tail;
  }
  
  /**
   * Compares the entities by their resource ID lexicographically.
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   * @see java.lang.String#compareTo(String) for the definition of resource ID comparison
   */
  @Override
  public int compareTo(final EntityValue o) {
    return getResource().compareTo(o.getResource());
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    final EntityValue other = (EntityValue) object;
    if (!getResource().equals(other.getResource())) {
      return false;
    }
    return true;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + getResource().hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "EntityValue [resource=" + this.resource + ", label=" + this.label + ", prefixed="
        + this.prefixed + ", prefix=" + this.prefix + ", tail=" + this.tail + "]";
  }
}
