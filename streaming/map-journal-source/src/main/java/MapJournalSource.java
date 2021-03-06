/*
 * Copyright (c) 2008-2017, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.hazelcast.core.EntryEventType;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Pipeline;
import com.hazelcast.jet.Sinks;
import com.hazelcast.jet.Sources;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.jet.stream.IStreamMap;
import com.hazelcast.map.journal.EventJournalMapEvent;

import java.util.concurrent.TimeUnit;

/**
 * A pipeline which streams events from an IMap. The
 * values for new entries are extracted and then written
 * to a local IList in the Jet cluster.
 */
public class MapJournalSource {

    private static final String MAP_NAME = "map";
    private static final String SINK_NAME = "list";

    public static void main(String[] args) throws Exception {
        System.setProperty("hazelcast.logging.type", "log4j");
        JetConfig jetConfig = getJetConfig();
        JetInstance jet = Jet.newJetInstance(jetConfig);
        Jet.newJetInstance(jetConfig);

        try {
            Pipeline p = Pipeline.create();
            p.drawFrom(Sources.<Integer, Integer, Integer>mapJournal(MAP_NAME, e -> e.getType() == EntryEventType.ADDED,
                    EventJournalMapEvent::getNewValue, true))
             .drainTo(Sinks.list(SINK_NAME));

            jet.newJob(p);

            IStreamMap<Integer, Integer> map = jet.getMap(MAP_NAME);
            for (int i = 0; i < 1000; i++) {
                map.set(i, i);
            }
            TimeUnit.SECONDS.sleep(3);
            System.out.println("Read " + jet.getList(SINK_NAME).size() + " entries from map journal.");
        } finally {
            Jet.shutdownAll();
        }

    }

    private static JetConfig getJetConfig() {
        JetConfig cfg = new JetConfig();
        // Add an event journal config for map which has custom capacity of 1000 (default 10_000)
        // and time to live seconds as 10 seconds (default 0 which means infinite)
        cfg.getHazelcastConfig()
           .getMapEventJournalConfig(MAP_NAME)
           .setEnabled(true)
           .setCapacity(1000)
           .setTimeToLiveSeconds(10);
        return cfg;
    }

}
