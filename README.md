# Introduction

This is an attempt to implement Java Cache Specification (JSR-107) using as underlying storage the [etcd](https://coreos.com/etcd/) key-value store.

Another goal, if not possible to create a fully functional implementation, is learn deeper about etcd and also about JSR-107 and its [TCK](https://github.com/jsr107/jsr107tck).

The [etcd-java](https://github.com/IBM/etcd-java) client is used to access the etcd cluster.

// TODO
- Implement all the specs
- Improve code design
- Find a way to test it
