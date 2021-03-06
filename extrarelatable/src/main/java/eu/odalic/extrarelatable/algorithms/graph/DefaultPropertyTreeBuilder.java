package eu.odalic.extrarelatable.algorithms.graph;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import eu.odalic.extrarelatable.algorithms.subcontext.SubcontextCompiler;
import eu.odalic.extrarelatable.algorithms.subcontext.SubcontextMatcher;
import eu.odalic.extrarelatable.model.bag.Attribute;
import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Context;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.NumberLikeValue;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.graph.PropertyTree.CommonNode;
import eu.odalic.extrarelatable.model.graph.PropertyTree.RootNode;
import eu.odalic.extrarelatable.model.graph.PropertyTree.SharedPairNode;
import eu.odalic.extrarelatable.model.subcontext.Partition;
import eu.odalic.extrarelatable.model.subcontext.Subcontext;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;
import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;
import eu.odalic.extrarelatable.util.UuidGenerator;

/**
 * Default implementation of {@link PropertyTreeBuilder}.
 * 
 * @author Václav Brodec
 *
 */
@Component
public class DefaultPropertyTreeBuilder implements PropertyTreeBuilder {

	private static final Set<URI> STOP_ENTITIES = ImmutableSet
			.of(URI.create("http://www.w3.org/2000/01/rdf-schema#label"));

	public final int MINIMUM_PARTITION_SIZE = 2;

	private final SubcontextCompiler subcontextCompiler;
	private final SubcontextMatcher subcontextMatcher;
	private final UuidGenerator uuidGenerator;

	private final double minimumPartitionRelativeSize;
	private final double maximumPartitionRelativeSize;

	/**
	 * Construct the builder.
	 * 
	 * @param subcontextCompiler
	 *            used to construct candidate collections of partitions during the
	 *            building process
	 * @param subcontextMatcher
	 *            used to select the best from the candidate sub-contexts
	 * @param uuidGenerator
	 *            generator of UUIDs used to identify elements of the tree
	 * @param minimumPartitionRelativeSize
	 *            determines the minimum relative size (in ratio to the complete
	 *            column) of a partition of a numeric column where the recursive
	 *            building process stops, if no larger is available
	 * @param maximumPartitionRelativeSize
	 *            determines the maximum relative size (in ratio to the complete
	 *            column) of a partition of a numeric column where the recursive
	 *            building process stops, if no smaller is available
	 */
	@Autowired
	public DefaultPropertyTreeBuilder(final SubcontextCompiler subcontextCompiler,
			final SubcontextMatcher subcontextMatcher, @Qualifier("UuidGenerator") final UuidGenerator uuidGenerator,
			@Value("${eu.odalic.extrarelatable.minimumPartitionRelativeSize:0.01}") final double minimumPartitionRelativeSize,
			@Value("${eu.odalic.extrarelatable.maximumPartitionRelativeSize:0.99}") final double maximumPartitionRelativeSize) {
		checkNotNull(subcontextCompiler);
		checkNotNull(subcontextMatcher);
		checkNotNull(uuidGenerator);
		checkArgument(minimumPartitionRelativeSize > 0);
		checkArgument(maximumPartitionRelativeSize < 1);
		checkArgument(minimumPartitionRelativeSize <= maximumPartitionRelativeSize);

		this.subcontextCompiler = subcontextCompiler;
		this.subcontextMatcher = subcontextMatcher;
		this.uuidGenerator = uuidGenerator;
		this.minimumPartitionRelativeSize = minimumPartitionRelativeSize;
		this.maximumPartitionRelativeSize = maximumPartitionRelativeSize;
	}

	@Override
	public PropertyTree build(final SlicedTable slicedTable, final int columnIndex) {
		return build(slicedTable, columnIndex, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(),
				ImmutableMap.of(), false, false);
	}

	private Set<CommonNode> buildChildren(final Partition partition, final Set<Integer> availableContextColumnIndices,
			final TypedTable table) {
		final Set<Subcontext> subcontexts = subcontextCompiler.compile(partition, availableContextColumnIndices, table,
				minimumPartitionRelativeSize, maximumPartitionRelativeSize, MINIMUM_PARTITION_SIZE);
		if (subcontexts.isEmpty()) {
			return ImmutableSet.of();
		}

		final ImmutableSet.Builder<CommonNode> children = ImmutableSet.builder();

		final Subcontext winningSubcontext = subcontextMatcher.match(subcontexts, partition,
				minimumPartitionRelativeSize, maximumPartitionRelativeSize, MINIMUM_PARTITION_SIZE);
		if (winningSubcontext == null) {
			return ImmutableSet.of();
		}

		final Attribute subattribute = winningSubcontext.getAttribute();
		final int parentalPartitionSize = partition.size();

		for (final Entry<eu.odalic.extrarelatable.model.bag.Value, Partition> partitionEntry : winningSubcontext
				.getPartitions().entrySet()) {
			final Partition subpartition = partitionEntry.getValue();
			final int subpartitionSize = subpartition.size();
			if (subpartitionSize < minimumPartitionRelativeSize * parentalPartitionSize) {
				continue;
			}
			if (subpartitionSize > maximumPartitionRelativeSize * parentalPartitionSize) {
				continue;
			}
			if (subpartitionSize < MINIMUM_PARTITION_SIZE) {
				continue;
			}

			final int usedContextColumnIndex = winningSubcontext.getColumnIndex();

			final Set<CommonNode> subchildren = buildChildren(subpartition,
					Sets.difference(availableContextColumnIndices, ImmutableSet.of(usedContextColumnIndex)), table);

			final eu.odalic.extrarelatable.model.bag.Value subvalue = partitionEntry.getKey();
			final SharedPairNode subtree = new SharedPairNode(
					new AttributeValuePair(uuidGenerator.generate(), subattribute, subvalue),
					ImmutableMultiset.copyOf(subpartition.getValues()));
			subtree.addChildren(subchildren);

			children.add(subtree);
		}

		return children.build();
	}

	@Override
	public PropertyTree build(SlicedTable slicedTable, int columnIndex,
			Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			Map<? extends Integer, ? extends DeclaredEntity> declaredClasses,
			Map<? extends Integer, ? extends DeclaredEntity> contextProperties,
			Map<? extends Integer, ? extends DeclaredEntity> contextClasses, boolean onlyWithProperties,
			boolean onlyDeclaredAsContext) {
		checkNotNull(slicedTable);
		checkArgument(columnIndex >= 0);
		checkArgument(columnIndex < slicedTable.getWidth());

		final List<eu.odalic.extrarelatable.model.bag.Value> numericColumn = slicedTable.getDataColumns()
				.get(columnIndex);

		final Partition partition = new Partition(numericColumn.stream().filter(e -> e.isNumberLike())
				.map(e -> (NumberLikeValue) e).collect(ImmutableList.toImmutableList()));
		if (partition.size() < MINIMUM_PARTITION_SIZE) {
			return null;
		}

		final Label label = slicedTable.getHeaders().get(columnIndex);

		final RootNode rootNode = new RootNode(label, ImmutableMultiset.copyOf(partition.getValues()));

		final Set<Integer> availableContextColumnIndices = slicedTable.getContextColumns().keySet();
		final Set<CommonNode> children = buildChildren(partition, availableContextColumnIndices, slicedTable);
		rootNode.addChildren(children);

		final DeclaredEntity declaredProperty = declaredProperties.get(columnIndex);
		if (declaredProperty == null) {
			if (onlyWithProperties) {
				return null;
			}
		}

		final Context context = new Context(slicedTable.getHeaders(), slicedTable.getMetadata().getAuthor(),
				slicedTable.getMetadata().getTitle(), declaredProperty,
				onlyDeclaredAsContext ? getMeaningfulEntities(declaredProperties)
						: getMeaningfulEntities(contextProperties),
				onlyDeclaredAsContext ? ImmutableMap.of() : contextClasses, columnIndex, availableContextColumnIndices);

		final PropertyTree tree = new PropertyTree(rootNode, context);
		rootNode.setPropertyTree(tree);

		return tree;
	}

	private static Map<Integer, DeclaredEntity> getMeaningfulEntities(
			final Map<? extends Integer, ? extends DeclaredEntity> declaredProperties) {
		return declaredProperties.entrySet().stream().filter(e -> !STOP_ENTITIES.contains(e.getValue().getUri()))
				.collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> e.getValue()));
	}
}
