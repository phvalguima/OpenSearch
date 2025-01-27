/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.cluster.coordination;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.opensearch.cli.Terminal;
import org.opensearch.cli.UserException;
import org.opensearch.cluster.ClusterState;
import org.opensearch.cluster.coordination.OpenSearchNodeCommand;
import org.opensearch.common.collect.Tuple;
import org.opensearch.env.Environment;
import org.opensearch.gateway.PersistedClusterStateService;

import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * Command to list metadata details of the node.
 */
public class ListMetadataCommand extends OpenSearchNodeCommand {

    public ListMetadataCommand() {
        super("List metadata details from the cluster state");
    }

    @Override
    protected void processNodePaths(Terminal terminal, Path[] dataPaths, int nodeLockId, OptionSet options, Environment env)
        throws IOException, UserException {

        final PersistedClusterStateService persistedClusterStateService = createPersistedClusterStateService(env.settings(), dataPaths);

        terminal.println(Terminal.Verbosity.VERBOSE, "Loading cluster state");
        final Tuple<Long, ClusterState> termAndClusterState = loadTermAndClusterState(persistedClusterStateService, env);
        final ClusterState currentClusterState = termAndClusterState.v2();
		terminal.println("Cluster state metadata:");

		final ObjectMapper objectMapper = new ObjectMapper();
		final ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
		terminal.println(objectWriter.writeValueAsString(currentClusterState.metadata()));
		terminal.println(objectWriter.writeValueAsString(currentClusterState));
    }
}