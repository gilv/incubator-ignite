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

package org.apache.ignite.examples.streaming.socket;

import org.apache.ignite.*;
import org.apache.ignite.examples.*;
import org.apache.ignite.examples.streaming.numbers.*;
import org.apache.ignite.lang.*;
import org.apache.ignite.stream.*;
import org.apache.ignite.stream.adapters.*;
import org.apache.ignite.stream.socket.*;

import javax.cache.processor.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Stream random numbers into the streaming cache using {@link IgniteSocketStreamer}.
 * <p>
 * Example illustrates usage of TCP socket streamer in case of non-Java clients. In this example client streams
 * zero-terminated strings.
 * <p>
 * To start the example, you should:
 * <ul>
 *      <li>Start a few nodes using {@link ExampleNodeStartup} or by starting remote nodes as specified below.</li>
 *      <li>Start streaming using {@link ZStringsSocketStreamerExample}.</li>
 *      <li>Start querying popular numbers using {@link QueryPopularNumbers}.</li>
 * </ul>
 * <p>
 * You should start remote nodes by running {@link ExampleNodeStartup} in another JVM.
 */
public class ZStringsSocketStreamerExample {
    /** Random number generator. */
    private static final Random RAND = new Random();

    /** Range within which to generate numbers. */
    private static final int RANGE = 1000;

    /** Port. */
    private static final int PORT = 5555;

    /** Delimiter. */
    private static final byte[] DELIM = new byte[] {0};

    /**
     * @param args Args.
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        // Mark this cluster member as client.
        Ignition.setClientMode(true);

        try (Ignite ignite = Ignition.start("examples/config/example-ignite.xml")) {
            if (!ExamplesUtils.hasServerNodes(ignite))
                return;

            // The cache is configured with sliding window holding 1 second of the streaming data.
            IgniteCache<Integer, Long> stmCache = ignite.getOrCreateCache(CacheConfig.randomNumbersCache());

            try (IgniteDataStreamer<Integer, Long> stmr = ignite.dataStreamer(stmCache.getName())) {
                // Allow data updates.
                stmr.allowOverwrite(true);

                // Configure data transformation to count instances of the same word.
                stmr.receiver(new StreamTransformer<Integer, Long>() {
                    @Override public Object process(MutableEntry<Integer, Long> e, Object... objects)
                        throws EntryProcessorException {
                        Long val = e.getValue();

                        e.setValue(val == null ? 1L : val + 1);

                        return null;
                    }
                });

                InetAddress addr = InetAddress.getLocalHost();

                IgniteSocketStreamer<String, Integer, Long> sockStmr = new IgniteSocketStreamer<>();

                sockStmr.setAddr(addr);

                sockStmr.setPort(PORT);

                sockStmr.setDelimiter(DELIM);

                sockStmr.setStreamer(stmr);

                // Converter from zero-terminated string to Java strings.
                sockStmr.setConverter(new SocketMessageConverter<String>() {
                    @Override public String convert(byte[] msg) {
                        try {
                            return new String(msg, "ASCII");
                        }
                        catch (UnsupportedEncodingException e) {
                            throw new IgniteException(e);
                        }
                    }
                });

                sockStmr.setTupleExtractor(new StreamTupleExtractor<String, Integer, Long>() {
                    @Override public Map.Entry<Integer, Long> extract(String input) {
                        String[] pair = input.split("=");
                        return new IgniteBiTuple<>(Integer.parseInt(pair[0]), Long.parseLong(pair[1]));
                    }
                });

                sockStmr.start();

                sendData(addr, PORT);
            }
        }
    }

    /**
     * @param addr Address.
     * @param port Port.
     */
    private static void sendData(InetAddress addr, int port) throws IOException, InterruptedException {
        try (Socket sock = new Socket(addr, port);
             OutputStream oos = new BufferedOutputStream(sock.getOutputStream())) {

            while (true) {
                int key = RAND.nextInt(RANGE);

                String str = key + "=1";

                byte[] arr = str.getBytes("ASCII");

                oos.write(arr);
                oos.write(DELIM);
            }
        }
    }
}
