# Graph Report - SUPERKOS  (2026-05-23)

## Corpus Check
- 39 files · ~24,908 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 316 nodes · 480 edges · 23 communities (8 shown, 15 thin omitted)
- Extraction: 65% EXTRACTED · 35% INFERRED · 0% AMBIGUOUS · INFERRED: 167 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `d04a1766`
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

## God Nodes (most connected - your core abstractions)
1. `Hunian` - 30 edges
2. `User` - 26 edges
3. `RoommateSurvey` - 25 edges
4. `LaporanReview` - 20 edges
5. `MatchResult` - 18 edges
6. `RoommateRequest` - 15 edges
7. `WebController` - 14 edges
8. `ChatRoom` - 14 edges
9. `PencariHunian` - 14 edges
10. `Message` - 13 edges

## Surprising Connections (you probably didn't know these)
- `Admin` --extends--> `User`  [EXTRACTED]
  src/main/java/com/superkos/app/model/Admin.java →   _Bridges community 13 → community 5_
- `PencariHunian` --extends--> `User`  [EXTRACTED]
  src/main/java/com/superkos/app/model/PencariHunian.java →   _Bridges community 5 → community 3_

## Communities (23 total, 15 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.08
Nodes (3): DummyDataLoader, PemilikController, Hunian

### Community 1 - "Community 1"
Cohesion: 0.07
Nodes (3): AuthController, User, UserRepository

### Community 2 - "Community 2"
Cohesion: 0.05
Nodes (4): ChatController, ChatRoom, Message, RoommateRequest

### Community 3 - "Community 3"
Cohesion: 0.12
Nodes (3): WebController, PencariHunian, HunianRepository

### Community 5 - "Community 5"
Cohesion: 0.08
Nodes (7): GlobalModelAdvice, RoommateMatchController, PemilikProperti, ChatRoomRepository, MessageRepository, RoommateRequestRepository, User

### Community 8 - "Community 8"
Cohesion: 0.29
Nodes (3): ISearchFilter, ISortStrategy, SearchEngine

## Knowledge Gaps
- **9 isolated node(s):** `java.configuration.updateBuildConfiguration`, `java.compile.nullAnalysis.mode`, `LaporanReviewRepository`, `PemilikPropertiRepository`, `RoommateSurveyRepository` (+4 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **15 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `PencariHunian` connect `Community 3` to `Community 2`, `Community 13`, `Community 5`?**
  _High betweenness centrality (0.171) - this node is a cross-community bridge._
- **Why does `Hunian` connect `Community 0` to `Community 3`?**
  _High betweenness centrality (0.133) - this node is a cross-community bridge._
- **Why does `User` connect `Community 1` to `Community 3`, `Community 5`?**
  _High betweenness centrality (0.125) - this node is a cross-community bridge._
- **What connects `java.configuration.updateBuildConfiguration`, `java.compile.nullAnalysis.mode`, `LaporanReviewRepository` to the rest of the system?**
  _9 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.08282828282828283 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.07357357357357357 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.05454545454545454 - nodes in this community are weakly interconnected._