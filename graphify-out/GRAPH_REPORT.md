# Graph Report - SUPERKOS  (2026-05-22)

## Corpus Check
- 36 files · ~21,054 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 281 nodes · 398 edges · 22 communities (8 shown, 14 thin omitted)
- Extraction: 69% EXTRACTED · 31% INFERRED · 0% AMBIGUOUS · INFERRED: 123 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `64c695d5`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 19|Community 19]]

## God Nodes (most connected - your core abstractions)
1. `Hunian` - 30 edges
2. `User` - 26 edges
3. `RoommateSurvey` - 25 edges
4. `MatchResult` - 18 edges
5. `RoommateRequest` - 15 edges
6. `WebController` - 14 edges
7. `ChatRoom` - 14 edges
8. `Message` - 13 edges
9. `PencariHunian` - 12 edges
10. `ChatController` - 10 edges

## Surprising Connections (you probably didn't know these)
- `PemilikProperti` --extends--> `User`  [EXTRACTED]
  src/main/java/com/superkos/app/model/PemilikProperti.java →   _Bridges community 5 → community 0_
- `PencariHunian` --extends--> `User`  [EXTRACTED]
  src/main/java/com/superkos/app/model/PencariHunian.java →   _Bridges community 5 → community 3_

## Communities (22 total, 14 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.06
Nodes (3): DummyDataLoader, Hunian, PemilikProperti

### Community 1 - "Community 1"
Cohesion: 0.07
Nodes (3): AuthController, User, UserRepository

### Community 2 - "Community 2"
Cohesion: 0.08
Nodes (3): ChatController, ChatRoom, RoommateRequest

### Community 3 - "Community 3"
Cohesion: 0.12
Nodes (3): WebController, PencariHunian, HunianRepository

### Community 5 - "Community 5"
Cohesion: 0.08
Nodes (7): GlobalModelAdvice, RoommateMatchController, Admin, ChatRoomRepository, MessageRepository, RoommateRequestRepository, User

### Community 8 - "Community 8"
Cohesion: 0.29
Nodes (3): ISearchFilter, ISortStrategy, SearchEngine

## Knowledge Gaps
- **7 isolated node(s):** `java.configuration.updateBuildConfiguration`, `java.compile.nullAnalysis.mode`, `RoommateSurveyRepository`, `superkos`, `graphify` (+2 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **14 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Hunian` connect `Community 0` to `Community 3`?**
  _High betweenness centrality (0.212) - this node is a cross-community bridge._
- **Why does `User` connect `Community 1` to `Community 3`?**
  _High betweenness centrality (0.138) - this node is a cross-community bridge._
- **Why does `RoommateSurvey` connect `Community 4` to `Community 3`?**
  _High betweenness centrality (0.090) - this node is a cross-community bridge._
- **What connects `java.configuration.updateBuildConfiguration`, `java.compile.nullAnalysis.mode`, `RoommateSurveyRepository` to the rest of the system?**
  _7 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.0620782726045884 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.06685633001422475 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.08021390374331551 - nodes in this community are weakly interconnected._