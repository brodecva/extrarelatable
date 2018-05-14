package eu.odalic.extrarelatable.services.odalic.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableMap;

/**
 * <p>
 * Odalic domain class adapted for REST API.
 * </p>
 *
 * @author Josef Janoušek
 *
 */
@XmlRootElement(name = "columnProcessingAnnotation")
public final class ColumnProcessingAnnotationValue {

	private Map<String, ColumnProcessingTypeValue> processingType;

	public ColumnProcessingAnnotationValue() {
		this.processingType = ImmutableMap.of();
	}

	/**
	 * @return the processing type
	 */
	@XmlAnyElement
	public Map<String, ColumnProcessingTypeValue> getProcessingType() {
		return this.processingType;
	}

	/**
	 * @param processingType
	 *            the processing type to set
	 */
	public void setProcessingType(final Map<? extends String, ? extends ColumnProcessingTypeValue> processingType) {
		checkNotNull(processingType);

		this.processingType = ImmutableMap.copyOf(processingType);
	}

	@Override
	public String toString() {
		return "ColumnProcessingAnnotationValue [processingType=" + this.processingType + "]";
	}
}
