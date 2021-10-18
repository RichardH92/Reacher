# Reacher
Reacher is a library that supports efficient reachability queries on directed acyclic graphs with a single connected component.

## Supported Queries
* `getAncestors(node)`: retrieves the ancestors for a given node
* `getDescendants(node)`: retrieves the descendants for a given node
* `doesPathExist(from, to)`: determines whether a path exists from one node to another

## Development
### Setting up the Dev Environment
1. [Install Java](https://openjdk.java.net/install/)
2. [Install Gradle](https://gradle.org/install/)

### Building
`gradle build`
### Running Unit Tests
`gradle test`
### Running Benchmarks
`gradle jmh`