package eu.odalic.extrarelatable.services.odalic.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.odalic.extrarelatable.services.odalic.conversions.ColumnPositionValueSetDeserializer;
import eu.odalic.extrarelatable.services.odalic.conversions.ColumnPositionValueSetSerializer;
import eu.odalic.extrarelatable.services.odalic.conversions.ColumnRelationPositionKeyJsonDeserializer;
import eu.odalic.extrarelatable.services.odalic.conversions.ColumnRelationPositionKeyJsonSerializer;

/**
 * Odalic domain class adapted for REST API.
 *
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "result")
@JsonIgnoreProperties(ignoreUnknown=true)
public final class ResultValue implements Serializable {

  private static final long serialVersionUID = -6359038623760039155L;

  private Map<String, Set<ColumnPositionValue>> subjectColumnsPositions;

  private List<HeaderAnnotationValue> headerAnnotations;

  private CellAnnotationValue[][] cellAnnotations;

  private Map<ColumnRelationPositionValue, ColumnRelationAnnotationValue> columnRelationAnnotationsAlternative;

  private List<StatisticalAnnotationValue> statisticalAnnotations;

  private List<ColumnProcessingAnnotationValue> columnProcessingAnnotations;

  private List<String> warnings;

  public ResultValue() {
    this.subjectColumnsPositions = ImmutableMap.of();
    this.headerAnnotations = ImmutableList.of();
    this.cellAnnotations = new CellAnnotationValue[0][0];;
    this.columnRelationAnnotationsAlternative = ImmutableMap.of();
    this.statisticalAnnotations = ImmutableList.of();
    this.columnProcessingAnnotations = ImmutableList.of();
    this.warnings = ImmutableList.of();
  }

  /**
   * @return the cell annotations
   */
  @XmlElement
  public CellAnnotationValue[][] getCellAnnotations() {
    return eu.odalic.extrarelatable.util.Arrays.deepCopy(CellAnnotationValue.class, this.cellAnnotations);
  }

  /**
   * @return the column processing annotations
   */
  @XmlElement
  public List<ColumnProcessingAnnotationValue> getColumnProcessingAnnotations() {
    return this.columnProcessingAnnotations;
  }

  /**
   * @return the column relation annotations
   */
  @XmlElement
  @JsonDeserialize(keyUsing = ColumnRelationPositionKeyJsonDeserializer.class)
  @JsonSerialize(keyUsing = ColumnRelationPositionKeyJsonSerializer.class)
  public Map<ColumnRelationPositionValue, ColumnRelationAnnotationValue> getColumnRelationAnnotationsAlternative() {
    return this.columnRelationAnnotationsAlternative;
  }

  /**
   * @return the header annotations
   */
  @XmlElement
  public List<HeaderAnnotationValue> getHeaderAnnotations() {
    return this.headerAnnotations;
  }

  /**
   * @return the statistical annotations
   */
  @XmlElement
  public List<StatisticalAnnotationValue> getStatisticalAnnotations() {
    return this.statisticalAnnotations;
  }

  /**
   * @return the subject columns positions
   */
  @XmlAnyElement
  @JsonDeserialize(contentUsing = ColumnPositionValueSetDeserializer.class)
  @JsonSerialize(contentUsing = ColumnPositionValueSetSerializer.class)
  public Map<String, Set<ColumnPositionValue>> getSubjectColumnsPositions() {
    return this.subjectColumnsPositions;
  }

  /**
   * @return the warnings
   */
  @XmlElement
  public List<String> getWarnings() {
    return this.warnings;
  }

  /**
   * @param cellAnnotations the cell annotations to set
   */
  public void setCellAnnotations(final CellAnnotationValue[][] cellAnnotations) {
    Preconditions.checkNotNull(cellAnnotations, "The cellAnnotations cannot be null!");

    this.cellAnnotations =
    		eu.odalic.extrarelatable.util.Arrays.deepCopy(CellAnnotationValue.class, cellAnnotations);
  }

  /**
   * @param columnProcessingAnnotations the column processing annotations to set
   */
  public void setColumnProcessingAnnotations(
      final List<ColumnProcessingAnnotationValue> columnProcessingAnnotations) {
    Preconditions.checkNotNull(columnProcessingAnnotations, "The columnProcessingAnnotations cannot be null!");

    this.columnProcessingAnnotations = ImmutableList.copyOf(columnProcessingAnnotations);
  }

  /**
   * @param columnRelationAnnotationsAlternative the column relation annotations to set
   */
  public void setColumnRelationAnnotationsAlternative(
      final Map<? extends ColumnRelationPositionValue, ? extends ColumnRelationAnnotationValue> columnRelationAnnotationsAlternative) {
    checkNotNull(columnRelationAnnotationsAlternative);
    
    this.columnRelationAnnotationsAlternative = ImmutableMap.copyOf(columnRelationAnnotationsAlternative);
  }

  /**
   * @param headerAnnotations the header annotations to set
   */
  public void setHeaderAnnotations(final List<HeaderAnnotationValue> headerAnnotations) {
    Preconditions.checkNotNull(headerAnnotations, "The headerAnnotations cannot be null!");

    this.headerAnnotations = ImmutableList.copyOf(headerAnnotations);
  }

  /**
   * @param statisticalAnnotations the statistical annotations to set
   */
  public void setStatisticalAnnotations(final List<StatisticalAnnotationValue> statisticalAnnotations) {
    Preconditions.checkNotNull(statisticalAnnotations, "The statisticalAnnotations cannot be null!");

    this.statisticalAnnotations = ImmutableList.copyOf(statisticalAnnotations);
  }
  
  /**
   * @param subjectColumnsPositions the subject columns positions to set
   */
  public void setSubjectColumnsPositions(
      final Map<? extends String, Set<ColumnPositionValue>> subjectColumnsPositions) {
    Preconditions.checkNotNull(subjectColumnsPositions, "The subjectColumnsPositions cannot be null!");
    
    this.subjectColumnsPositions = ImmutableMap.copyOf(subjectColumnsPositions);
  }

  /**
   * @param warnings the warnings to set
   */
  public void setWarnings(final List<String> warnings) {
    Preconditions.checkNotNull(warnings, "The warnings cannot be null!");

    this.warnings = ImmutableList.copyOf(warnings);
  }

  @Override
  public String toString() {
    return "ResultValue [subjectColumnsPositions=" + this.subjectColumnsPositions
        + ", headerAnnotations=" + this.headerAnnotations + ", cellAnnotations="
        + Arrays.toString(this.cellAnnotations) + ", columnRelationAnnotationsAlternative="
        + this.columnRelationAnnotationsAlternative + ", statisticalAnnotations=" + this.statisticalAnnotations
        + ", warnings=" + this.warnings + "]";
  }
}
