/********************************************************************************
 * The contents of this file are subject to the GNU General Public License      *
 * (GPL) Version 2 or later (the "License"); you may not use this file except   *
 * in compliance with the License. You may obtain a copy of the License at      *
 * http://www.gnu.org/copyleft/gpl.html                                         *
 *                                                                              *
 * Software distributed under the License is distributed on an "AS IS" basis,   *
 * without warranty of any kind, either expressed or implied. See the License   *
 * for the specific language governing rights and limitations under the         *
 * License.                                                                     *
 *                                                                              *
 * This file was originally developed as part of the software suite that        *
 * supports the book "The Elements of Computing Systems" by Nisan and Schocken, *
 * MIT Press 2005. If you modify the contents of this file, please document and *
 * mark your changes clearly, for the benefit of others.                        *
 ********************************************************************************/

package Hack.Utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * A directed graph that holds Objects as its nodes, and supports the following
 * operations: - Checks if a path exists between to nodes. - Checks if there is
 * a circle in the graph. - Creates a topological sort of the graph starting
 * from a certain node.
 */
public class Graph {

	// The graph
	private HashMap<Object, HashSet<Object>> m_graph;

	// true if the graph has a circle
	private boolean m_hasCircle;

	/**
	 * Constructs a new empty Graph.
	 */
	public Graph() {
		m_graph = new HashMap<Object, HashSet<Object>>();
	}

	/**
	 * Adds an edge between the source and target objects. If the source or
	 * target objects don't exist yet in the graph, they will be added
	 * automatically. If the edge aleardy exists, nothing will happen.
	 */
	public void addEdge(Object source, Object target) {
		checkExistence(source);
		checkExistence(target);

		Set<Object> edgeSet = (Set<Object>) m_graph.get(source);
		edgeSet.add(target);
	}

	// Checks whether the given object exists in the graph. If not, creates it.
	private void checkExistence(Object object) {
		if (!m_graph.keySet().contains(object)) {
			HashSet<Object> edgeSet = new HashSet<Object>();
			m_graph.put(object, edgeSet);
		}
	}

	// Finds recursively using the DFS algorithm if there is a path from the
	// source to destination.
	private boolean doPathExists(Object source, Object destination, Set<Object> marked) {
		boolean pathFound = false;
		marked.add(source);
		Set<Object> edgeSet = (Set<Object>) m_graph.get(source);
		if (edgeSet != null) {
			Iterator<Object> edgeIter = edgeSet.iterator();
			while (edgeIter.hasNext() && !pathFound) {
				Object currentNode = edgeIter.next();
				pathFound = currentNode.equals(destination);
				if (!pathFound && !marked.contains(currentNode)) {
					pathFound = doPathExists(currentNode, destination, marked);
				}
			}
		}

		return pathFound;
	}

	// Runs the topological sort on the given node. This will run recursively
	// on all edges from the given node to non marked nodes. In the end, the
	// given node will be added to the given nodes vector.
	private void doTopologicalSort(Object node, Vector<Object> nodes, Set<Object> marked, Set<Object> processed) {
		marked.add(node);
		processed.add(node);
		Set<Object> edgeSet = (Set<Object>) m_graph.get(node);
		if (edgeSet != null) {
			Iterator<Object> edgeIter = edgeSet.iterator();
			while (edgeIter.hasNext()) {
				Object currentNode = edgeIter.next();

				// check circle
				if (processed.contains(currentNode)) {
					m_hasCircle = true;
				}

				if (!marked.contains(currentNode)) {
					doTopologicalSort(currentNode, nodes, marked, processed);
				}
			}
		}
		processed.remove(node);
		nodes.addElement(node);
	}

	/**
	 * Returns true if the graph has a circle.
	 */
	public boolean hasCircle() {
		return m_hasCircle;
	}

	/**
	 * Returns true if the graph is empty.
	 */
	public boolean isEmpty() {
		return m_graph.keySet().isEmpty();
	}

	/**
	 * Returns true if there is a path from the given source node to the given
	 * destination node.
	 */
	public boolean pathExists(Object source, Object destination) {
		Set<Object> marked = new HashSet<Object>();
		return doPathExists(source, destination, marked);
	}

	/**
	 * Returns the objects (nodes) of this graph sorted in a topological order,
	 * starting from the given object. Sets the 'containsCircle' property if a
	 * circle is detected in the graph.
	 */
	public Object[] topologicalSort(Object start) {
		m_hasCircle = false;
		Set<Object> marked = new HashSet<Object>();
		Set<Object> processed = new HashSet<Object>();
		Vector<Object> nodes = new Vector<Object>();
		doTopologicalSort(start, nodes, marked, processed);

		// reverse the order received from the DFS algorithm
		Object[] result = new Object[nodes.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = nodes.elementAt(result.length - i - 1);
		}

		return result;
	}
}
