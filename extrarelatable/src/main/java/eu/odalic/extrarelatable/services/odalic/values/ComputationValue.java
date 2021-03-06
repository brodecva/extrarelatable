package eu.odalic.extrarelatable.services.odalic.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Set;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

/**
 * Stand-alone computation request for REST API. Apart from the encoded table as
 * {@link ComputationInputValue}, it keeps the names of the used knowledge
 * bases, the primary one and the (unused by ERT) statistical processing flag.
 *
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "computation")
public final class ComputationValue implements Serializable {

	private static final long serialVersionUID = -6359038623760039155L;

	private ComputationInputValue input;

	private Set<String> usedBases;

	private String primaryBase;

	private boolean statistical;

	@SuppressWarnings("unused")
	private ComputationValue() {
		this.usedBases = ImmutableSet.of();
		this.statistical = false;
	}

	public ComputationValue(final ComputationInputValue input, final Set<? extends String> usedBases,
			final String primaryBase, final boolean statistical) {
		checkNotNull(input);
		checkNotNull(usedBases);
		checkNotNull(primaryBase);
		checkNotNull(statistical);

		this.input = input;
		this.usedBases = ImmutableSet.copyOf(usedBases);
		this.primaryBase = primaryBase;
		this.statistical = statistical;
	}

	@XmlElement
	public Set<String> getUsedBases() {
		return this.usedBases;
	}

	@XmlElement
	@Nullable
	public ComputationInputValue getInput() {
		return this.input;
	}

	public void setInput(ComputationInputValue input) {
		Preconditions.checkNotNull(input);

		this.input = input;
	}

	@XmlElement
	@Nullable
	public String getPrimaryBase() {
		return this.primaryBase;
	}

	@XmlElement
	@Nullable
	public Boolean isStatistical() {
		return this.statistical;
	}

	public void setStatistical(Boolean statistical) {
		this.statistical = statistical;
	}

	public void setUsedBases(Set<? extends String> usedBases) {
		Preconditions.checkNotNull(usedBases);

		this.usedBases = ImmutableSet.copyOf(usedBases);
	}

	public void setPrimaryBase(String primaryBase) {
		Preconditions.checkNotNull(input);

		this.primaryBase = primaryBase;
	}

	@Override
	public String toString() {
		return "ComputationValue [input=" + input + ", usedBases=" + usedBases + ", primaryBase=" + primaryBase
				+ ", statistical=" + statistical + "]";
	}
}
