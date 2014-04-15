// <reference path="../reference.ts"/>

interface EntityIdGenerator {

  next(): string
}

class NodeUUIDGenerator implements EntityIdGenerator {

  next(): string {
    return uuid.v4()
  }
}
