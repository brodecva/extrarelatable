package eu.odalic.extrarelatable.model.annotation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

import javax.annotation.concurrent.Immutable;

import eu.odalic.extrarelatable.model.graph.PropertyTree.Node;

@Immutable
public final class MeasuredNode implements Comparable<MeasuredNode> {
	private final UUID uuid;
	private final Node node;
	private final double distance;
	
	public MeasuredNode(final UUID uuid, final Node node, final double distance) {
		checkNotNull(uuid);
		checkNotNull(node);
		checkArgument(distance >= 0);
		
		this.uuid = uuid;
		this.node = node;
		this.distance = distance;
	}
	
	public UUID getUuid() {
		return uuid;
	}

	public double getDistance() {
		return distance;
	}

	public Node getNode() {
		return node;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(distance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MeasuredNode other = (MeasuredNode) obj;
		if (Double.doubleToLongBits(distance) != Double.doubleToLongBits(other.distance)) {
			return false;
		}
		if (!uuid.equals(other.uuid)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final MeasuredNode other) {
		final int distanceComparison = Double.compare(distance, other.distance);
		
		if (distanceComparison == 0) {
			return uuid.compareTo(other.uuid);
		} else {
			return distanceComparison;
		}
	}
	
	@Override
	public String toString() {
		return "MeasuredNode [uuid=" + uuid + ", node=" + node + ", distance=" + distance + "]";
	}
}
