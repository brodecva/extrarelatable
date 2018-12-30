/**
 * <p>
 * REST API for ExtraRelaTable allows to manipulate with present background
 * knowledge graphs, which are used to model the learned referential files as
 * well as later inputs. The graphs can be queried in order to annotate tables
 * either parsed or in their native CSV form. The annotations consist of
 * properties identifying the nature of the relations between the subjects of
 * the tables and the present numeric columns. Other relevant information from
 * an algorithm run is included to allows scoring and integration to results of
 * general semantic table interpretation.
 * </p>
 * 
 * <p>
 * The API also allows to search for individual properties present in the graphs
 * in order to facilitate cooperation with other, possibly super-ordinate
 * knowledge bases.
 * </p>
 * 
 * <p>
 * One of the key aspects of the used mapping to the exchanged JSON values is
 * usage of a wrapper around the returned objects, which follows this structure:
 * </p>
 * 
 * <pre>{ status: 200, type: "DATA", payload: { ... }, stamp "15434" }</pre>
 * 
 * <p>
 * The status is the HTTP status code of the response. Type is either DATA, when
 * the payload contains an object of the type specified, or MESSAGE when the
 * payload contains message object. The stamp is a string which was sent by the
 * client in the request as its query parameter of the same name (it can be used
 * to check order of the requests).
 * </p>
 * 
 */
package eu.odalic.extrarelatable.api.rest;
