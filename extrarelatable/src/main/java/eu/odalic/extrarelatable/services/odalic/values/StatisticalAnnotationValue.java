package eu.odalic.extrarelatable.services.odalic.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.services.odalic.conversions.EntityCandidateValueSetDeserializer;
import eu.odalic.extrarelatable.services.odalic.conversions.EntityCandidateValueSetSerializer;

/**
 * <p>
 * Odalic domain class adapted for REST API.
 * </p>
 *
 * @author Josef Janoušek
 *
 */
@XmlRootElement(name = "statisticalAnnotation")
public final class StatisticalAnnotationValue {

  private Map<String, ComponentTypeValue> component;

  private Map<String, Set<EntityCandidateValue>> predicate;

  public StatisticalAnnotationValue() {
    this.component = ImmutableMap.of();
    this.predicate = ImmutableMap.of();
  }

  /**
   * @return the component
   */
  @XmlAnyElement
  public Map<String, ComponentTypeValue> getComponent() {
    return this.component;
  }

  /**
   * @return the predicate
   */
  @XmlAnyElement
  @JsonDeserialize(contentUsing = EntityCandidateValueSetDeserializer.class)
  @JsonSerialize(contentUsing = EntityCandidateValueSetSerializer.class)
  public Map<String, Set<EntityCandidateValue>> getPredicate() {
    return this.predicate;
  }

  /**
   * @param component the component to set
   */
  public void setComponent(
      final Map<? extends String, ? extends ComponentTypeValue> component) {
    this.component = ImmutableMap.copyOf(component);
  }

  /**
   * @param predicate the predicate to set
   */
  public void setPredicate(
      final Map<? extends String, ? extends Set<? extends EntityCandidateValue>> predicate) {
    checkNotNull(predicate);
	  
	  this.predicate = predicate.entrySet().stream().collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> ImmutableSet.copyOf(e.getValue())));
  }

  @Override
  public String toString() {
    return "StatisticalAnnotationValue [component=" + this.component + ", predicate="
        + this.predicate + "]";
  }
}
