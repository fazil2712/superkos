# Graph Report - superkos  (2026-06-01)

## Corpus Check
- 41 files · ~26,692 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 366 nodes · 537 edges · 26 communities (10 shown, 16 thin omitted)
- Extraction: 67% EXTRACTED · 33% INFERRED · 0% AMBIGUOUS · INFERRED: 178 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `750a0ffa`
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
- [[_COMMUNITY_Community 22|Community 22]]
- [[_COMMUNITY_Community 23|Community 23]]

## God Nodes (most connected - your core abstractions)
1. `Hunian` - 31 edges
2. `User` - 26 edges
3. `RoommateSurvey` - 25 edges
4. `LaporanReview` - 21 edges
5. `MatchResult` - 18 edges
6. `Reservasi` - 18 edges
7. `RoommateRequest` - 16 edges
8. `WebController` - 15 edges
9. `ChatRoom` - 15 edges
10. `PencariHunian` - 15 edges

## Surprising Connections (you probably didn't know these)
- `Admin` --extends--> `User`  [EXTRACTED]
  D:/WORK/TUBES/SUPERKOS/src/main/java/com/superkos/app/model/Admin.java →   _Bridges community 13 → community 5_
- `PencariHunian` --extends--> `User`  [EXTRACTED]
  D:/WORK/TUBES/SUPERKOS/src/main/java/com/superkos/app/model/PencariHunian.java →   _Bridges community 5 → community 3_

## Communities (26 total, 16 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.08
Nodes (3): DummyDataLoader, PemilikController, Hunian

### Community 2 - "Community 2"
Cohesion: 0.05
Nodes (4): ChatController, ChatRoom, Message, RoommateRequest

### Community 3 - "Community 3"
Cohesion: 0.11
Nodes (3): WebController, PencariHunian, HunianRepository

### Community 5 - "Community 5"
Cohesion: 0.07
Nodes (8): GlobalModelAdvice, RoommateMatchController, PemilikProperti, ChatRoomRepository, MessageRepository, RoommateRequestRepository, UserRepository, User

### Community 8 - "Community 8"
Cohesion: 0.25
Nodes (3): ISearchFilter, ISortStrategy, SearchEngine

### Community 23 - "Community 23"
Cohesion: 0.07
Nodes (3): ReservasiController, Reservasi, ReservasiRepository

## Knowledge Gaps
- **4 isolated node(s):** `LaporanReviewRepository`, `RoommateSurveyRepository`, `java.configuration.updateBuildConfiguration`, `java.compile.nullAnalysis.mode`
  These have ≤1 connection - possible missing edges or undocumented components.
- **16 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `PencariHunian` connect `Community 3` to `Community 2`, `Community 13`, `Community 5`?**
  _High betweenness centrality (0.160) - this node is a cross-community bridge._
- **Why does `User` connect `Community 1` to `Community 3`, `Community 5`?**
  _High betweenness centrality (0.107) - this node is a cross-community bridge._
- **Why does `Hunian` connect `Community 0` to `Community 3`, `Community 23`?**
  _High betweenness centrality (0.096) - this node is a cross-community bridge._
- **What connects `LaporanReviewRepository`, `RoommateSurveyRepository`, `java.configuration.updateBuildConfiguration` to the rest of the system?**
  _4 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.08181818181818182 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.08021390374331551 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.05084745762711865 - nodes in this community are weakly interconnected._