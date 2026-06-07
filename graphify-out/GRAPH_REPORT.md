# Graph Report - SUPERKOS  (2026-06-07)

## Corpus Check
- 45 files · ~31,951 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 407 nodes · 638 edges · 29 communities (11 shown, 18 thin omitted)
- Extraction: 65% EXTRACTED · 35% INFERRED · 0% AMBIGUOUS · INFERRED: 224 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `f941c65b`
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
- [[_COMMUNITY_Community 26|Community 26]]

## God Nodes (most connected - your core abstractions)
1. `Hunian` - 32 edges
2. `User` - 27 edges
3. `RoommateSurvey` - 25 edges
4. `ChatRoom` - 23 edges
5. `Reservasi` - 23 edges
6. `LaporanReview` - 21 edges
7. `MatchResult` - 18 edges
8. `WebController` - 16 edges
9. `PencariHunian` - 16 edges
10. `RoommateRequest` - 16 edges

## Surprising Connections (you probably didn't know these)
- `Admin` --extends--> `User`  [EXTRACTED]
  src/main/java/com/superkos/app/model/Admin.java →   _Bridges community 13 → community 1_
- `PemilikProperti` --extends--> `User`  [EXTRACTED]
  src/main/java/com/superkos/app/model/PemilikProperti.java →   _Bridges community 1 → community 3_

## Communities (29 total, 18 thin omitted)

### Community 1 - "Community 1"
Cohesion: 0.05
Nodes (7): AuthController, GlobalModelAdvice, RoommateMatchController, User, ChatRoomRepository, UserRepository, User

### Community 3 - "Community 3"
Cohesion: 0.08
Nodes (5): WebController, PemilikProperti, PencariHunian, HunianRepository, LaporanReviewRepository

### Community 4 - "Community 4"
Cohesion: 0.09
Nodes (3): RoommateInviteController, RoommateSurvey, PencariHunianRepository

### Community 5 - "Community 5"
Cohesion: 0.08
Nodes (4): ChatController, RoommateRequest, MessageRepository, RoommateRequestRepository

### Community 8 - "Community 8"
Cohesion: 0.25
Nodes (3): ISearchFilter, ISortStrategy, SearchEngine

## Knowledge Gaps
- **3 isolated node(s):** `RoommateSurveyRepository`, `java.configuration.updateBuildConfiguration`, `java.compile.nullAnalysis.mode`
  These have ≤1 connection - possible missing edges or undocumented components.
- **18 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `PencariHunian` connect `Community 3` to `Community 1`, `Community 2`, `Community 4`, `Community 5`, `Community 13`?**
  _High betweenness centrality (0.141) - this node is a cross-community bridge._
- **Why does `User` connect `Community 1` to `Community 5`, `Community 6`?**
  _High betweenness centrality (0.119) - this node is a cross-community bridge._
- **Why does `Hunian` connect `Community 0` to `Community 2`, `Community 3`, `Community 6`, `Community 23`?**
  _High betweenness centrality (0.095) - this node is a cross-community bridge._
- **What connects `RoommateSurveyRepository`, `java.configuration.updateBuildConfiguration`, `java.compile.nullAnalysis.mode` to the rest of the system?**
  _3 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.09682539682539683 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.053877551020408164 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.060129509713228495 - nodes in this community are weakly interconnected._