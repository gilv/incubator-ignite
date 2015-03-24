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

package org.apache.ignite.internal.processors.datastreamer;

import org.apache.ignite.configuration.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.util.tostring.*;
import org.apache.ignite.internal.util.typedef.internal.*;
import org.apache.ignite.lang.*;
import org.apache.ignite.plugin.extensions.communication.*;
import org.jetbrains.annotations.*;

import java.nio.*;
import java.util.*;

/**
 *
 */
public class DataStreamerRequest implements Message {
    /** */
    private static final long serialVersionUID = 0L;

    /** */
    private long reqId;

    /** */
    private ByteBuffer resTopicBytes;

    /** Cache name. */
    private String cacheName;

    /** */
    private ByteBuffer updaterBytes;

    /** Entries to update. */
    @GridDirectCollection(DataStreamerEntry.class)
    private Collection<DataStreamerEntry> entries;

    /** {@code True} to ignore deployment ownership. */
    private boolean ignoreDepOwnership;

    /** */
    private boolean skipStore;

    /** */
    private DeploymentMode depMode;

    /** */
    private String sampleClsName;

    /** */
    private String userVer;

    /** Node class loader participants. */
    @GridToStringInclude
    @GridDirectMap(keyType = UUID.class, valueType = IgniteUuid.class)
    private Map<UUID, IgniteUuid> ldrParticipants;

    /** */
    private IgniteUuid clsLdrId;

    /** */
    private boolean forceLocDep;

    /**
     * {@code Externalizable} support.
     */
    public DataStreamerRequest() {
        // No-op.
    }

    /**
     * @param reqId Request ID.
     * @param resTopicBytes Response topic.
     * @param cacheName Cache name.
     * @param updaterBytes Cache receiver.
     * @param entries Entries to put.
     * @param ignoreDepOwnership Ignore ownership.
     * @param skipStore Skip store flag.
     * @param depMode Deployment mode.
     * @param sampleClsName Sample class name.
     * @param userVer User version.
     * @param ldrParticipants Loader participants.
     * @param clsLdrId Class loader ID.
     * @param forceLocDep Force local deployment.
     */
    public DataStreamerRequest(long reqId,
        ByteBuffer resTopicBytes,
        @Nullable String cacheName,
        ByteBuffer updaterBytes,
        Collection<DataStreamerEntry> entries,
        boolean ignoreDepOwnership,
        boolean skipStore,
        DeploymentMode depMode,
        String sampleClsName,
        String userVer,
        Map<UUID, IgniteUuid> ldrParticipants,
        IgniteUuid clsLdrId,
        boolean forceLocDep) {
        this.reqId = reqId;
        this.resTopicBytes = resTopicBytes;
        this.cacheName = cacheName;
        this.updaterBytes = updaterBytes;
        this.entries = entries;
        this.ignoreDepOwnership = ignoreDepOwnership;
        this.skipStore = skipStore;
        this.depMode = depMode;
        this.sampleClsName = sampleClsName;
        this.userVer = userVer;
        this.ldrParticipants = ldrParticipants;
        this.clsLdrId = clsLdrId;
        this.forceLocDep = forceLocDep;
    }

    /**
     * @return Request ID.
     */
    public long requestId() {
        return reqId;
    }

    /**
     * @return Response topic.
     */
    public ByteBuffer responseTopicBytes() {
        return resTopicBytes;
    }

    /**
     * @return Cache name.
     */
    public String cacheName() {
        return cacheName;
    }

    /**
     * @return Updater.
     */
    public ByteBuffer updaterBytes() {
        return updaterBytes;
    }

    /**
     * @return Entries to update.
     */
    public Collection<DataStreamerEntry> entries() {
        return entries;
    }

    /**
     * @return {@code True} to ignore ownership.
     */
    public boolean ignoreDeploymentOwnership() {
        return ignoreDepOwnership;
    }

    /**
     * @return Skip store flag.
     */
    public boolean skipStore() {
        return skipStore;
    }

    /**
     * @return Deployment mode.
     */
    public DeploymentMode deploymentMode() {
        return depMode;
    }

    /**
     * @return Sample class name.
     */
    public String sampleClassName() {
        return sampleClsName;
    }

    /**
     * @return User version.
     */
    public String userVersion() {
        return userVer;
    }

    /**
     * @return Participants.
     */
    public Map<UUID, IgniteUuid> participants() {
        return ldrParticipants;
    }

    /**
     * @return Class loader ID.
     */
    public IgniteUuid classLoaderId() {
        return clsLdrId;
    }

    /**
     * @return {@code True} to force local deployment.
     */
    public boolean forceLocalDeployment() {
        return forceLocDep;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(DataStreamerRequest.class, this);
    }

    /** {@inheritDoc} */
    @Override public boolean writeTo(ByteBuffer buf, MessageWriter writer) {
        writer.setBuffer(buf);

        if (!writer.isHeaderWritten()) {
            if (!writer.writeHeader(directType(), fieldsCount()))
                return false;

            writer.onHeaderWritten();
        }

        switch (writer.state()) {
            case 0:
                if (!writer.writeString("cacheName", cacheName))
                    return false;

                writer.incrementState();

            case 1:
                if (!writer.writeIgniteUuid("clsLdrId", clsLdrId))
                    return false;

                writer.incrementState();

            case 2:
                if (!writer.writeByte("depMode", depMode != null ? (byte)depMode.ordinal() : -1))
                    return false;

                writer.incrementState();

            case 3:
                if (!writer.writeCollection("entries", entries, MessageCollectionItemType.MSG))
                    return false;

                writer.incrementState();

            case 4:
                if (!writer.writeBoolean("forceLocDep", forceLocDep))
                    return false;

                writer.incrementState();

            case 5:
                if (!writer.writeBoolean("ignoreDepOwnership", ignoreDepOwnership))
                    return false;

                writer.incrementState();

            case 6:
                if (!writer.writeMap("ldrParticipants", ldrParticipants, MessageCollectionItemType.UUID, MessageCollectionItemType.IGNITE_UUID))
                    return false;

                writer.incrementState();

            case 7:
                if (!writer.writeLong("reqId", reqId))
                    return false;

                writer.incrementState();

            case 8:
                if (!writer.writeByteBuffer("resTopicBytes", resTopicBytes))
                    return false;

                writer.incrementState();

            case 9:
                if (!writer.writeString("sampleClsName", sampleClsName))
                    return false;

                writer.incrementState();

            case 10:
                if (!writer.writeBoolean("skipStore", skipStore))
                    return false;

                writer.incrementState();

            case 11:
                if (!writer.writeByteBuffer("updaterBytes", updaterBytes))
                    return false;

                writer.incrementState();

            case 12:
                if (!writer.writeString("userVer", userVer))
                    return false;

                writer.incrementState();

        }

        return true;
    }

    /** {@inheritDoc} */
    @Override public boolean readFrom(ByteBuffer buf, MessageReader reader) {
        reader.setBuffer(buf);

        if (!reader.beforeMessageRead())
            return false;

        switch (reader.state()) {
            case 0:
                cacheName = reader.readString("cacheName");

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 1:
                clsLdrId = reader.readIgniteUuid("clsLdrId");

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 2:
                byte depModeOrd;

                depModeOrd = reader.readByte("depMode");

                if (!reader.isLastRead())
                    return false;

                depMode = DeploymentMode.fromOrdinal(depModeOrd);

                reader.incrementState();

            case 3:
                entries = reader.readCollection("entries", MessageCollectionItemType.MSG);

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 4:
                forceLocDep = reader.readBoolean("forceLocDep");

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 5:
                ignoreDepOwnership = reader.readBoolean("ignoreDepOwnership");

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 6:
                ldrParticipants = reader.readMap("ldrParticipants", MessageCollectionItemType.UUID, MessageCollectionItemType.IGNITE_UUID, false);

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 7:
                reqId = reader.readLong("reqId");

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 8:
                resTopicBytes = reader.readByteBuffer("resTopicBytes");

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 9:
                sampleClsName = reader.readString("sampleClsName");

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 10:
                skipStore = reader.readBoolean("skipStore");

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 11:
                updaterBytes = reader.readByteBuffer("updaterBytes");

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 12:
                userVer = reader.readString("userVer");

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

        }

        return true;
    }

    /** {@inheritDoc} */
    @Override public byte directType() {
        return 62;
    }

    /** {@inheritDoc} */
    @Override public byte fieldsCount() {
        return 13;
    }
}
