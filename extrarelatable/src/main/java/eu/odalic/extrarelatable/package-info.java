/**
 * <p>
 * ExtraRelaTable algorithm applies bottom-up approach to assignment of
 * properties to numeric table columns. These properties identify the relations
 * formed between them and the subject of the table.
 * </p>
 * 
 * <p>
 * The algorithm is exposed through public REST API at
 * http://localhost:8080/extrarelatable/, supposed that the packaged WAR is
 * deployed in local servlet container. ERT may optionally use available running
 * instance of Odalic STI tool to provide additional context, which increases
 * the effectivenes of the algorithm.
 * </p>
 * 
 * <p>
 * Alongside the main application, a small experimental framework is included,
 * implemented as a set of JUnit tests, which allow to thoroughly evaluate the
 * algorithm on various supported datasets.
 * </p>
 * 
 * <p>
 * The project is documented in the accompanying thesis Discovering and Creating
 * Relations among CSV Columns using Linked Data Knowledge Bases.
 * </p>
 */
package eu.odalic.extrarelatable;
