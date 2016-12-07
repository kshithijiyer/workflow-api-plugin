/*
 * The MIT License
 *
 * Copyright (c) 2016, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.workflow.graphanalysis;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import hudson.model.Action;
import org.jenkinsci.plugins.workflow.graph.BlockStartNode;
import org.jenkinsci.plugins.workflow.graph.FlowNode;

import javax.annotation.Nonnull;

/**
 * Library of common functionality when analyzing/walking flow graphs
 * @author Sam Van Oort
 */
public final class FlowScanningUtils {

    /** Prevent instantiation */
    private FlowScanningUtils() {}

    /**
     * Create a predicate that will match on all FlowNodes having a specific action present
     * @param actionClass Action class to look for
     * @return Predicate that will match when FlowNode has the action given
     */
    @Nonnull
    public static  Predicate<FlowNode> hasActionPredicate(@Nonnull final Class<? extends Action> actionClass) {
        return new Predicate<FlowNode>() {
            @Override
            public boolean apply(FlowNode input) {
                return (input != null && input.getAction(actionClass) != null);
            }
        };
    }

    // Default predicates, which may be used for common conditions
    public static final Predicate<FlowNode> MATCH_BLOCK_START = (Predicate)Predicates.instanceOf(BlockStartNode.class);

    /**
     * Returns all {@link BlockStartNode}s enclosing the given FlowNode, starting from the inside out.
     * This is useful if we want to obtain information about its scope, such as the workspace, parallel branch, or label.
     * Warning: while this is efficient for one node, batch operations are far more efficient when handling many nodes.
     * @param f {@link FlowNode} to start from.
     * @return Iterator that returns all enclosing BlockStartNodes from the inside out.
     */
    @Nonnull
    public static Filterator<FlowNode> fetchEnclosingBlocks(@Nonnull FlowNode f) {
        LinearBlockHoppingScanner scanner = new LinearBlockHoppingScanner();
        scanner.setup(f);
        return scanner.filter(MATCH_BLOCK_START);
    }
}
