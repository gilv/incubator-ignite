/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.spi.discovery.tcp.messages;

import org.apache.ignite.internal.util.typedef.internal.*;

import java.io.*;
import java.util.*;

/**
 * Handshake response.
 */
public class TcpDiscoveryHandshakeResponse extends TcpDiscoveryAbstractMessage {
    /** */
    private static final long serialVersionUID = 0L;

    /** */
    private long order;

    /**
     * Public default no-arg constructor for {@link Externalizable} interface.
     */
    public TcpDiscoveryHandshakeResponse() {
        // No-op.
    }

    /**
     * Constructor.
     *
     * @param creatorNodeId Creator node ID.
     * @param locNodeOrder Local node order.
     */
    public TcpDiscoveryHandshakeResponse(UUID creatorNodeId, long locNodeOrder) {
        super(creatorNodeId);

        order = locNodeOrder;
    }

    /**
     * Gets order of the node sent the response.
     *
     * @return Order of the node sent the response.
     */
    public long order() {
        return order;
    }

    /**
     * Sets order of the node sent the response.
     *
     * @param order Order of the node sent the response.
     */
    public void order(long order) {
        this.order = order;
    }

    /** {@inheritDoc} */
    @Override public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeLong(order);
    }

    /** {@inheritDoc} */
    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        order = in.readLong();
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(TcpDiscoveryHandshakeResponse.class, this, "super", super.toString());
    }
}
