package pl.tfij.brightcache.fixture

import groovy.transform.CompileStatic

@CompileStatic
class DummyUserProviderWithCount {
    private final Map<String, Integer> counters = new HashMap<>()

    User randomUser(String id) {
        increaseCounter(id)
        return new User(id, "sampleFirstName${UUID.randomUUID().toString()}")
    }

    int counter(String id) {
        return counters[id]
    }

    private void increaseCounter(String id) {
        int counter = counters.getOrDefault(id, 0)
        counters[id] = counter + 1
    }

    void reset() {
        counters.removeAll { true }
    }
}
