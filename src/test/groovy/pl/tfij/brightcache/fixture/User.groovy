package pl.tfij.brightcache.fixture

import groovy.transform.CompileStatic

@CompileStatic
class User {
    private final String firstName
    private final String lastName

    User(String firstName, String lastName) {
        this.firstName = firstName
        this.lastName = lastName
    }

    String getFirstName() {
        return firstName
    }

    String getLastName() {
        return lastName
    }
}
