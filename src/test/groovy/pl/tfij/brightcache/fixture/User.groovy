package pl.tfij.brightcache.fixture

import groovy.transform.CompileStatic

@CompileStatic
class User {
    private final String id
    private final String firstName

    User(String id, String firstName) {
        this.id = id
        this.firstName = firstName
    }

    String getId() {
        return id
    }

    String getFirstName() {
        return firstName
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        User user = (User) o

        if (firstName != user.firstName) return false
        if (id != user.id) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0)
        return result
    }
}
