# Introduction

This is an attempt to implement Java Cache Specification (JSR-107) using as underlying storage the [etcd](https://coreos.com/etcd/) key-value store.

Another goal, if not possible to create a fully functional implementation, is learn deeper about etcd and also about JSR-107 and its [TCK](https://github.com/jsr107/jsr107tck).

The [jetcd](https://github.com/etcd-io/jetcd) client is used to access the etcd cluster.